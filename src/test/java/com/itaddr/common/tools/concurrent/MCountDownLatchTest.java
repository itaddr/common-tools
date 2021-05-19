package com.itaddr.common.tools.concurrent;

import com.itaddr.common.tools.utils.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author 马嘉祺
 * @Date 2020/3/4 0004 14 20
 * @Description <p></p>
 */
public class MCountDownLatchTest {
    
    @Test
    public void tryAcquireInterruptiblyTest() {
        AtomicBoolean stopping = new AtomicBoolean(false);
        MCountDownLatch countDownLatch = new MCountDownLatch();
        
        Thread thread = new Thread(() -> {
            System.out.println("[thread test] begin await");
            long beginTimeNanos = System.nanoTime();
            try {
                countDownLatch.await(5, TimeUnit.SECONDS);
                long endTimeNanos = System.nanoTime();
                System.out.println("[thread test] success await, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            } catch (InterruptedException e) {
                long endTimeNanos = System.nanoTime();
                System.out.println("[thread test] failure await, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            }
            stopping.set(true);
        });
        thread.start();
        
        ThreadUtil.sleepUninterruptibly(2, TimeUnit.SECONDS);
        System.out.println(">>> interrupt thread test <<<");
        thread.interrupt();
        
        while (!stopping.get()) {
            ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
    }
    
    @Test
    public void tryAcquireUninterruptiblyTest() {
        AtomicBoolean stopping = new AtomicBoolean(false);
        MCountDownLatch countDownLatch = new MCountDownLatch();
        
        Thread thread = new Thread(() -> {
            System.out.println("[thread test] begin await");
            long beginTimeNanos = System.nanoTime();
            countDownLatch.awaitUninterruptibly(5, TimeUnit.SECONDS);
            long endTimeNanos = System.nanoTime();
            System.out.println("[thread test] success await, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            stopping.set(true);
        });
        thread.start();
        
        ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);
        System.out.println(">>> interrupt thread test <<<");
        thread.interrupt();
        ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);
        System.out.println(">>> interrupt thread test <<<");
        thread.interrupt();
        ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);
        System.out.println(">>> interrupt thread test <<<");
        thread.interrupt();
        
        while (!stopping.get()) {
            ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
    }
    
}
