package com.jk.concurrency.thread.safe;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class ThreadUnsafeCounterTest {

    @Test
    public void unsafe() {
        UnsafeCounter unsafeCounter = new UnsafeCounter();
        ExecutorService runner = Executors.newFixedThreadPool(10);
        List<Future<Void>> futures = new ArrayList<>();
        IntStream.range(0, 20000).forEach(i -> {
            Future<Void> future = (Future<Void>) runner.submit(() -> unsafeCounter.incrementCounter());
            futures.add(future);
        });
        futures.forEach(i -> {
            try {
                i.get();
            } catch (InterruptedException e) {
               throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Counter value is: " + unsafeCounter.getCounter());
        Assert.assertNotEquals(20000, unsafeCounter.getCounter());
    }

}
