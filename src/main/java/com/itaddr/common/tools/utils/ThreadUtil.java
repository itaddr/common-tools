/*
 *  The lBole licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.itaddr.common.tools.utils;

import com.itaddr.common.tools.exception.NotCaptureException;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author 马嘉祺
 * @Date 2020/3/4 0004 10 15
 * @Description <p></p>
 */
public final class ThreadUtil {
    
    public static final Predicate1 DEFAULT_AWAKE = () -> false;
    
    private ThreadUtil() {
    }
    
    public static ThreadFactory factory(String threadNamePrefix) {
        return new ThreadFactory() {
            private AtomicInteger threadNum = new AtomicInteger(0);
            
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, threadNamePrefix + threadNum.getAndIncrement());
            }
        };
    }
    
    public static void sleep(long timeout, TimeUnit unit) throws InterruptedException {
        long remaining = unit.toNanos(timeout);
        Thread.sleep(remaining / 1000000L, (int) (remaining % 1000000L));
    }
    
    public static void sleepUninterruptibly(long timeout, TimeUnit unit) {
        long remaining = unit.toNanos(timeout);
        long beginTimeNanos = System.nanoTime(), endTimeNanos;
        while (remaining > 0) {
            try {
                Thread.sleep(remaining / 1000000L, (int) (remaining % 1000000L));
                return;
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                remaining -= (endTimeNanos = System.nanoTime()) - beginTimeNanos;
                beginTimeNanos = endTimeNanos;
            }
        }
    }
    
    public static void join(Thread thread) throws InterruptedException {
        if (null == thread || !thread.isAlive()) {
            return;
        }
        thread.join();
    }
    
    public static void join(Thread thread, long timeout, TimeUnit unit) throws InterruptedException {
        if (null == thread || !thread.isAlive()) {
            return;
        }
        long remaining = unit.toNanos(timeout);
        thread.join(remaining / 1000000L, (int) (remaining % 1000000L));
    }
    
    public static void joinUninterruptibly(Thread thread) {
        if (null == thread || !thread.isAlive()) {
            return;
        }
        boolean isInterrupted = false;
        while (thread.isAlive()) {
            try {
                thread.join();
                if (!thread.isInterrupted() && isInterrupted) {
                    thread.interrupt();
                }
                return;
            } catch (InterruptedException ignored) {
                isInterrupted = true;
            }
        }
    }
    
    public static void joinUninterruptibly(Thread thread, long timeout, TimeUnit unit) {
        if (null == thread || !thread.isAlive()) {
            return;
        }
        boolean isInterrupted = false;
        long remaining = unit.toNanos(timeout);
        long beginTimeNanos = System.nanoTime(), endTimeNanos;
        while (thread.isAlive() && remaining > 0) {
            try {
                thread.join(remaining / 1000000L, (int) (remaining % 1000000L));
                if (!thread.isInterrupted() && isInterrupted) {
                    thread.interrupt();
                }
                return;
            } catch (InterruptedException ignored) {
                remaining -= (endTimeNanos = System.nanoTime()) - beginTimeNanos;
                beginTimeNanos = endTimeNanos;
                isInterrupted = true;
            }
        }
    }
    
    public static void uninterruptibly(Predicate1 awake, Consumer1 await) {
        if (null == awake || null == await) {
            throw new NullPointerException();
        }
        while (!awake.test()) {
            try {
                await.accept();
                return;
            } catch (InterruptedException ignored) {
            } catch (Throwable e) {
                throw new NotCaptureException(e);
            }
        }
    }
    
    public static void uninterruptibly(Predicate1 awake, Consumer2 await, long timeoutMs) {
        if (null == await) {
            throw new IllegalArgumentException("await不能为空");
        }
        awake = null == awake ? DEFAULT_AWAKE : awake;
        long remaining = timeoutMs * 1000000;
        long beginTimeNanos = System.nanoTime(), endTimeNanos;
        while (remaining > 0 && !awake.test()) {
            try {
                await.accept(remaining / 1000000L);
                return;
            } catch (InterruptedException ignored) {
                remaining -= (endTimeNanos = System.nanoTime()) - beginTimeNanos;
                beginTimeNanos = endTimeNanos;
            } catch (Throwable e) {
                throw new NotCaptureException(e);
            }
        }
    }
    
    public static void uninterruptibly(Predicate1 awake, Consumer3 await, long timeoutMs, int nanos) {
        if (null == await) {
            throw new IllegalArgumentException("await不能为空");
        }
        awake = null == awake ? DEFAULT_AWAKE : awake;
        long remaining = timeoutMs * 1000000 + nanos;
        long beginTimeNanos = System.nanoTime(), endTimeNanos;
        while (remaining > 0 && !awake.test()) {
            try {
                await.accept(remaining / 1000000L, (int) (remaining % 1000000L));
                return;
            } catch (InterruptedException ignored) {
                remaining -= (endTimeNanos = System.nanoTime()) - beginTimeNanos;
                beginTimeNanos = endTimeNanos;
            } catch (Throwable e) {
                throw new NotCaptureException(e);
            }
        }
    }
    
    public static void uninterruptibly(Predicate1 awake, Consumer4 await, long timeout, TimeUnit unit) {
        if (null == await) {
            throw new IllegalArgumentException("await不能为空");
        }
        awake = null == awake ? DEFAULT_AWAKE : awake;
        long remaining = unit.toNanos(timeout);
        long beginTimeNanos = System.nanoTime(), endTimeNanos;
        while (remaining > 0 && !awake.test()) {
            try {
                await.accept(remaining, TimeUnit.NANOSECONDS);
                return;
            } catch (InterruptedException ignored) {
                remaining -= (endTimeNanos = System.nanoTime()) - beginTimeNanos;
                beginTimeNanos = endTimeNanos;
            } catch (Throwable e) {
                throw new NotCaptureException(e);
            }
        }
    }
    
    public interface Consumer1 {
        void accept() throws InterruptedException;
    }
    
    public interface Consumer2 {
        void accept(long timeoutMs) throws InterruptedException;
    }
    
    public interface Consumer3 {
        void accept(long timeoutMs, int nanos) throws InterruptedException;
    }
    
    public interface Consumer4 {
        void accept(long timeout, TimeUnit unit) throws InterruptedException;
    }
    
    public interface Predicate1 {
        boolean test();
    }
    
}
