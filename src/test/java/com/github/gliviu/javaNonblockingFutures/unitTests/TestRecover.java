package com.github.gliviu.javaNonblockingFutures.unitTests;

import com.github.gliviu.javaNonblockingFutures.Future;
import com.github.gliviu.javaNonblockingFutures.Future.RecoverHandler;
import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;

/**
 * Tests {@link Future#recover(RecoverHandler)}
 *
 */
public class TestRecover extends BaseUnitTests {
    @Test
    @Configuration(repetitions = 1, threadPool = 1)
    public void test1() {
        if (testPhase()) {
            // Test
            Future.successful("s1").recover(t -> {
                return t.getMessage();
            }).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append("fail: " + fail.getMessage());
                } else {
                    output.append("success: " + result);
                }
                finalizeTest();
            });
        } else {
            // Verify
            Assert.assertEquals("success: s1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 1)
    public void test2() {
        if (testPhase()) {
            // Test
            Future.failed(new RuntimeException("f1")).recover(t -> {
                return t.getMessage();
            }).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append("fail: " + fail.getMessage());
                } else {
                    output.append("success: " + result);
                }
                finalizeTest();
            });
        } else {
            // Verify
            Assert.assertEquals("success: f1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 1)
    public void test3() {
        if (testPhase()) {
            // Test
            Future.successful("s1").recover(t -> {
                throw new RuntimeException("f2");
            }).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append("fail: " + fail.getMessage());
                } else {
                    output.append("success: " + result);
                }
                finalizeTest();
            });
        } else {
            // Verify
            Assert.assertEquals("success: s1", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 1)
    public void test4() {
        if (testPhase()) {
            // Test
            Future.failed(new RuntimeException("f1")).recover(t -> {
                throw new RuntimeException("f2");
            }).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append("fail: " + fail.getMessage());
                } else {
                    output.append("success: " + result);
                }
                finalizeTest();
            });
        } else {
            // Verify
            Assert.assertEquals("fail: f2", output.toString());
        }
    }

    @Test
    @Configuration(repetitions = 1, threadPool = 1)
    public void test5() {
        if (testPhase()) {
            // Test
            Future.failed(new RuntimeException("f1")).recover(t -> {
                throw new RuntimeException("f2");
            }).recover(t ->{
                return t.getMessage();
            }).onComplete((fail, result) -> {
                if (fail != null) {
                    output.append("fail: " + fail.getMessage());
                } else {
                    output.append("success: " + result);
                }
                finalizeTest();
            });
        } else {
            // Verify
            Assert.assertEquals("success: f2", output.toString());
        }
    }

}
