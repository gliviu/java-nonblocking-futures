package com.github.gliviu.javaNonblockingFutures.unitTests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.github.gliviu.javaNonblockingFutures.Future;
import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.TestUtils;

/**
 * Test {@link Future#waitResult()}
 */
public class TestWaitResult extends BaseUnitTests {
    @Configuration(repetitions = 1, threadPool = 1)
    @Test
    public void test1() {
        if (testPhase()) {
            // Test
            Future<String> future = Future.successful("s1");
            try {
                output.append("success: " + future.waitResult());
            } catch (Throwable t) {
                output.append(t.getMessage());
            }
            finalizeTest();
        } else {
            // Verify
            String expected = "success: s1";
            Assert.assertEquals(expected, output.toString());
        }

    }

    @Configuration(repetitions = 1, threadPool = 1)
    @Test
    public void test2() {
        if (testPhase()) {
            // Test
            Future<String> future = Future.failed(new RuntimeException("f1"));
            try {
                output.append("success: " + future.waitResult());
            } catch (Throwable t) {
                output.append("fail: " + t.getMessage());
            }
            finalizeTest();
        } else {
            // Verify
            String expected = "fail: f1";
            Assert.assertEquals(expected, output.toString());
        }

    }



    @Configuration(repetitions = 30, threadPool = 1, sleep1 = "0", sleep2 = "10", option1="10")
    @Configuration(repetitions = 30, threadPool = 8, sleep1 = "0", sleep2 = "10", option1="100")
    @Configuration(repetitions = 50, threadPool = 1, sleep1 = "0", sleep2 = "1", option1="80")
    @Configuration(repetitions = 100, threadPool = 8, sleep1 = "0", sleep2 = "1", option1="200")
    @Test
    public void test3() {
        int futureNo = Integer.parseInt(option1);
        if (testPhase()) {
            // Test
            List<Future<String>> futures = new ArrayList<>();
            IntStream.range(0, futureNo).forEach(i -> futures.add(Future.future(() -> {
                int sleepMilli = TestUtils.random(sleep1, sleep2);
                if(sleepMilli!=0){
                    TestUtils.sleep(sleepMilli);
                }
                int random = TestUtils.random(0, 1);
                if(random==0){
                    throw new RuntimeException("f1");
                }
                return "s1";
            } , executor)));
            for (Future<String> future : futures) {
                try {
                    output.append(String.format("success: %s;", future.waitResult()));
                } catch (Throwable t) {
                    output.append(String.format("fail: %s;", t.getMessage()));
                }
            }
            finalizeTest();
        } else {
            // Verify
            int errorNo = 0, successNo = 0;
            for(String s : output.toString().split(";")){
                if(s.equals("fail: f1")){
                    errorNo++;
                }
                if(s.equals("success: s1")){
                    successNo++;
                }
            }
            int expected = futureNo*repetitions;
            Assert.assertEquals(expected, errorNo+successNo);
        }

    }

}
