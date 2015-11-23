package com.github.gliviu.javaNonblockingFutures.unitTests;

import java.util.logging.Handler;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.gliviu.javaNonblockingFutures.Future;
import com.github.gliviu.javaNonblockingFutures.Future.CompleteHandler;
import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.TestUtils;

/**
 * Tests {@link Future#onComplete(CompleteHandler)}, {@link Future#onFailure(Handler)}, {@link Future#onSuccess(Handler)}.
 */
public class TestHandlers extends BaseUnitTests {
    @Test
    @Configuration(repetitions=1,      threadPool=8)
    @Configuration(repetitions=2,      threadPool=8)
    @Configuration(repetitions=5,      threadPool=8)
    @Configuration(repetitions=10,     threadPool=8)
    @Configuration(repetitions=100,    threadPool=8)
    @Configuration(repetitions=1000,   threadPool=8)
    @Configuration(repetitions=10000,  threadPool=8)
    @Configuration(repetitions=1,      threadPool=1)
    @Configuration(repetitions=2,      threadPool=1)
    @Configuration(repetitions=5,      threadPool=1)
    @Configuration(repetitions=10,     threadPool=1)
    @Configuration(repetitions=100,    threadPool=1)
    @Configuration(repetitions=1000,   threadPool=1)
    @Configuration(repetitions=10000,  threadPool=1)

    @Configuration(repetitions=1,      threadPool=8, sleep1="10")
    @Configuration(repetitions=2,      threadPool=8, sleep1="10")
    @Configuration(repetitions=5,      threadPool=8, sleep1="10")
    @Configuration(repetitions=10,     threadPool=8, sleep1="10")
    @Configuration(repetitions=100,    threadPool=8, sleep1="10")
    @Configuration(repetitions=1000,   threadPool=8, sleep1="10")
    @Configuration(repetitions=1,      threadPool=1, sleep1="10")
    @Configuration(repetitions=2,      threadPool=1, sleep1="10")
    @Configuration(repetitions=5,      threadPool=1, sleep1="10")
    @Configuration(repetitions=10,     threadPool=1, sleep1="10")
    @Configuration(repetitions=100,    threadPool=1, sleep1="10")

    @Configuration(repetitions=1,      threadPool=8, sleep2="10")
    @Configuration(repetitions=2,      threadPool=8, sleep2="10")
    @Configuration(repetitions=5,      threadPool=8, sleep2="10")
    @Configuration(repetitions=10,     threadPool=8, sleep2="10")
    @Configuration(repetitions=100,    threadPool=8, sleep2="10")
    @Configuration(repetitions=1000,   threadPool=8, sleep2="10")
    @Configuration(repetitions=1,      threadPool=1, sleep2="10")
    @Configuration(repetitions=2,      threadPool=1, sleep2="10")
    @Configuration(repetitions=5,      threadPool=1, sleep2="10")
    @Configuration(repetitions=10,     threadPool=1, sleep2="10")
    @Configuration(repetitions=100,    threadPool=1, sleep2="10")

    @Configuration(repetitions=1,      threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=2,      threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=5,      threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=10,     threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=100,    threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=1,      threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=2,      threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=5,      threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=10,     threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=100,    threadPool=1, sleep1="10", sleep2="10")
    public void test1() {
        if(testPhase()){
            // Test
            Future<String> future = Future.future(() -> {
                if(sleep1!=null){
                    TestUtils.sleep(sleep1);
                }
                return "s1";
            } , executor);

            future.onSuccess(result -> {
                if(sleep2!=null){
                    TestUtils.sleep(sleep2);
                }
                output.append(result);
                finalizeTest();
            });
            future.onFailure(t -> {
                output.append(t.getMessage());
                finalizeTest();
            });
        } else{
            // Assert
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "s1").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }


