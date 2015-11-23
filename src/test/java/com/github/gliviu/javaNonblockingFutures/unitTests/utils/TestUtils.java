package com.github.gliviu.javaNonblockingFutures.unitTests.utils;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.gliviu.javaNonblockingFutures.Future;

public class TestUtils {
    public final static Random RANDOM_GENERATOR = new Random(System.currentTimeMillis());
    public static void shutdown(ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("task intrerupted");
        } finally {
            if (!executor.isTerminated()) {
                System.out.println("force termination");
            }
            executor.shutdownNow();
        }
    }

    public static void waitFuture(final Future<?>... futures) {
        Arrays.stream(futures).forEach(future -> {
            try {
                future.waitResult();
            } catch (final Throwable e) {
                // Ignore.
            }
        });
    }

    public static void sleep(final int i) {
        try {
            Thread.sleep(i);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int random(int start, int end) {
        return start + RANDOM_GENERATOR.nextInt(end+1);
    }

}
