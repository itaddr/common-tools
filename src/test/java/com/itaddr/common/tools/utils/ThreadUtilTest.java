package com.itaddr.common.tools.utils;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author 马嘉祺
 * @Date 2020/3/5 0005 22 16
 * @Description <p></p>
 */
public class ThreadUtilTest {
    
    @Test
    public void sleep1() throws InterruptedException {
        long beginTimeMs = System.currentTimeMillis();
        Thread.sleep(2000);
        long endTimeMs = System.currentTimeMillis();
        System.out.println(endTimeMs - beginTimeMs);
    }
    
    @Test
    public void sleepUninterruptibly() {
        long beginTimeMs = System.currentTimeMillis();
        ThreadUtil.sleepUninterruptibly(2, TimeUnit.SECONDS);
        long endTimeMs = System.currentTimeMillis();
        System.out.println(endTimeMs - beginTimeMs);
    }
    
    @Test
    public void join1() {
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000);
        
        AtomicBoolean stopping = new AtomicBoolean(false);
        
        Thread thread1 = new Thread(() -> ThreadUtil.sleepUninterruptibly(5, TimeUnit.SECONDS));
        
        Thread thread2 = new Thread(() -> {
            thread1.start();
            
            System.out.println("[thread test] begin join");
            long beginTimeNanos = System.nanoTime();
            try {
                /*ThreadUtil.join(thread1);*/
                thread1.join();
                long endTimeNanos = System.nanoTime();
                System.out.println("[thread test] success join, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            } catch (InterruptedException e) {
                long endTimeNanos = System.nanoTime();
                System.out.println("[thread test] failure join, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            }
            stopping.set(true);
        });
        thread2.start();
        
        /*ThreadUtil.sleepUninterruptibly(2, TimeUnit.SECONDS);*/
        ThreadUtil.uninterruptibly(null, Thread::sleep, 2000, 0);
        System.out.println(">>> interrupt thread test <<<");
        thread2.interrupt();
        
        while (!stopping.get()) {
            /*ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);*/
            ThreadUtil.uninterruptibly(null, Thread::sleep, 1000, 0);
        }
    }
    
    @Test
    public void join2() {
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000);
        
        AtomicBoolean stopping = new AtomicBoolean(false);
        
        Thread thread1 = new Thread(() -> ThreadUtil.sleepUninterruptibly(5, TimeUnit.SECONDS));
        
        Thread thread2 = new Thread(() -> {
            thread1.start();
            
            System.out.println("[thread test] begin join");
            long beginTimeNanos = System.nanoTime();
            try {
                /*ThreadUtil.join(thread1, 2, TimeUnit.SECONDS);*/
                thread1.join(2000, 0);
                long endTimeNanos = System.nanoTime();
                System.out.println("[thread test] success join, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            } catch (InterruptedException e) {
                long endTimeNanos = System.nanoTime();
                System.out.println("[thread test] failure join, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            }
            stopping.set(true);
        });
        thread2.start();
        
        /*ThreadUtil.sleepUninterruptibly(3, TimeUnit.SECONDS);*/
        ThreadUtil.uninterruptibly(null, Thread::sleep, 3000, 0);
        System.out.println(">>> interrupt thread test <<<");
        thread2.interrupt();
        
        while (!stopping.get()) {
            /*ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);*/
            ThreadUtil.uninterruptibly(null, Thread::sleep, 1000, 0);
        }
    }
    
    @Test
    public void joinUninterruptibly1() {
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000);
        
        AtomicBoolean stopping = new AtomicBoolean(false);
        
        Thread thread1 = new Thread(() -> ThreadUtil.uninterruptibly(null, Thread::sleep, 5000, 0));
        
        Thread thread2 = new Thread(() -> {
            thread1.start();
            
            System.out.println("[thread test] begin join");
            long beginTimeNanos = System.nanoTime();
            /*ThreadUtil.joinUninterruptibly(thread1);*/
            ThreadUtil.uninterruptibly(() -> !thread1.isAlive(), thread1::join);
            long endTimeNanos = System.nanoTime();
            System.out.println("[thread test] success join, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            stopping.set(true);
        });
        thread2.start();
        
        /*ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);*/
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000, 0);
        System.out.println(">>> interrupt thread test <<<");
        thread2.interrupt();
        
        /*ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);*/
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000, 0);
        System.out.println(">>> interrupt thread test <<<");
        thread2.interrupt();
        
        while (!stopping.get()) {
            /*ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);*/
            ThreadUtil.uninterruptibly(null, Thread::sleep, 1000, 0);
        }
    }
    
    @Test
    public void joinUninterruptibly2() {
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000);
        
        AtomicBoolean stopping = new AtomicBoolean(false);
        
        Thread thread1 = new Thread(() -> ThreadUtil.uninterruptibly(null, Thread::sleep, 10000, 0));
        
        Thread thread2 = new Thread(() -> {
            thread1.start();
            
            System.out.println("[thread test] begin join");
            long beginTimeNanos = System.nanoTime();
            /*ThreadUtil.joinUninterruptibly(thread1, 4, TimeUnit.SECONDS);*/
            ThreadUtil.uninterruptibly(null, thread1::join, 4000, 0);
            long endTimeNanos = System.nanoTime();
            System.out.println("[thread test] success join, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            stopping.set(true);
        });
        thread2.start();
        
        /*ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);*/
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000, 0);
        System.out.println(">>> interrupt thread test <<<");
        thread2.interrupt();
        
        /*ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);*/
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000, 0);
        System.out.println(">>> interrupt thread test <<<");
        thread2.interrupt();
        
        while (!stopping.get()) {
            /*ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);*/
            ThreadUtil.uninterruptibly(null, Thread::sleep, 1000, 0);
        }
        
    }
    
    @Test
    public void uninterruptibly1() {
    
    }
    
    @Test
    public void uninterruptibly2() {
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000);
        
        long beginTimeMs = System.currentTimeMillis();
        ThreadUtil.uninterruptibly(null, Thread::sleep, 2000);
        long endTimeMs = System.currentTimeMillis();
        System.out.println(endTimeMs - beginTimeMs);
    }
    
    @Test
    public void uninterruptibly3() {
        ThreadUtil.uninterruptibly(null, Thread::sleep, 1000, 0);
        
        long beginTimeMs = System.currentTimeMillis();
        ThreadUtil.uninterruptibly(null, Thread::sleep, 2000, 0);
        long endTimeMs = System.currentTimeMillis();
        System.out.println(endTimeMs - beginTimeMs);
    }
    
    @Test
    public void uninterruptibly4() {
    
    }
    
}
