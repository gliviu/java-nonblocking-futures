package com.github.gliviu.javaNonblockingFutures.unitTests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.gliviu.javaNonblockingFutures.Future;

import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.TestUtils;

/**
 * Test {@link Future#timeout(Duration)}.
 */
public class TestTimeout extends BaseUnitTests {

    @Test
    @Configuration(repetitions = 1, threadPool = 8)
    public void test1() {
        if (testPhase()) {
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(() -> {
                TestUtils.sleep(100);
                return "s1";
            }, executor));
            futures.add(Future.timeout(2000, TimeUnit.MILLISECONDS));
            Future.first(futures).onComplete((fail, result) -> {
                if (fail != null && fail instanceof TimeoutException) {
                    output.append("timeout");
                } else if (fail!=null){
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
    public void test2() {
        if (testPhase()) {
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(() -> {
                TestUtils.sleep(200);
                return "s1";
            }, executor));
            futures.add(Future.timeout(100, TimeUnit.MILLISECONDS));
            Future.first(futures).onComplete((fail, result) -> {
                if (fail != null && fail instanceof TimeoutException) {
                    output.append("timeout");
                } else if (fail!=null){
                    output.append(fail.getMessage());
                } else {
                    output.append(result);
                }
                finalizeTest();
            });

        } else {
            // Verify
            Assert.assertEquals("timeout", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 8)
    public void test3() {
        if (testPhase()) {
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(() -> {
                TestUtils.sleep(200);
                throw new RuntimeException("f1");
            }, executor));
            futures.add(Future.timeout(100, TimeUnit.MILLISECONDS));
            Future.first(futures).onComplete((fail, result) -> {
                if (fail != null && fail instanceof TimeoutException) {
                    output.append("timeout");
                } else if (fail!=null){
                    output.append(fail.getMessage());
                } else {
                    output.append(result);
                }
                finalizeTest();
            });

        } else {
            // Verify
            Assert.assertEquals("timeout", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 8)
    public void test4() {
        if (testPhase()) {
            // Test
            List<Future<String>> futures = new ArrayList<>();
            futures.add(Future.future(() -> {
                TestUtils.sleep(100);
                throw new RuntimeException("f1");
            }, executor));
            futures.add(Future.timeout(2000, TimeUnit.MILLISECONDS));
            Future.first(futures).onComplete((fail, result) -> {
                if (fail != null && fail instanceof TimeoutException) {
                    output.append("timeout");
                } else if (fail!=null){
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

}
