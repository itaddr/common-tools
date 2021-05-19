package com.itaddr.common.tools.concurrent;

import com.itaddr.common.tools.utils.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author 马嘉祺
 * @Date 2020/3/4 0004 13 59
 * @Description <p></p>
 */
public class MSemaphoreTest {
    
    @Test
    public void tryAcquireInterruptiblyTest() {
        AtomicBoolean stopping = new AtomicBoolean(false);
        MSemaphore semaphore = new MSemaphore(1);
        
        semaphore.tryAcquireUninterruptibly(1, 5, TimeUnit.SECONDS);
        System.out.println("[thread main] success acquire\n===============================");
        
        Thread thread = new Thread(() -> {
            System.out.println("[thread test] begin acquire");
            long beginTimeNanos = System.nanoTime();
            try {
                semaphore.tryAcquire(1, 5, TimeUnit.SECONDS);
                long endTimeNanos = System.nanoTime();
                System.out.println("[thread test] success acquire, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
            } catch (InterruptedException e) {
                long endTimeNanos = System.nanoTime();
                System.out.println("[thread test] failure acquire, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
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
        MSemaphore semaphore = new MSemaphore(1);
        
        semaphore.tryAcquireUninterruptibly(1, 5, TimeUnit.SECONDS);
        System.out.println("[thread main] success acquire\n===============================");
        
        Thread thread = new Thread(() -> {
            System.out.println("[thread test] begin acquire");
            long beginTimeNanos = System.nanoTime();
            semaphore.tryAcquireUninterruptibly(1, 5, TimeUnit.SECONDS);
            long endTimeNanos = System.nanoTime();
            System.out.println("[thread test] success acquire, nanos=" + (endTimeNanos - beginTimeNanos) + "ns");
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
