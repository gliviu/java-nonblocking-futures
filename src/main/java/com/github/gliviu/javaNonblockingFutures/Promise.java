package com.github.gliviu.javaNonblockingFutures;

/**
 * Promise is very similar to {@link Future}, meaning it can have a completion status of success or failure.
 * <p>
 * However after creation it can be manually completed using {@link #success(Object)} or {@link #failure(Throwable)} while {@link Future}s are read-only.
 * <p>
 * Create them with {@link #promise()}.
 * <p>
 * Get access to underlying Future using {@link Promise#future()}
 * @param <V> result type of this future.
 * <p>
 * @see <a href="https://github.com/gliviu/java-nonblocking-futures">https://github.com/gliviu/java-nonblocking-futures</a>.
 */
public class Promise<V>{
    private Future<V> future = new Future<V>();
    
    /**
     * Private constructor.
     */
    private Promise(){}
    
    /**
     * Creates new promise.
     */
    public static <V> Promise<V> promise(){
    	return new Promise<V>();
    }
    
    /**
     * Complete this promise with successful result.
     * See also {@link Promise#trySuccess(Object)}.
     * @param result result information.
     */
    public void success(V result){
        future.forceSuccess(result);
    }

    /**
     * Complete this promise with failed result.
     * See also {@link Promise#tryFailure(Throwable)}.
     * @param fail failure information.
     */
    public void failure(Throwable fail){
        future.forceFailure(fail);
    }

    /**
     * Retrieve a future that will be completed when this promise is completed.
     */
    public Future<V> future(){
        return future;
    }

    /**
     * Status of this promise.
     */
    public boolean isComplete(){
        return future.isComplete();
    }


    /**
     * Tries to completes this promise with success result if it is not already completed.
     * @param result result information.
     * @return false if the promise is already completed, true otherwise
     */
    public boolean trySuccess(V result) {
        return future.trySuccess(result);
    }

    /**
     * Tries to completes this promise with failure if it is not already completed.
     * @param fail failure information.
     * @return false if the promise is already completed, true otherwise
     */
    public boolean tryFailure(Throwable fail) {
        return future.tryFailure(fail);
    }
}