package com.jk.explore.java.core.concurrency.aba;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
// Taken from:
// https://www.baeldung.com/java-atomicmarkablereference
// https://www.baeldung.com/java-atomicstampedreference
// https://www.baeldung.com/cs/aba-concurrency
// https://www.baeldung.com/java-common-concurrency-pitfalls
public class Account {

    private AtomicInteger balance;
    private AtomicInteger transactions;
    private ThreadLocal<Integer> casFailureCount;

    public boolean deposit(int amount) {
        int current = balance.get();
        boolean result = balance.compareAndSet(current, current + amount);
        if (result) {
            transactions.incrementAndGet();
        } else {
            int currentCASFailureCount = casFailureCount.get();
            casFailureCount.set(currentCASFailureCount + 1);
        }
        return result;
    }

    public boolean withdraw(int amount) throws InterruptedException {
        int current = getBalance().get();
        waitOnCondition();
        boolean result = balance.compareAndSet(current, current - amount);
        if (result) {
            transactions.incrementAndGet();
        } else {
            int currentCASFailureCount = casFailureCount.get();
            casFailureCount.set(currentCASFailureCount + 1);
        }
        return result;
    }

    private void waitOnCondition() throws InterruptedException {
        if ("thread1".equals(Thread.currentThread().getName())) {
            TimeUnit.SECONDS.sleep(2);
        }
    }

    public AtomicInteger getBalance() {
        return balance;
    }

    public void setBalance(AtomicInteger balance) {
        this.balance = balance;
    }

    public AtomicInteger getTransactions() {
        return transactions;
    }

    public void setTransactions(AtomicInteger transactions) {
        this.transactions = transactions;
    }

    public ThreadLocal<Integer> getCasFailureCount() {
        return casFailureCount;
    }

    public void setCasFailureCount(ThreadLocal<Integer> casFailureCount) {
        this.casFailureCount = casFailureCount;
    }
}
