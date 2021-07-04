package com.jk.concurrency.thread.safe;

public class ThreadSafeCounter {

    private volatile int count;

    public ThreadSafeCounter(int count) {
        this.count = count;
    }

    public ThreadSafeCounter() {
    }

    public int getCount() {
        return count;
    }

    public synchronized void increment() {
        count++;
    }
}
