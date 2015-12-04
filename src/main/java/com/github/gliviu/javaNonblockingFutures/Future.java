package com.github.gliviu.javaNonblockingFutures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Futures are objects that represent the results of asynchronous computations.
 * <p>
 * They are read-only and can be in any of these states once:
 * <ul>
 * <li>pending - asynchronous code is executing</li>
 * <li>fulfilled - operation completed successfully</li>
 * <li>rejected - operation failed</li>
 * </ul>
 * <p>
 * @see <a href="https://github.com/gliviu/java-nonblocking-futures">https://github.com/gliviu/java-nonblocking-futures</a>.
 * <p>
 * Create new futures using
 * <ul>
 * <li>{@link #future(Callable, ExecutorService)}</li>
 * <li>{@link #successful(Object)}</li>
 * <li>{@link #failed(Throwable)}</li>
 * <li>{@link #timeout(int, TimeUnit)}</li>
 * </ul>
 * <p>
 * Handlers are provided to process the result.
 * <ul>
 * <li>{@link #onComplete(CompleteHandler)}</li>
 * <li>{@link #onSuccess(Handler)}</li>
 * <li>{@link #onFailure(Handler)}</li>
 * </ul>
 * <p>
 * Composing futures can be achieved using
 * <ul>
 * <li>{@link #flatMap(Mapper)}</li>
 * <li>{@link #map(Mapper)}</li>
 * <li>{@link #all(Iterable)}</li>
 * <li>{@link #first(Iterable)}</li>
 * <li>{@link #recover(RecoverHandler)}</li>
 * </ul>
 * <p>
 * Use {@link #waitResult()} to wait synchronously for future completion and {@link #isComplete()} to check its state.
 * @param <V> result type of this future.
 */
public class Future<V> {

    private static final int NOT_AVAILABLE = -1;

    final InternalFutureTask internalFuture;
    private final ConcurrentLinkedQueue<Handler<V>> successHandlers = new ConcurrentLinkedQueue<Handler<V>>();
    private final ConcurrentLinkedQueue<Handler<Throwable>> failureHandlers = new ConcurrentLinkedQueue<Handler<Throwable>>();
    private final ConcurrentLinkedQueue<CompleteHandler<Throwable, V>> completeHandlers = new ConcurrentLinkedQueue<CompleteHandler<Throwable, V>>();


    /**
     * Handler for {@link Future#onSuccess(Handler)} and {@link Future#onFailure(Handler)}
     */
    public interface Handler<V> {
        void handle(V result);
    }

    /**
     * Handler for mapping requests from {@link Future#map(Mapper)} and {@link Future#flatMap(Mapper)}. 
     */
    public interface Mapper<V, T> {
        T map(final V v);
    }

    /**
     * Handler for {@link Future#recover(RecoverHandler)}.
     */
    public interface RecoverHandler<V> {
        V handle(Throwable t);
    }

    /**
     * Handler for {@link Future#onComplete(CompleteHandler)}.
     */
    public interface CompleteHandler<T extends Throwable, V> {
        void handle(T throwable, V result);
    }

    class InternalFutureTask extends FutureTask<V>{
        public InternalFutureTask(Callable<V> callable) {
            super(callable);
        }

        @Override
        protected void done() {
            synchronized (Future.this) {
                super.done();
                final V result;
                try {
                    result = this.get();
                } catch (final Throwable t) {
                    for(Handler<Throwable> handler : failureHandlers){
                        handleFailure(handler, t);
                    }
                    for(CompleteHandler<Throwable, V> handler : completeHandlers){
                        handleCompleteFailure(t, handler);
                    }
                    return;
                }
                for(Handler<V> handler : successHandlers){
                    handleSuccess(result, handler);
                }
                for(CompleteHandler<Throwable, V> handler : completeHandlers){
                    handleComplelteSuccess(result, handler);
                }
            }
        }

        void forceSuccess(V v) {
            this.set(v);
        }

        void forceFailure(Throwable t) {
            this.setException(t);
        }

    }

    /**
     * Internal constructor used to create futures based on {@link Callable}.
     */
    private Future(final Callable<V> callable, final ExecutorService executor) {
        internalFuture = new InternalFutureTask(callable);
        executor.submit(internalFuture);
    }

    /**
     * Internal constructor used to create promises.
     */
    Future() {
        internalFuture = new InternalFutureTask(new Callable<V>() {
            @Override
            public V call() throws Exception {
                return null;
            }
        });
    }


    /**
     * Creates a future with given {@link Callable} and starts executing it right away.
     * @param callable process to run asynchronously.
     * @param executor thread pool.
     */
    public static <V> Future<V> future(final Callable<V> callable, final ExecutorService executor){
        return new Future<V>(callable, executor);
    }

    /**
     * Creates an already successful future.
     * @param value success value.
     */
    public static <V> Future<V> successful(V value){
        Future<V> future = new Future<V>();
        future.internalFuture.forceSuccess(value);
        return future;
    }

    /**
     * Creates an already failed future.
     * @param t Failure.
     */
    public static <V> Future<V> failed(Throwable t){
        Future<V> future = new Future<V>();
        future.internalFuture.forceFailure(t);
        return future;
    }

    /**
     * Future that will fail with {@link TimeoutException} after a given period.
     * @param timeout timeout duration.
     * @return the future.
     */
    public static <V> Future<V> timeout(int timeout, TimeUnit unit){
        ScheduledExecutorService timeoutExecutor = Executors.newSingleThreadScheduledExecutor();
        final Future<V> promise = new Future<V>();
        timeoutExecutor.schedule(new Runnable() {

            @Override
            public void run() {
                promise.internalFuture.forceFailure(new TimeoutException());
            }
        }, unit.toMillis(timeout), TimeUnit.MILLISECONDS);
        
        timeoutExecutor.shutdown();
        return promise;
    }

    /**
     * Creates a new future holding the successful results of given futures.
     * In case some futures are failing, one of the failures will be returned as a new future.
     * Empty list received means a future with empty result is returned. 
     * @param futures futures to be processed.
     */
    public static <T> Future<Iterable<T>> all(Iterable<Future<T>> futures) {
        final Future<Iterable<T>> promise = new Future<Iterable<T>>();
        final AtomicInteger futuresSize = new AtomicInteger(NOT_AVAILABLE);
        final List<T> results = Collections.synchronizedList(new ArrayList<T>());
        int i = 0;
        for (Future<T> future : futures) {
            future.onComplete(new CompleteHandler<Throwable, T>() {
                @Override
                public void handle(Throwable fail, T result) {
                    if (fail != null) {
                        promise.tryFailure(fail);
                    } else {
                        results.add(result);
                        if (futuresSize.get() == results.size()) {
                            promise.trySuccess(results);
                        }
                    }
                }
            });
            i++;
        }
        futuresSize.set(i);
        if (futuresSize.get() == results.size()) {
            promise.trySuccess(results);
        }
        return promise;
    }

    /**
     * Convenience method for {@link #all(Iterable)}.
     * @param futures futures to be processed.
     */
    @SafeVarargs
    public static <T> Future<Iterable<T>> all(Future<T> ...futures) {
        return all(Arrays.asList(futures));
    }


    /**
     * Convenience method for {@link #first(Iterable)}.
     * @param futures futures to be processed.
     */
    @SafeVarargs
    public static <T> Future<T> first(Future<T> ...futures) {
        return first(Arrays.asList(futures));
    }

    /**
     * Creates a new future holding result or failure of the first completed future.
     * If an empty list is received, the returned future will never complete.
     * @param futures futures to be processed.
     */
    public static <T> Future<T> first(Iterable<Future<T>> futures) {
        final Future<T> promise = new Future<T>();
        for (Future<T> future : futures) {
            future.onComplete(new CompleteHandler<Throwable, T>() {
                @Override
                public void handle(Throwable fail, T result) {
                    if (fail != null) {
                        promise.tryFailure(fail);
                    } else {
                        promise.trySuccess(result);
                    }
                }
            });
        }
        return promise;
    }
    
    /**
     * Success handler.
     * @param handler Callback to be executed when this future completed successfully.
     * Any exception not treated in this handler will be printed to standard output.
     */
    public synchronized void onSuccess(Handler<V> handler) {
        if (internalFuture.isDone()) {
            try {
                final V result = internalFuture.get();
                handleSuccess(result, handler);
            } catch (Throwable t) {
                // Only success cases handled here. Ignoring errors.
            }
        } else {
            this.successHandlers.add(handler);
        }
    }

    /**
     * Failure handler.
     * @param handler Callback to be executed when this future completed with failure.
     * Any exception not treated in this handler will be printed to standard output.
     */
    public synchronized void onFailure(Handler<Throwable> handler) {
        if (internalFuture.isDone()) {
            try {
                internalFuture.get();
            } catch (Throwable t) {
                handleFailure(handler, t);
            }
        } else {
            this.failureHandlers.add(handler);
        }
    }

    /**
     * Completion handler.
     * @param handler Callback to be executed when this future completed.
     * Any exception not treated in this handler will be printed to standard output.
     */
    public synchronized void onComplete(CompleteHandler<Throwable, V> handler) {
        if (internalFuture.isDone()) {
            try {
                final V result = internalFuture.get();
                handleComplelteSuccess(result, handler);
            } catch (Throwable t) {
                handleCompleteFailure(t, handler);
                return;
            }
        } else {
            this.completeHandlers.add(handler);
        }
    }

    /**
     * @return completion status of this future.
     */
    public synchronized boolean isComplete(){
        return internalFuture.isDone();
    }

    private void handleComplelteSuccess(final V result, CompleteHandler<Throwable, V> h) {
        try{
            h.handle(null, result);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void handleCompleteFailure(final Throwable t, CompleteHandler<Throwable, V> h) {
        try{
            h.handle(t instanceof ExecutionException && t.getCause()!=null ? t.getCause() : t, null);
        } catch(Exception e){
            // Handlers are supposed to catch their exceptions otherwise they'll be printed to stdout.
            e.printStackTrace();
        }
    }

    private void handleSuccess(final V result, Handler<V> h) {
        try{
            h.handle(result);
        } catch(Exception e){
            // Handlers are supposed to catch their exceptions otherwise they'll be printed to stdout.
            e.printStackTrace();
        }
    }

    private void handleFailure(Handler<Throwable> h, final Throwable t) {
        try{
            h.handle(t instanceof ExecutionException && t.getCause()!=null ? t.getCause() : t);
        } catch(Exception e){
            // Handlers are supposed to catch their exceptions otherwise they'll be printed to stdout.
            e.printStackTrace();
        }
    }

    /**
     * Applies given function to the successful result of this future. Returns the transformation as a new future.
     * If this future completed with failure or the handler throws exception, a new future is returned holding the failure.   
     * @param mapper handler for mapping operation.
     * @param <T> result type after applying 'mapper'.
     */
    public <T> Future<T> map(final Mapper<V, T> mapper) {
        final Future<T> promise = new Future<T>();
        this.onSuccess(new Handler<V>() {
            @Override
            public void handle(V result) {
                try {
                    promise.internalFuture.forceSuccess(mapper.map(result));
                } catch (Throwable t) {
                    promise.internalFuture.forceFailure(t);
                }
            }
        });
        this.onFailure(new Handler<Throwable>() {
            @Override
            public void handle(Throwable fail) {
                promise.internalFuture.forceFailure(fail);
            }
        });
        return promise;
    }

    /**
     * Maps the result of this future to another future. Waits for it to complete and returns the result as a new future.
     * If this future completed with failure or the handler throws exception, a new future is returned holding the failure.   
     * @param mapper handler for mapping operation.
     * @param <T> result type after applying 'mapper'.
     */
    public <T> Future<T> flatMap(final Mapper<V, Future<T>> mapper) {
        final Future<T> promise = new Future<T>();
        this.onSuccess(new Handler<V>() {
            
            @Override
            public void handle(V v) {
                try{
                    Future<T> future = mapper.map(v);
                    future.onSuccess(new Handler<T>() {
                        @Override
                        public void handle(T result) {
                            promise.internalFuture.forceSuccess(result);
                        }
                    });
                    future.onFailure(new Handler<Throwable>() {
                        @Override
                        public void handle(Throwable fail) {
                            promise.internalFuture.forceFailure(fail);
                        }
                    });
                } catch(Exception e){
                    promise.internalFuture.forceFailure(e);
                }
            }
        });
        
        this.onFailure(new Handler<Throwable>() {
            @Override
            public void handle(Throwable fail) {
                promise.internalFuture.forceFailure(fail);
            }
        });
        return promise;
    }

    /**
     * Waits for this future to complete. Returns result in case of success or throws error if future completed with failure.
     */
    public V waitResult() throws Throwable {
        try {
            return internalFuture.get();
        } catch (ExecutionException e) {
            throw e.getCause()!=null?e.getCause():e;
        }
    }

    /**
     * Handles any errors occurred in this future by mapping the failure to a new future.
     * @param recoverHandler handler for mapping operation.
     */
    public Future<V> recover(final RecoverHandler<V> recoverHandler){
        final Future<V> promise = new Future<V>();
        this.onComplete(new CompleteHandler<Throwable, V>() {

            @Override
            public void handle(Throwable t, V result) {
                if(t!=null){
                    try{
                        promise.internalFuture.forceSuccess(recoverHandler.handle(t));
                    } catch(Throwable fail){
                        promise.internalFuture.forceFailure(fail);
                    }
                } else{
                    promise.internalFuture.forceSuccess(result);
                }
            }
        });
        return promise;
    }

    /**
     * Tries to completes this promise with success result if it is not already completed.
     * @param result result information.
     * @return false if the promise is already completed, true otherwise
     */
    synchronized boolean trySuccess(V result) {
        if(internalFuture.isDone()){
            return false;
        } else{
            internalFuture.forceSuccess(result);
            return true;
        }
    }

    /**
     * Tries to completes this promise with failure if it is not already completed.
     * @param fail failure information.
     * @return false if the promise is already completed, true otherwise
     */
    synchronized boolean tryFailure(Throwable fail) {
        if(internalFuture.isDone()){
            return false;
        } else{
            internalFuture.forceFailure(fail);
            return true;
        }
    }

    /**
     * Complete this promise with successful result.
     * @param result result information.
     */
    synchronized void forceSuccess(V result){
        if(internalFuture.isDone()){
            throw new IllegalStateException("Cannot set successful result as future/promise is already completed.");
        } else{
            internalFuture.forceSuccess(result);
        }
    }

    /**
     * Complete this future with failed result.
     * @param fail failure information.
     */
    synchronized void forceFailure(Throwable fail){
        if(internalFuture.isDone()){
            throw new IllegalStateException("Cannot set failed result as future/promise is is already completed.");
        } else{
            internalFuture.forceFailure(fail);
        }
    }


}