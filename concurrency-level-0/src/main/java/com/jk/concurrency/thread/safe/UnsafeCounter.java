package com.jk.concurrency.thread.safe;

public class UnsafeCounter {
    private int counter;

    public int getCounter() {
        return counter;
    }

    public void incrementCounter() {
        counter++;
    }


}
