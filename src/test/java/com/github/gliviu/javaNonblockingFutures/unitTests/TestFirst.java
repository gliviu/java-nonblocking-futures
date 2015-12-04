package com.github.gliviu.javaNonblockingFutures.unitTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.gliviu.javaNonblockingFutures.Future;
import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.TestUtils;

/**
 * Tests {@link Future#first(Iterable)}
 *
 */
public class TestFirst extends BaseUnitTests {

    @Test
    @Configuration(repetitions = 25, threadPool = 1, sequential = true, sleep1 = "0", sleep2 = "500")
    @Configuration(repetitions = 25, threadPool = 4, sequential = true, sleep1 = "0", sleep2 = "500")
    @Configuration(repetitions = 25, threadPool = 8, sequential = true, sleep1 = "0", sleep2 = "500")
    @Configuration(repetitions = 25, threadPool = 16, sequential = true, sleep1 = "0", sleep2 = "500")
    public void test1() {
        int futureNo = threadPool;
        if (testPhase()) {
            // Test
            int granularity = 100; // Timeouts should be quite different. So we set them to be multiple of 'granularity' milliseconds.
            int[] timeouts = IntStream.range(0, futureNo).map(i -> {
                int timeout = TestUtils.random(sleep1, sleep2);
                timeout = timeout - timeout % granularity;
                return timeout;
            }).toArray();
            OptionalInt min = Arrays.stream(timeouts).min();

            List<Future<Integer>> futures = new ArrayList<>();
            IntStream.range(0, futureNo).forEach(i -> futures.add(Future.future(() -> {
                int sleepMilli = timeouts[i];

                if (sleepMilli != 0) {
                    TestUtils.sleep(sleepMilli);
                }
                return sleepMilli;
            } , executor)));
            Future.first(futures).onSuccess(result -> {
                output.append(result.equals(min.getAsInt()));
            });
            Future.all(futures).onSuccess(results -> {
                finalizeTest();
            });
        } else {
            // Verify
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "true").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 1)
    public void test2() {
        if (testPhase()) {
            // Test
        	Future.first(Future.successful("s1")).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append(fail.getMessage());
                } else {
                    output.append(result);
                }
                finalizeTest();
            });

        } else {
            // Verify
            Assert.assertEquals("s1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 1)
    public void test3() {
        if (testPhase()) {
            // Test
        	Future.first(Future.failed(new RuntimeException("f1"))).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append(fail.getMessage());
                } else {
                    output.append(result);
                }
                finalizeTest();
            });

        } else {
            // Verify
            Assert.assertEquals("f1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 8)
    public void test4() {
        if (testPhase()) {
            // Test
            Future<String> future1 = Future.future(() -> {
                TestUtils.sleep(100);
                return "s1";
            }, executor);;
            Future<String> future2 = Future.future(() -> {
                TestUtils.sleep(200);
                throw new RuntimeException("f1");
            }, executor);
            Future.first(future1, future2).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append(fail.getMessage());
                } else {
                    output.append(result);
                }
                finalizeTest();
            });

        } else {
            // Verify
            Assert.assertEquals("s1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 8)
    public void test5() {
        if (testPhase()) {
            // Test
            Future<String> future1 = Future.future(() -> {
                TestUtils.sleep(200);
                return "s1";
            }, executor);
            Future<String> future2 = Future.future(() -> {
                TestUtils.sleep(100);
                throw new RuntimeException("f1");
            }, executor);
            Future.first(future1, future2).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append(fail.getMessage());
                } else {
                    output.append(result);
                }
                finalizeTest();
            });

        } else {
            // Verify
            Assert.assertEquals("f1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 8)
    public void test6() {
        if (testPhase()) {
            // Test
            Future<String> future1 = Future.future(() -> {
                TestUtils.sleep(100);
                throw new RuntimeException("f1");
            }, executor);
            Future<String> future2 = Future.future(() -> {
                TestUtils.sleep(200);
                throw new RuntimeException("f2");
            }, executor);
            Future.first(future1, future2).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append(fail.getMessage());
                } else {
                    output.append(result);
                }
                finalizeTest();
            });

        } else {
            // Verify
            Assert.assertEquals("f1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 1)
    public void test7() {
        if (testPhase()) {
            // Test
        	Future.first().onComplete((fail, result) -> {
                if (fail != null) {
                    output.append(fail.getMessage());
                } else {
                    output.append(result);
                }
            });
            finalizeTest();
            TestUtils.sleep(1000);
            output.append("done");
        } else {
            // Verify
            Assert.assertEquals("done", output.toString());
        }
    }


}
