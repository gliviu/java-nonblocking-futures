package com.github.gliviu.javaNonblockingFutures.unitTests.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.gliviu.javaNonblockingFutures.Future;
import com.github.gliviu.javaNonblockingFutures.Promise;
import org.junit.Rule;

public abstract class BaseUnitTests {
    @Rule
    public ConfigurationRule rule = new ConfigurationRule();

    /**
     * Executor used by current test.
     */
    public ExecutorService executor;
    public Promise<Void> currentTestFinished;
    public StringBuffer output;

    /**
     * True - test phase. False - assertion phase.
     */
    public AtomicBoolean testPhase;

    /**
     * Number of repetitions for current test.
     */
    public Integer repetitions;

    /**
     * Current test step. Between [0, repetitions).
     */
    public AtomicInteger step;

    /**
     * Number of tests finalized.
     */
    public AtomicInteger doneTests;

    /**
     * Promise will be completed after all steps have run.
     */
    public Promise<Void> allTestStepsFinished;

    /**
     * Number of threads allocated to the executor used by current test.
     */
    public Integer threadPool;

    public Boolean sequential;
    public Integer sleep1;
    public Integer sleep2;
    public String fail1;
    public String fail2;
    public String fail3;
    public String fail4;
    public String fail5;
    public String fail6;
    public String option1;
    public String option2;
    public String option3;

    protected boolean testPhase() {
        return testPhase.get();
    }

    protected void finalizeTest() {
        if (doneTests.incrementAndGet() == repetitions) {
            allTestStepsFinished.success(null);
        }
        if(sequential){
            currentTestFinished.success(null);;
        }
    }

    protected String process(String result, String error) {
        if (error == null) {
            return result;
        } else {
            throw new RuntimeException(error);
        }
    }

    protected String process1(String result, String error) {
        return process(result, error);
    }
    protected String process2(String result, String error) {
        return process(result, error);
    }
    protected String process3(String result, String error) {
        return process(result, error);
    }
    protected String process4(String result, String error) {
        return process(result, error);
    }

    protected Future<String> processF(String result, String futureError, String immediateError, final Integer futureMilliseconds, ExecutorService executor) {
        if (immediateError == null) {
            return Future.future(() -> {
                if(futureMilliseconds!=null){
                    TestUtils.sleep(futureMilliseconds);
                }
                return process(result, futureError);
            } , executor);
        } else {
            throw new RuntimeException(immediateError);
        }
    }
    protected Future<String> processF(String result, String futureError, String immediateError, ExecutorService executor) {
        return processF(result, futureError, immediateError, null, executor);
    }
    protected Future<String> processF1(String result, String futureError, String immediateError, ExecutorService executor) {
        return processF(result, futureError, immediateError, executor);
    }
    protected Future<String> processF2(String result, String futureError, String immediateError, ExecutorService executor) {
        return processF(result, futureError, immediateError, executor);
    }
    protected Future<String> processF3(String result, String futureError, String immediateError, ExecutorService executor) {
        return processF(result, futureError, immediateError, executor);
    }
    protected Future<String> processF4(String result, String futureError, String immediateError, ExecutorService executor) {
        return processF(result, futureError, immediateError, executor);
    }

    protected Future<String> processF1(String result, String futureError, String immediateError, Integer futureMilliseconds, ExecutorService executor) {
        return processF(result, futureError, immediateError, futureMilliseconds, executor);
    }

    protected Future<String> processF2(String result, String futureError, String immediateError, Integer futureMilliseconds, ExecutorService executor) {
        return processF(result, futureError, immediateError, futureMilliseconds, executor);
    }

    protected Future<String> processF3(String result, String futureError, String immediateError, Integer futureMilliseconds, ExecutorService executor) {
        return processF(result, futureError, immediateError, futureMilliseconds, executor);
    }

    protected Future<String> processF4(String result, String futureError, String immediateError, Integer futureMilliseconds, ExecutorService executor) {
        return processF(result, futureError, immediateError, futureMilliseconds, executor);
    }

}