    @Test
    public void test2_1(){
        StringBuffer result = new StringBuffer();
        Future<String> future = Future.successful("a1");
        future.onSuccess(res -> {
            result.append(res);
        });
        future.onFailure(t ->{
            result.append(t.getMessage());
        });
        future.onComplete((t, res) ->{
            if(t!=null){
                result.append(t.getMessage());
            } else{
                result.append(res);
            }
        });
        TestUtils.waitFuture(future);
        Assert.assertEquals("a1a1", result.toString());
    }

    @Test
    public void test2_2(){
        StringBuffer result = new StringBuffer();
        Future<String> future = Future.failed(new RuntimeException("e1"));
        future.onSuccess(res -> {
            result.append(res);
        });
        future.onFailure(t ->{
            result.append(t.getMessage());
        });
        future.onComplete((t, res) ->{
            if(t!=null){
                result.append(t.getMessage());
            } else{
                result.append(res);
            }
        });
        TestUtils.waitFuture(future);
        Assert.assertEquals("e1e1", result.toString());
    }

    @Test
    @Configuration(repetitions=20,     threadPool=1,   sleep1="50")
    @Configuration(repetitions=160,    threadPool=8,   sleep1="50")

    @Configuration(repetitions=50,     threadPool=1,   sleep2="30")
    @Configuration(repetitions=50,     threadPool=8,   sleep2="30")
    public void test8_1() {
        if(testPhase()){
            // Test
            Future<String> f1 = Future.future(() -> {
                if(sleep2!=null){
                    TestUtils.sleep(sleep2);
                }
                return "f1";
            } , executor);
            if(sleep2!=null){
                TestUtils.sleep(sleep2);
            }
            f1.onComplete((fail, result) -> {
                throw new RuntimeException("exc1");
            });
            output.append("a1");
            finalizeTest();
        } else{
            // Assert
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "a1").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=100,    threadPool=1,   sleep1="500")
    @Configuration(repetitions=100,    threadPool=8,   sleep1="500")

    @Configuration(repetitions=50,     threadPool=1,   sleep2="50")
    @Configuration(repetitions=50,     threadPool=8,   sleep2="50")
    public void test8_2() {
        if(testPhase()){
            // Test
            Future<String> f1 = Future.future(() -> {
                if(sleep2!=null){
                    TestUtils.sleep(sleep2);
                }
                return "f1";
            } , executor);
            if(sleep2!=null){
                TestUtils.sleep(sleep2);
            }
            f1.onComplete((fail, result) -> {
                throw new RuntimeException("exc1");
            });
            f1.onSuccess(result -> {
                throw new RuntimeException("exc1");
            });
            f1.onSuccess(result -> {
                output.append(result);
                finalizeTest();
            });
        } else{
            // Assert
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "f1").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=100,    threadPool=1)
    @Configuration(repetitions=50,     threadPool=1,   sleep1="30")
    @Configuration(repetitions=50,     threadPool=1,   sleep1="30",    sleep2="30")
    public void test8_3() {
        if(testPhase()){
            // Test
            Future<String> f1 = Future.future(() -> {
                return "f1";
            } , executor);
            if(sleep1!=null){
                TestUtils.sleep(sleep1);
            }
            f1.onSuccess(result -> {
                throw new RuntimeException("exc1");
            });
            if(sleep2!=null){
                TestUtils.sleep(sleep2);
            }
            f1.onSuccess(result -> {
                output.append(result);
                finalizeTest();
            });
        } else{
            // Assert
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "f1").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=100,    threadPool=1)
    @Configuration(repetitions=50,     threadPool=1,   sleep1="30")
    @Configuration(repetitions=50,     threadPool=1,   sleep1="30",    sleep2="30")
    public void test8_4() {
        if(testPhase()){
            // Test
            Future<String> f1 = Future.future(() -> {
                throw new RuntimeException("e1");
            } , executor);
            if(sleep1!=null){
                TestUtils.sleep(sleep1);
            }
            f1.onFailure(failure -> {
                throw new RuntimeException("exc1");
            });
            if(sleep2!=null){
                TestUtils.sleep(sleep2);
            }
            f1.onFailure(failure -> {
                output.append(failure.getMessage());
                finalizeTest();
            });
        } else{
            // Assert
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "e1").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }


}
