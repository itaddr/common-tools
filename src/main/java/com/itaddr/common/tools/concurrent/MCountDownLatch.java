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
package com.itaddr.common.tools.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author 马嘉祺
 * @Date 2020/3/4 0004 10 59
 * @Description <p></p>
 */
public class MCountDownLatch {
    
    private final CountDownLatch countDownLatch;
    
    public MCountDownLatch() {
        this.countDownLatch = new CountDownLatch(1);
    }
    
    public MCountDownLatch(int count) {
        this.countDownLatch = new CountDownLatch(count);
    }
    
    public void await() throws InterruptedException {
        countDownLatch.await();
    }
    
    public void awaitUninterruptibly() {
        while (countDownLatch.getCount() > 0) {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignored) {
            }
        }
    }
    
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return countDownLatch.await(timeout, unit);
    }
    
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }
        long remaining = unit.toNanos(timeout);
        long beginTimeNanos = System.nanoTime(), endTimeNanos;
        while (countDownLatch.getCount() > 0 && remaining > 0) {
            try {
                return countDownLatch.await(remaining, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ignored) {
                remaining -= (endTimeNanos = System.nanoTime()) - beginTimeNanos;
                beginTimeNanos = endTimeNanos;
            }
        }
        return false;
    }
    
    public void countDown() {
        countDownLatch.countDown();
    }
    
    public long getCount() {
        return countDownLatch.getCount();
    }
    
    @Override
    public int hashCode() {
        return countDownLatch.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return countDownLatch.equals(obj);
    }
    
    @Override
    public String toString() {
        return countDownLatch.toString();
    }
    
}
