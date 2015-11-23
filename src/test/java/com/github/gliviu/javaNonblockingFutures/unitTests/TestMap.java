package com.github.gliviu.javaNonblockingFutures.unitTests;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.gliviu.javaNonblockingFutures.Future;
import com.github.gliviu.javaNonblockingFutures.Future.Mapper;
import org.junit.Assert;
import org.junit.Test;

import com.github.gliviu.javaNonblockingFutures.unitTests.utils.BaseUnitTests;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.Configuration;
import com.github.gliviu.javaNonblockingFutures.unitTests.utils.TestUtils;

/**
 * Tests {@link Future#map(Mapper)}
 *
 */
public class TestMap extends BaseUnitTests {
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
    @Configuration(repetitions=30,     threadPool=8, sleep1="10")
    @Configuration(repetitions=1000,   threadPool=8, sleep1="10")
    @Configuration(repetitions=1,      threadPool=1, sleep1="10")
    @Configuration(repetitions=2,      threadPool=1, sleep1="10")
    @Configuration(repetitions=5,      threadPool=1, sleep1="10")
    @Configuration(repetitions=10,     threadPool=1, sleep1="10")
    @Configuration(repetitions=20,     threadPool=1, sleep1="10")

    @Configuration(repetitions=1,      threadPool=8, sleep2="10")
    @Configuration(repetitions=2,      threadPool=8, sleep2="10")
    @Configuration(repetitions=5,      threadPool=8, sleep2="10")
    @Configuration(repetitions=10,     threadPool=8, sleep2="10")
    @Configuration(repetitions=30,     threadPool=8, sleep2="10")
    @Configuration(repetitions=1,      threadPool=1, sleep2="10")
    @Configuration(repetitions=2,      threadPool=1, sleep2="10")
    @Configuration(repetitions=5,      threadPool=1, sleep2="10")
    @Configuration(repetitions=10,     threadPool=1, sleep2="10")
    @Configuration(repetitions=20,     threadPool=1, sleep2="10")

    @Configuration(repetitions=1,      threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=2,      threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=5,      threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=10,     threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=30,     threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=1,      threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=2,      threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=5,      threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=10,     threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=20,     threadPool=1, sleep1="10", sleep2="10")
    public void test1() {
        if(testPhase()){
            // Test
            final Future<String> future = Future.future(() -> {
                if(sleep1!=null){
                    TestUtils.sleep(sleep1);
                }
                return "s1";
            } , executor).map(s1 -> {
                if(sleep1!=null){
                    TestUtils.sleep(sleep1);
                }
                return s1+"s2";
            }).map(s2 -> {
                if(sleep1!=null){
                    TestUtils.sleep(sleep1);
                }
                return s2+"s3";
            });

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
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "s1s2s3").collect(Collectors.joining());
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

    @Configuration(repetitions=1,      threadPool=8, sleep1="10")
    @Configuration(repetitions=2,      threadPool=8, sleep1="10")
    @Configuration(repetitions=5,      threadPool=8, sleep1="10")
    @Configuration(repetitions=10,     threadPool=8, sleep1="10")
    @Configuration(repetitions=30,     threadPool=8, sleep1="10")
    @Configuration(repetitions=1000,   threadPool=8, sleep1="10")
    @Configuration(repetitions=1,      threadPool=1, sleep1="10")
    @Configuration(repetitions=2,      threadPool=1, sleep1="10")
    @Configuration(repetitions=5,      threadPool=1, sleep1="10")
    @Configuration(repetitions=10,     threadPool=1, sleep1="10")
    @Configuration(repetitions=20,     threadPool=1, sleep1="10")

    @Configuration(repetitions=1,      threadPool=8, sleep2="10")
    @Configuration(repetitions=2,      threadPool=8, sleep2="10")
    @Configuration(repetitions=5,      threadPool=8, sleep2="10")
    @Configuration(repetitions=10,     threadPool=8, sleep2="10")
    @Configuration(repetitions=30,     threadPool=8, sleep2="10")
    @Configuration(repetitions=1,      threadPool=1, sleep2="10")
    @Configuration(repetitions=2,      threadPool=1, sleep2="10")
    @Configuration(repetitions=5,      threadPool=1, sleep2="10")
    @Configuration(repetitions=10,     threadPool=1, sleep2="10")
    @Configuration(repetitions=20,     threadPool=1, sleep2="10")

    @Configuration(repetitions=1,      threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=2,      threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=5,      threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=10,     threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=30,     threadPool=8, sleep1="10", sleep2="10")
    @Configuration(repetitions=1,      threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=2,      threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=5,      threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=10,     threadPool=1, sleep1="10", sleep2="10")
    @Configuration(repetitions=20,     threadPool=1, sleep1="10", sleep2="10")
    public void test2() {
        if(testPhase()){
            // Test
            final Future<String> future = Future.future(() -> {
                if(sleep1!=null){
                    TestUtils.sleep(sleep1);
                }
                return "s1";
            } , executor).map(s1 -> {
                if(sleep1!=null){
                    TestUtils.sleep(sleep1);
                }
                throw new IllegalStateException(s1+"e2");    
            }).map(s2 -> {
                if(sleep1!=null){
                    TestUtils.sleep(sleep1);
                }
                return s2+"s3";
            });

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
            String expected = IntStream.range(0, repetitions).mapToObj(i -> "s1e2").collect(Collectors.joining());
            Assert.assertEquals(expected, output.toString());
        }
    }

}
