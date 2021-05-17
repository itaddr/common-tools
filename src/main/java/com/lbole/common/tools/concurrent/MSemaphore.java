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
package com.lbole.common.tools.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @Author 马嘉祺
 * @Date 2020/3/1 0001 11 02
 * @Description <p></p>
 */
public class MSemaphore {
    
    private final Semaphore semaphore;
    
    public MSemaphore(int permits) {
        this.semaphore = new Semaphore(permits);
    }
    
    public MSemaphore(int permits, boolean fair) {
        this.semaphore = new Semaphore(permits, fair);
    }
    
    public ReleaseOnlyOnce acquire() throws InterruptedException {
        semaphore.acquire(1);
        return new ReleaseOnlyOnce(semaphore);
    }
    
    public ReleaseOnlyOnce acquireUninterruptibly() {
        semaphore.acquireUninterruptibly(1);
        return new ReleaseOnlyOnce(semaphore);
    }
    
    public ReleaseOnlyOnce tryAcquire() {
        if (semaphore.tryAcquire()) {
            new ReleaseOnlyOnce(semaphore);
        }
        return null;
    }
    
    public ReleaseOnlyOnce tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        if (semaphore.tryAcquire(1, timeout, unit)) {
            return new ReleaseOnlyOnce(semaphore);
        }
        return null;
    }
    
    public ReleaseOnlyOnce tryAcquireUninterruptibly(long timeout, TimeUnit unit) {
        return tryAcquireUninterruptibly(1, timeout, unit);
    }
    
    
    public ReleaseOnlyOnce acquire(int permits) throws InterruptedException {
        semaphore.acquire(permits);
        return new ReleaseOnlyOnce(semaphore, permits);
    }
    
    public ReleaseOnlyOnce acquireUninterruptibly(int permits) {
        semaphore.acquireUninterruptibly(permits);
        return new ReleaseOnlyOnce(semaphore, permits);
    }
    
    public ReleaseOnlyOnce tryAcquire(int permits) {
        if (semaphore.tryAcquire(permits)) {
            return new ReleaseOnlyOnce(semaphore, permits);
        }
        return null;
    }
    
    public ReleaseOnlyOnce tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
        if (semaphore.tryAcquire(permits, timeout, unit)) {
            return new ReleaseOnlyOnce(semaphore, permits);
        }
        return null;
    }
    
    public ReleaseOnlyOnce tryAcquireUninterruptibly(int permits, long timeout, TimeUnit unit) {
        if (permits < 0 || timeout < 0) {
            throw new IllegalArgumentException();
        }
        boolean acquire = false;
        long remaining = unit.toNanos(timeout);
        long beginTimeNanos = System.nanoTime(), endTimeNanos;
        while (!acquire && remaining > 0) {
            try {
                acquire = semaphore.tryAcquire(permits, remaining, TimeUnit.NANOSECONDS);
                remaining = 0;
            } catch (InterruptedException ignored) {
                remaining -= (endTimeNanos = System.nanoTime()) - beginTimeNanos;
                beginTimeNanos = endTimeNanos;
            }
        }
        return acquire ? new ReleaseOnlyOnce(semaphore, permits) : null;
    }
    
    
    public int availablePermits() {
        return semaphore.availablePermits();
    }
    
    public int drainPermits() {
        return semaphore.drainPermits();
    }
    
    public boolean isFair() {
        return semaphore.isFair();
    }
    
    public final boolean hasQueuedThreads() {
        return semaphore.hasQueuedThreads();
    }
    
    public final int getQueueLength() {
        return semaphore.getQueueLength();
    }
    
    @Override
    public int hashCode() {
        return semaphore.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return semaphore.equals(obj);
    }
    
    @Override
    public String toString() {
        return semaphore.toString();
    }
    
}
