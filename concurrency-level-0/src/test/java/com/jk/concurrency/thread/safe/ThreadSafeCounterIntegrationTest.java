package com.jk.concurrency.thread.safe;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class ThreadSafeCounterIntegrationTest {

    @Test
    public void threadSafeCounterWithIncrement_1() throws InterruptedException {
       ExecutorService runner = Executors.newFixedThreadPool(10);
       ThreadSafeCounter threadSafeCounter = new ThreadSafeCounter(0);
       IntStream.range(0, 2000).forEach(counter -> {
           runner.submit(() -> threadSafeCounter.increment());
       });
       runner.awaitTermination(1, TimeUnit.SECONDS);
       Assert.assertEquals(2000, threadSafeCounter.getCount());
    }

    @Test
    public void threadSafeCounterWithIncrement_2() {
        ExecutorService runner = Executors.newFixedThreadPool(10);
        ThreadSafeCounter threadSafeCounter = new ThreadSafeCounter(0);
        List<Future<Void>> futures = new ArrayList<>();
        IntStream.range(0, 2000).forEach(counter -> {
            Future<Void> future = (Future<Void>) runner.submit(() -> threadSafeCounter.increment());
            futures.add(future);
        });
        CompletableFuture<Void> cf = new CompletableFuture<>();

        IntStream.range(0, futures.size()).forEach(index -> {
            try {
                futures.get(index).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        Assert.assertEquals(2000, threadSafeCounter.getCount());
    }

    @Test
    public void threadSafeCounterWithIncrement_3() throws InterruptedException, ExecutionException {
        ThreadSafeCounter threadSafeCounter = new ThreadSafeCounter(0);
        List<CompletableFuture<?>> cf_all = new ArrayList<>();
        IntStream.range(0, 2000).forEach(counter -> {
            CompletableFuture<?> cf = CompletableFuture.runAsync(() -> threadSafeCounter.increment());
            cf_all.add(cf);
        });
        CompletableFuture<Void> combined_cf = null;
        for(CompletableFuture future : cf_all) {
            combined_cf = CompletableFuture.allOf(future);
        }
        combined_cf.get();
        Assert.assertEquals(2000, threadSafeCounter.getCount());
    }

    @Test
    public void threadSafeCounterWithIncrement_4() throws InterruptedException, ExecutionException {
        ThreadSafeCounter threadSafeCounter = new ThreadSafeCounter(0);
        List<CompletableFuture<Void>> cf_all = new ArrayList<>();
        IntStream.range(0, 10000).forEach(counter -> {
            CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> threadSafeCounter.increment());
            cf_all.add(cf);
        });
        Optional<CompletableFuture<Void>> results = cf_all.stream().reduce((cf1, cf2) -> CompletableFuture.allOf(cf1, cf2));
        CompletableFuture<Void> combinedFuture = results.get();
        combinedFuture.get();
        Assert.assertEquals(10000, threadSafeCounter.getCount());
    }

    @Test
    public void threadSafeAtomicInteger() throws InterruptedException, ExecutionException {
        AtomicInteger atomicCounter = new AtomicInteger(0);
        List<CompletableFuture<Void>> cf_all = new ArrayList<>();
        IntStream.range(0, 10000).forEach(counter -> {
            CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> atomicCounter.incrementAndGet());
            cf_all.add(cf);
        });
        Optional<CompletableFuture<Void>> results = cf_all.stream().reduce((cf1, cf2) -> CompletableFuture.allOf(cf1, cf2));
        CompletableFuture<Void> combinedFuture = results.get();
        combinedFuture.get();
        Assert.assertEquals(10000, atomicCounter.get());
    }
    
    @Test
    public void givenMultiThread_whenSafeCounterWithoutLockIncrement() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        AtomicThreadSafeCounter safeCounter = new AtomicThreadSafeCounter();

        IntStream.range(0, 1000)
          .forEach(count -> service.submit(safeCounter::increment));
        service.awaitTermination(100, TimeUnit.MILLISECONDS);

        assertEquals(1000, safeCounter.getCount());
    }
}
