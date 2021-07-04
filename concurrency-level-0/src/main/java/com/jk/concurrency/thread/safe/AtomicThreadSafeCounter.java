package com.jk.concurrency.thread.safe;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicThreadSafeCounter {

    private AtomicInteger counter;

    public int getCount() {
        return counter.get();
    }

    public void increment() {
        boolean notIncremented = true;
        while(notIncremented) {
            int currentValue = counter.get();
            int newValue = currentValue+1;
            notIncremented = ! counter.compareAndSet(currentValue, newValue);
        }
    }
}
