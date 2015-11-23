package com.github.gliviu.javaNonblockingFutures.unitTests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import com.github.gliviu.javaNonblockingFutures.Future;
import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.TestUtils;

/**
 * Tests for {@link Future#all(Iterable)}.
 */
public class TestAll extends BaseUnitTests {

    @Test
    @Configuration(repetitions=1, threadPool=8)
    public void test1_1(){
        if(testPhase()){
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(()->{
                TestUtils.sleep(100);
                return "s1";
            }, executor));
            futures.add(Future.future(()->{
                TestUtils.sleep(200);
                return "s2";
            }, executor));

            Future.all(futures).onComplete((fail, result)->{
                if(fail!=null){
                    output.append(fail.getMessage());
                }else{
                    output.append(result);
                }
                finalizeTest();
            });
        } else{
            // Verify
            String expected = "[s1, s2]";
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=8)
    public void test1_2(){
        if(testPhase()){
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(()->{
                TestUtils.sleep(100);
                return "s1";
            }, executor));
            futures.add(Future.future(()->{
                TestUtils.sleep(200);
                throw new RuntimeException("f2");
            }, executor));

            Future.all(futures).onComplete((fail, result)->{
                if(fail!=null){
                    output.append(fail.getMessage());
                }else{
                    output.append(result);
                }
                finalizeTest();
            });
        } else{
            // Verify
            String expected = "f2";
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=8)
    public void test1_3(){
        if(testPhase()){
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(()->{
                TestUtils.sleep(200);
                return "s1";
            }, executor));
            futures.add(Future.future(()->{
                TestUtils.sleep(100);
                throw new RuntimeException("f2");
            }, executor));

            Future.all(futures).onComplete((fail, result)->{
                if(fail!=null){
                    output.append(fail.getMessage());
                }else{
                    output.append(result);
                }
                finalizeTest();
            });
        } else{
            // Verify
            String expected = "f2";
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=8)
    public void test1_4(){
        if(testPhase()){
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(()->{
                TestUtils.sleep(100);
                throw new RuntimeException("f1");
            }, executor));
            futures.add(Future.future(()->{
                TestUtils.sleep(200);
                return "s2";
            }, executor));

            Future.all(futures).onComplete((fail, result)->{
                if(fail!=null){
                    output.append(fail.getMessage());
                }else{
                    output.append(result);
                }
                finalizeTest();
            });
        } else{
            // Verify
            String expected = "f1";
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=8)
    public void test1_5(){
        if(testPhase()){
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(()->{
                TestUtils.sleep(100);
                throw new RuntimeException("f1");
            }, executor));
            futures.add(Future.future(()->{
                TestUtils.sleep(200);
                throw new RuntimeException("f2");
            }, executor));

            Future.all(futures).onComplete((fail, result)->{
                if(fail!=null){
                    output.append(fail.getMessage());
                }else{
                    output.append(result);
                }
                finalizeTest();
            });
        } else{
            // Verify
            String expected = "f1";
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=8)
    public void test1_6(){
        if(testPhase()){
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(()->{
                TestUtils.sleep(200);
                throw new RuntimeException("f1");
            }, executor));
            futures.add(Future.future(()->{
                TestUtils.sleep(100);
                throw new RuntimeException("f2");
            }, executor));

            Future.all(futures).onComplete((fail, result)->{
                if(fail!=null){
                    output.append(fail.getMessage());
                }else{
                    output.append(result);
                }
                finalizeTest();
            });
        } else{
            // Verify
            String expected = "f2";
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=8)
    public void test1_7(){
        if(testPhase()){
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(()->{
                TestUtils.sleep(100);
                throw new RuntimeException("f1");
            }, executor));
            futures.add(Future.future(()->{
                TestUtils.sleep(200);
                throw new RuntimeException("f2");
            }, executor));

            Future.all(futures).onComplete((fail, result)->{
                if(fail!=null){
                    output.append(fail.getMessage());
                }else{
                    output.append(result);
                }
                finalizeTest();
            });
        } else{
            // Verify
            String expected = "f1";
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=1, threadPool=8)
    public void test2(){
        if(testPhase()){
            // Test
            List<Future<String>> futures = new ArrayList<>();

            Future.all(futures).onComplete((fail, result)->{
                if(fail!=null){
                    output.append(fail.getMessage());
                }else{
                    output.append(result);
                }
                finalizeTest();
            });
        } else{
            // Verify
            String expected = "[]";
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=20, threadPool=1, sleep1="0", sleep2="10", option1="20")
    @Configuration(repetitions=30, threadPool=1, sleep1="0", sleep2="10", option1="10")
    @Configuration(repetitions=50, threadPool=1, sleep1="0", sleep2="10", option1="5")
    @Configuration(repetitions=250, threadPool=1, sleep1="0", sleep2="10", option1="1")
    @Configuration(repetitions=50, threadPool=1, sleep1="0", sleep2="1", option1="100")

    @Configuration(repetitions=50, threadPool=4, sleep1="0", sleep2="10", option1="20")
    @Configuration(repetitions=100, threadPool=4, sleep1="0", sleep2="10", option1="10")
    @Configuration(repetitions=200, threadPool=4, sleep1="0", sleep2="10", option1="5")
    @Configuration(repetitions=1000, threadPool=4, sleep1="0", sleep2="10", option1="1")
    @Configuration(repetitions=100, threadPool=4, sleep1="0", sleep2="1", option1="100")

    @Configuration(repetitions=100, threadPool=8, sleep1="0", sleep2="10", option1="20")
    @Configuration(repetitions=200, threadPool=8, sleep1="0", sleep2="10", option1="10")
    @Configuration(repetitions=400, threadPool=8, sleep1="0", sleep2="10", option1="5")
    @Configuration(repetitions=2000, threadPool=8, sleep1="0", sleep2="10", option1="1")
    @Configuration(repetitions=200, threadPool=8, sleep1="0", sleep2="1", option1="100")
    public void test3(){
        int futureNo = Integer.parseInt(option1);
        if(testPhase()){
            // Test
            List<Future<Integer>> futures = new ArrayList<>();
            IntStream.range(1, futureNo+1).forEach(i -> 
            futures.add(Future.future(()->{
                int sleepMilli = TestUtils.random(sleep1, sleep2);
                if(sleepMilli!=0){
                    TestUtils.sleep(sleepMilli);
                }
                return i;
            }, executor)));
            Future.all(futures).map(strings ->{
                return StreamSupport.stream(strings.spliterator(), false).sorted().map(i -> Integer.toString(i)).collect(Collectors.joining(","));
            }).onSuccess(result->{
                output.append(result);
                finalizeTest();
            });
        } else{
            // Verify
            String expectedSingleRunResult = IntStream.range(1, futureNo+1).mapToObj(i -> Integer.toString(i)).collect(Collectors.joining(","));
            String expected = IntStream.range(0, repetitions).mapToObj(i -> expectedSingleRunResult).collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

}
