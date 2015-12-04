package com.github.gliviu.javaNonblockingFutures.unitTests;

import com.github.gliviu.javaNonblockingFutures.Promise;
import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;

/**
 * Tests {@link Promise}
 */
public class TestPromise extends BaseUnitTests {
    @Test
    @Configuration(repetitions=1, threadPool=1)
    public void test1(){
        if(testPhase()){
            Promise<String> promise = Promise.promise();
            promise.success("S1");
            output.append("success");
            promise.future().onSuccess(result -> {
                output.append(result);
                finalizeTest();
            });
        } else{
            Assert.assertEquals("successS1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=1)
    public void test2(){
        if(testPhase()){
            Promise<String> promise = Promise.promise();
            try{
                promise.success("S1");
                promise.success("S2");
            } catch(Exception e){
                output.append("exception");
            }
            promise.future().onSuccess(result -> {
                output.append(result);
                finalizeTest();
            });
        } else{
            Assert.assertEquals("exceptionS1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=1)
    public void test3(){
        if(testPhase()){
            Promise<String> promise = Promise.promise();
            promise.failure(new RuntimeException("F1"));
            output.append("failure");
            promise.future().onFailure(fail -> {
                output.append(fail.getMessage());
                finalizeTest();
            });
        } else{
            Assert.assertEquals("failureF1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=1)
    public void test4(){
        if(testPhase()){
            Promise<String> promise = Promise.promise();
            try{
                promise.failure(new RuntimeException("F1"));
                promise.failure(new RuntimeException("F2"));
            } catch(Exception e){
                output.append("exception");
            }
            promise.future().onFailure(fail -> {
                output.append(fail.getMessage());
                finalizeTest();
            });
        } else{
            Assert.assertEquals("exceptionF1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=1)
    public void test6_1(){
        if(testPhase()){
            Promise<String> promise = Promise.promise();
            try{
                promise.failure(new RuntimeException("F1"));
                boolean succeeded = promise.tryFailure(new RuntimeException("F2"));
                output.append(succeeded);
            } catch(Exception e){
                output.append("exception");
            }
            promise.future().onFailure(fail -> {
                output.append(fail.getMessage());
                finalizeTest();
            });
        } else{
            Assert.assertEquals("falseF1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=1)
    public void test6_2(){
        if(testPhase()){
            Promise<String> promise = Promise.promise();
            try{
                boolean succeeded = promise.tryFailure(new RuntimeException("F1"));
                output.append(succeeded);
            } catch(Exception e){
                output.append("exception");
            }
            promise.future().onFailure(fail -> {
                output.append(fail.getMessage());
                finalizeTest();
            });
        } else{
            Assert.assertEquals("trueF1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=1)
    public void test6_3(){
        if(testPhase()){
            Promise<String> promise = Promise.promise();
            try{
                promise.success("S1");
                boolean succeeded = promise.tryFailure(new RuntimeException("F2"));
                output.append(succeeded);
            } catch(Exception e){
                output.append("exception");
            }
            promise.future().onSuccess(result -> {
                output.append(result);
                finalizeTest();
            });
        } else{
            Assert.assertEquals("falseS1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=1)
    public void test6_4(){
        if(testPhase()){
            Promise<String> promise = Promise.promise();
            try{
                promise.success("S1");
                boolean succeeded = promise.trySuccess("S2");
                output.append(succeeded);
            } catch(Exception e){
                output.append("exception");
            }
            promise.future().onSuccess(result -> {
                output.append(result);
                finalizeTest();
            });
        } else{
            Assert.assertEquals("falseS1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=1)
    public void test6_5(){
        if(testPhase()){
            Promise<String> promise = Promise.promise();
            try{
                boolean succeeded = promise.trySuccess("S1");
                promise.trySuccess("S2");
                output.append(succeeded);
            } catch(Exception e){
                output.append("exception");
            }
            promise.future().onSuccess(result -> {
                output.append(result);
                finalizeTest();
            });
        } else{
            Assert.assertEquals("trueS1", output.toString());
        }
    }
}
