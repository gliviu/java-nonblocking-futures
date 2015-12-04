package com.github.gliviu.javaNonblockingFutures.unitTests;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.gliviu.javaNonblockingFutures.Future;
import com.github.gliviu.javaNonblockingFutures.Promise;
import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.TestUtils;

/**
 * Tests exception handling.
 *
 */
public class TestExceptionHandling extends BaseUnitTests {

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
            final Future<String> future = Future.future(() -> {
                if(sleep1!=null){
                    TestUtils.sleep(sleep1);
                }
                throw new IllegalStateException("e1");
            } , executor);

            future.onSuccess(result -> {
                if(sleep2!=null){
                    TestUtils.sleep(sleep2);
                }
                output.append(result);
                finalizeTest();
            });
            future.onFailure(t -> {
                if(sleep2!=null){
                    TestUtils.sleep(sleep2);
                }
                output.append(t.getMessage());
                finalizeTest();
            });
        } else{
            // Assert
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "e1").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }


    @Test
    @Configuration(repetitions=1, threadPool=8)
    public void test3() {
        if(testPhase()){
            // Test
            Future<String> future = Future.future(() -> {
                TestUtils.sleep(1000);
                throw new RuntimeException("r1");
            }, executor);

            future.onFailure(failure -> {
                output.append(failure.getMessage());
            });

            TestUtils.sleep(2000);

            // executor.submit(future);
            future.onSuccess(result -> {
                output.append(result);
            });
            TestUtils.sleep(100);
            finalizeTest();
        } else{
            // Assert
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "r1").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

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
    public void test4_1() {
        if(testPhase()){
            // Test
            Future<String> future0 = Future.future(()->{
                return "l0"; 
            }, executor);

            future0.onComplete((failureL0, resultL0) ->{
                Future<String> future1 = Future.future(() ->{
                    if(failureL0!=null){
                        throw (RuntimeException)failureL0;
                    }
                    return resultL0 + " l1";
                }, executor); 
                future1.onComplete((failureL1, resultL1) ->{
                    Future<String> future2 = Future.future(() ->{
                        if(failureL1!=null){
                            throw (RuntimeException)failureL0;
                        }
                        return resultL1+" l2";
                    }, executor);

                    future2.onComplete((failureL2, resultL2) ->{
                        if(failureL2==null){
                            output.append(String.format("success: %s ", resultL2));
                        } else{
                            output.append(String.format("failure: '%s' ", failureL2.getMessage()));
                        }
                        finalizeTest();
                    });
                });
            });

        } else{
            // Assert
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "success: l0 l1 l2 ").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

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
    public void test4_2() {
        if(testPhase()){
            // Test
            Future<String> future0 = Future.future(()->{
                throw new IllegalStateException("el0"); 
            }, executor);

            future0.onComplete((failureL0, resultL0) ->{
                Future<String> future1 = Future.future(() ->{
                    if(failureL0!=null){
                        throw (RuntimeException)failureL0;
                    }
                    throw new IllegalStateException("el1"); 
                }, executor); 
                future1.onComplete((failureL1, resultL1) ->{
                    Future<String> future2 = Future.future(() ->{
                        if(failureL1!=null){
                            throw (RuntimeException)failureL1;
                        }
                        throw new IllegalStateException("el2"); 
                    }, executor);

                    future2.onComplete((failureL2, resultL2) ->{
                        if(failureL2==null){
                            output.append(String.format("success: %s ", resultL2));
                        } else{
                            output.append(String.format("failure: %s ", failureL2.getMessage()));
                        }
                        finalizeTest();
                    });
                });
            });

        } else{
            // Assert
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "failure: el0 ").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }


    @Test
    @Configuration(repetitions=1000,  threadPool=1,    fail1="fail1")
    @Configuration(repetitions=1000,  threadPool=8,    fail1="fail1")

    @Configuration(repetitions=1000,  threadPool=1,    fail2="fail2")
    @Configuration(repetitions=1000,  threadPool=8,    fail2="fail2")

    @Configuration(repetitions=1000,  threadPool=1,    fail3="fail3")
    @Configuration(repetitions=1000,  threadPool=8,    fail3="fail3")

    @Configuration(repetitions=1000,  threadPool=1,    fail4="fail4")
    @Configuration(repetitions=1000,  threadPool=8,    fail4="fail4")

    @Configuration(repetitions=1000,  threadPool=1,    fail5="fail5")
    @Configuration(repetitions=1000,  threadPool=8,    fail5="fail5")

    @Configuration(repetitions=1000,  threadPool=1,    fail1="fail1",   fail2="fail2",   fail3="fail3",   fail4="fail4",   fail5="fail5")
    @Configuration(repetitions=1000,  threadPool=8,    fail1="fail1",   fail2="fail2",   fail3="fail3",   fail4="fail4",   fail5="fail5")
    public void test5_1() {
        if(testPhase()){
            // Test
            Future<String> future0 = Future.future(()->{
                return process("level0", fail1); 
            }, executor);

            future0.onComplete((failureL0, resultL0) ->{
                Future<String> future1 = Future.future(() ->{
                    if(failureL0!=null){
                        throw (RuntimeException)failureL0;
                    }
                    return process(resultL0 + " level1", fail2);
                }, executor); 

                future1.onComplete((failureL1, resultL1) ->{
                    Future<Future<String>> future2 = Future.future(() ->{
                        if(failureL1!=null){
                            throw (RuntimeException)failureL1;
                        }
                        return processF(resultL1+" level2", fail3, fail4, executor);
                    }, executor);


                    future2.onComplete((failureL2, resultL2F) ->{
                        Promise<String> promise3 = Promise.promise();
                        if(failureL2==null){
                            resultL2F.onComplete((failureL2_2, resultL2) -> {
                                try{
                                    if(failureL2_2==null){
                                        promise3.success(process(resultL2+" level3", fail5));
                                    } else{
                                        promise3.failure(failureL2_2);
                                    }
                                } catch(Exception e){
                                    promise3.failure(e);
                                }
                            });
                        } else{
                            promise3.failure(failureL2);
                        }

                        promise3.future().onComplete((failureL3, resultL3) ->{
                            if(failureL3==null){
                                output.append(String.format("success: %s ", resultL3));
                            } else{
                                output.append(String.format("failure: %s ", failureL3.getMessage()));
                            }
                            finalizeTest();
                        });

                    });
                });
            });

        } else{
            // Assert
            String resultValue, resultType;
            if(fail1!=null){
                resultValue = fail1;
                resultType = "failure";
            } else if(fail2!=null){
                resultValue = fail2;
                resultType = "failure";
            } else if(fail3!=null){
                resultValue = fail3;
                resultType = "failure";
            } else if(fail4!=null){
                resultValue = fail4;
                resultType = "failure";
            } else if(fail5!=null){
                resultValue = fail5;
                resultType = "failure";
            } else {
                resultValue = "level0 level1 level2 level3";
                resultType = "success";
            }
            String expected = IntStream.range(0, repetitions).mapToObj(i -> String.format("%s: %s ", resultType, resultValue)).collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

    /**
     * Same as previous but done with map/flatMap.
     */
    @Test
    @Configuration(repetitions=1000,  threadPool=1,    fail1="fail1")
    @Configuration(repetitions=1000,  threadPool=8,    fail1="fail1")

    @Configuration(repetitions=1000,  threadPool=1,    fail2="fail2")
    @Configuration(repetitions=1000,  threadPool=8,    fail2="fail2")

    @Configuration(repetitions=1000,  threadPool=1,    fail3="fail3")
    @Configuration(repetitions=1000,  threadPool=8,    fail3="fail3")

    @Configuration(repetitions=1000,  threadPool=1,    fail4="fail4")
    @Configuration(repetitions=1000,  threadPool=8,    fail4="fail4")

    @Configuration(repetitions=1000,  threadPool=1,    fail5="fail5")
    @Configuration(repetitions=1000,  threadPool=8,    fail5="fail5")

    @Configuration(repetitions=1000,  threadPool=1,    fail1="fail1",   fail2="fail2",   fail3="fail3",   fail4="fail4",   fail5="fail5")
    @Configuration(repetitions=1000,  threadPool=8,    fail1="fail1",   fail2="fail2",   fail3="fail3",   fail4="fail4",   fail5="fail5")
    public void test5_2() {
        if(testPhase()){
            Future.future(() -> {
                return process("level0", fail1); 
            }, executor).map(resultL0 -> {
                return process(resultL0 + " level1", fail2);
            }).flatMap(resultL1 -> {
                return processF(resultL1+" level2", fail3, fail4, executor);
            }).map(resultL2 -> {
                return process(resultL2+" level3", fail5);
            }).onComplete((failureL3, resultL3) ->{
                if(failureL3==null){
                    output.append(String.format("success: %s ", resultL3));
                } else{
                    output.append(String.format("failure: %s ", failureL3.getMessage()));
                }
                finalizeTest();
            });
        } else{
            // Assert
            String resultValue, resultType;
            if(fail1!=null){
                resultValue = fail1;
                resultType = "failure";
            } else if(fail2!=null){
                resultValue = fail2;
                resultType = "failure";
            } else if(fail3!=null){
                resultValue = fail3;
                resultType = "failure";
            } else if(fail4!=null){
                resultValue = fail4;
                resultType = "failure";
            } else if(fail5!=null){
                resultValue = fail5;
                resultType = "failure";
            } else {
                resultValue = "level0 level1 level2 level3";
                resultType = "success";
            }
            String expected = IntStream.range(0, repetitions).mapToObj(i -> String.format("%s: %s ", resultType, resultValue)).collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions=10000, threadPool=1)
    @Configuration(repetitions=10000, threadPool=8)

    @Configuration(repetitions=10000, threadPool=1, fail1="fail1")
    @Configuration(repetitions=10000, threadPool=8, fail1="fail1")

    @Configuration(repetitions=10000, threadPool=1, fail2="fail2")
    @Configuration(repetitions=10000, threadPool=8, fail2="fail2")

    @Configuration(repetitions=10000, threadPool=1, fail3="fail3")
    @Configuration(repetitions=10000, threadPool=8, fail3="fail3")

    @Configuration(repetitions=10000, threadPool=1, fail4="fail4")
    @Configuration(repetitions=10000, threadPool=8, fail4="fail4")

    @Configuration(repetitions=10000, threadPool=1, fail5="fail5")
    @Configuration(repetitions=10000, threadPool=8, fail5="fail5")

    @Configuration(repetitions=10000, threadPool=1, fail1="fail1", fail6="fail6")
    @Configuration(repetitions=10000, threadPool=8, fail1="fail1", fail6="fail6")
    public void test6(){
        if(testPhase()){
            // Test
            Future<String> future = Future.future(()->{
                if(fail3!=null){
                    throw new RuntimeException(fail3);
                }
                return process("p1", fail1);
            }, executor).map(process1Result->{
                if(fail4!=null){
                    throw new RuntimeException(fail4);
                }
                if("p1".equals(process1Result)){
                    if(fail5!=null){
                        throw new RuntimeException(fail5);
                    }
                    return process("p2", fail2);
                } else{
                    if(fail6!=null){
                        throw new RuntimeException(fail6);
                    }
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
                } else if(fail3!=null){
                    expected.append(fail3);
                } else if(fail4!=null){
                    expected.append(fail4);
                } else if(fail5!=null){
                    expected.append(fail5);
                } else if(fail6!=null){
                    expected.append(fail6);
                } else{
                    expected.append("p2");
                }
            });
            Assert.assertEquals(expected.toString(), output.toString());
        }
    }

}
