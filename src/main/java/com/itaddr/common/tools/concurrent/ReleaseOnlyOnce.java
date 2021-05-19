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

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 仅能被释放一次的信号量
 *
 * @author mjq
 */
public class ReleaseOnlyOnce {
    
    private final AtomicBoolean released = new AtomicBoolean(false);
    private final Semaphore semaphore;
    private final int releaseNum;
    
    public ReleaseOnlyOnce(Semaphore semaphore) {
        this.semaphore = semaphore;
        this.releaseNum = 1;
    }
    
    public ReleaseOnlyOnce(Semaphore semaphore, int releaseNum) {
        this.semaphore = semaphore;
        this.releaseNum = releaseNum;
    }
    
    public void release() {
        if (null == semaphore) {
            return;
        }
        if (released.compareAndSet(false, true)) {
            semaphore.release(releaseNum);
        }
    }
    
    public Semaphore getSemaphore() {
        return semaphore;
    }
    
    public int getReleaseNum() {
        return releaseNum;
    }
}

