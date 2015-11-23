package com.github.gliviu.javaNonblockingFutures.unitTests;

import java.util.stream.IntStream;

import com.github.gliviu.javaNonblockingFutures.Future;
import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;

/**
 * Combined tests.
 */
public class TestCombined extends BaseUnitTests {

    /**
     * if(process("p1")=="p1"){
     *      return process("p2");
     * } else{
     *      return "failure";
     * }
     */
    @Test
    @Configuration(repetitions=10000, threadPool=1)
    @Configuration(repetitions=10000, threadPool=8)

    @Configuration(repetitions=10000, threadPool=1, fail1="fail1")
    @Configuration(repetitions=10000, threadPool=8, fail1="fail1")

    @Configuration(repetitions=10000, threadPool=1, fail2="fail2")
    @Configuration(repetitions=10000, threadPool=8, fail2="fail2")
    public void test1(){
        if(testPhase()){
            // Test
            Future<String> future = Future.future(()->{
                return process("p1", fail1);
            }, executor).map(process1Result->{
                if("p1".equals(process1Result)){
                    return process("p2", fail2);
                } else{
                    return "failure";
                }
            });
            future.onSuccess(res ->{
                output.append(res);
                finalizeTest();
            });
            future.onFailure(t ->{
                output.append(t.getMessage());
                finalizeTest();
            });

        } else{
            // Assert
            StringBuffer expected = new StringBuffer();
            IntStream.range(0, repetitions).forEach(i -> {
                if(fail1!=null){
                    expected.append(fail1);
                } else if(fail2!=null){
                    expected.append(fail2);
                } else{
                    expected.append("p2");
                }
            });
            Assert.assertEquals(expected.toString(), output.toString());
        }
    }

    /**
     * result = process("p1");
     */
    @Test
    @Configuration(repetitions=10000, threadPool=1)
    @Configuration(repetitions=10000, threadPool=8)

    @Configuration(repetitions=10000, threadPool=1, fail1="fail1")
    @Configuration(repetitions=10000, threadPool=8, fail1="fail1")
    public void test2(){
        if(testPhase()){
            // Test
            Future<String> future = Future.future(()->{
                return process("p1", fail1);
            }, executor);

            try {
                String result = future.waitResult();
                output.append(result);
            } catch (Throwable e) {
                output.append(e.getMessage());
            }

            finalizeTest();
        } else{
            // Assert
            StringBuffer expected = new StringBuffer();
            IntStream.range(0, repetitions).forEach(i -> {
                if(fail1!=null){
                    expected.append(fail1);
                } else{
                    expected.append("p1");
                }
            });
            Assert.assertEquals(expected.toString(), output.toString());
        }
    }

    /**
     * res1 = process1();
     * if(res1=="r1a"){
     *      res2 = process2();
     *      if(res2=="r2a"){
     *          return "result1";
     *      } else if(res2="r2b") {
     *          return "result2";
     *      }
     * } else if(res1=="r1b"){
     *      res3 = process3();
     *      if(res3=="r3a"){
     *          return "result3";
     *      } else if(res3=="r3b"){
     *          return "result4";
     *      }
     * }
     */
    @Test
    @Configuration(repetitions=100,     threadPool=1,   option1="r1a",  option2="r2a",  sleep1="1")
    @Configuration(repetitions=1000,    threadPool=8,   option1="r1a",  option2="r2a",  sleep1="1")

    @Configuration(repetitions=100,     threadPool=1,   option1="r1a",  option2="r2b",  sleep1="1")
    @Configuration(repetitions=1000,    threadPool=8,   option1="r1a",  option2="r2b",  sleep1="1")

    @Configuration(repetitions=100,     threadPool=1,   option1="r1b",  option3="r3a",  sleep1="1")
    @Configuration(repetitions=1000,    threadPool=8,   option1="r1b",  option3="r3a",  sleep1="1")

    @Configuration(repetitions=100,     threadPool=1,   option1="r1b",  option3="r3b",  sleep1="1")
    @Configuration(repetitions=1000,    threadPool=8,   option1="r1b",  option3="r3b",  sleep1="1")

    @Configuration(repetitions=100,     threadPool=1,   option1="r1a",  option2="r2a",  fail1="error1",  sleep1="1")
    @Configuration(repetitions=1000,     threadPool=8,   option1="r1a",  option2="r2a",  fail1="error1",  sleep1="1")

    @Configuration(repetitions=100,     threadPool=1,   option1="r1a",  option2="r2a",  fail2="error2",  sleep1="1")
    @Configuration(repetitions=1000,     threadPool=8,   option1="r1a",  option2="r2a",  fail2="error2",  sleep1="1")

    @Configuration(repetitions=100,     threadPool=1,   option1="r1a",  option2="r2a",  fail3="error3",  sleep1="1")
    @Configuration(repetitions=1000,     threadPool=8,   option1="r1a",  option2="r2a",  fail3="error3",  sleep1="1")

    @Configuration(repetitions=100,     threadPool=1,   option1="r1b",  option3="r3a",  fail4="error4",  sleep1="1")
    @Configuration(repetitions=1000,     threadPool=8,   option1="r1b",  option3="r3a",  fail4="error4",  sleep1="1")

    @Configuration(repetitions=100,     threadPool=1,   option1="r1b",  option3="r3a",  fail5="error5",  sleep1="1")
    @Configuration(repetitions=1000,     threadPool=8,   option1="r1b",  option3="r3a",  fail5="error5",  sleep1="1")

    public void test3(){
        if(testPhase()){
            // Test
            processF1(option1, fail1, null, sleep1, executor).flatMap(process1Result -> {
                switch(process1Result){
                case "r1a":
                    return processF2(option2, fail2, fail3, sleep1, executor);
                case "r1b":
                    return processF3(option3, fail4, fail5, sleep1, executor);
                default:
                    throw new RuntimeException("unexpected1");
                }
            }).map(process23Result -> {
                switch(process23Result){
                case "r2a":
                    return "result1";
                case "r2b":
                    return "result2";
                case "r3a":
                    return "result3";
                case "r3b":
                    return "result4";
                default:
                    throw new RuntimeException("unexpected");
                }
            }).onComplete((error, result) ->{
                if(error!=null){
                    output.append(error.getMessage());
                } else{
                    output.append(result);
                }
                finalizeTest();
            });
        } else{
            // Assert
            StringBuffer expected = new StringBuffer();
            IntStream.range(0, repetitions).forEach(i -> {
                if(fail1!=null){
                    expected.append(fail1);
                } else if(fail2!=null){
                    expected.append(fail2);
                } else if(fail3!=null){
                    expected.append(fail3);
                } else if(fail4!=null){
                    expected.append(fail4);
                } else if(fail5!=null){
                    expected.append(fail5);
                } else if(fail6!=null){
                    expected.append(fail6);
                } else {
                    switch(option1){
                    case "r1a":
                        switch(option2){
                        case "r2a":
                            expected.append("result1");
                            break;
                        case "r2b":
                            expected.append("result2");
                            break;
                        default:
                            expected.append("unexpected");
                        };
                        break;
                    case "r1b":
                        switch(option3){
                        case "r3a":
                            expected.append("result3");
                            break;
                        case "r3b":
                            expected.append("result4");
                            break;
                        default:
                            expected.append("unexpected");
                        };
                        break;
                    default:
                        expected.append("unexpected1");
                    }
                }
            });
            Assert.assertEquals(expected.toString(), output.toString());
        }
    }



}
