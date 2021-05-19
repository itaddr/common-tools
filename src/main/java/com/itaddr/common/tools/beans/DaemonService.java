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
package com.itaddr.common.tools.beans;

import com.itaddr.common.tools.utils.ThreadUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 一个持续执行的后台任务
 *
 * @Author 马嘉祺
 * @Date 2020/3/6 0006 10 45
 * @Description <p></p>
 */
public class DaemonService {
    
    private final AtomicInteger runningCounter = new AtomicInteger(0);
    private final AtomicInteger failureCounter = new AtomicInteger(0);
    private final AtomicLong runningTimeStamp = new AtomicLong(0);
    
    private final String serviceName;
    
    private final Runnable runnable;
    
    private volatile InternalThread thread;
    
    /**
     * @param serviceName 任务名称
     * @param runnable    任务实体
     */
    public DaemonService(String serviceName, Runnable runnable) {
        if (null == serviceName || 0 == serviceName.length()) {
            throw new IllegalArgumentException("serviceName不能为空");
        }
        if (null == runnable) {
            throw new IllegalArgumentException("runnable不能为空");
        }
        this.serviceName = serviceName;
        this.runnable = runnable;
    }
    
    /**
     * 启动任务
     *
     * @return
     */
    public synchronized DaemonService start() {
        final InternalThread thread = this.thread;
        if (null != thread) {
            if (thread.isShutdown()) {
                awaitUninterruptibly();
            } else {
                return this;
            }
        }
        this.thread = new InternalThread(serviceName, runnable);
        this.thread.start();
        return this;
    }
    
    /**
     * 暂停任务
     *
     * @return
     */
    public synchronized DaemonService stop() {
        final InternalThread thread = this.thread;
        if (null == thread) {
            return this;
        }
        thread.shutdown();
        return this;
    }
    
    /**
     * 判断任务是否正在暂停
     *
     * @return
     */
    public boolean isStopped() {
        final InternalThread thread = this.thread;
        return null == thread || thread.isShutdown();
    }
    
    /**
     * 判断任务是否还存活
     *
     * @return
     */
    public boolean isRunning() {
        final InternalThread thread = this.thread;
        return null != thread && thread.isAlive();
    }
    
    /**
     * 等待任务死亡
     *
     * @return
     * @throws InterruptedException
     */
    public DaemonService awaitStopped() throws InterruptedException {
        final InternalThread thread = this.thread;
        if (null != thread && thread.isAlive()) {
            thread.join();
        }
        return this;
    }
    
    /**
     * 等地啊任务死亡
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public DaemonService awaitStopped(long timeout, TimeUnit unit) throws InterruptedException {
        final InternalThread thread = this.thread;
        if (null != thread && thread.isAlive()) {
            ThreadUtil.join(thread, timeout, unit);
        }
        return this;
    }
    
    /**
     * 等待任务死亡
     *
     * @return
     */
    public DaemonService awaitUninterruptibly() {
        final InternalThread thread = this.thread;
        if (null != thread && thread.isAlive()) {
            ThreadUtil.joinUninterruptibly(thread);
        }
        return this;
    }
    
    /**
     * 等待任务死亡
     *
     * @param timeout
     * @param unit
     * @return
     */
    public DaemonService awaitUninterruptibly(long timeout, TimeUnit unit) {
        final InternalThread thread = this.thread;
        if (null != thread && thread.isAlive()) {
            ThreadUtil.joinUninterruptibly(thread, timeout, unit);
        }
        return this;
    }
    
    public int runningCounter() {
        return runningCounter.get();
    }
    
    public int failureCounter() {
        return failureCounter.get();
    }
    
    public long runningTimeStamp() {
        return runningTimeStamp.get();
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    private class InternalThread extends Thread {
        
        private final Runnable runnable;
        
        private volatile boolean shutdown;
        
        InternalThread(String threadName, Runnable runnable) {
            super(threadName);
            this.shutdown = false;
            this.runnable = runnable;
        }
        
        boolean isShutdown() {
            return shutdown;
        }
        
        boolean isTerminated() {
            return !isAlive();
        }
        
        void shutdown() {
            this.shutdown = true;
        }
        
        @Override
        public void run() {
            while (!shutdown) {
                long beginTimeMs = System.currentTimeMillis();
                try {
                    runnable.run(DaemonService.this);
                } catch (Exception ignored) {
                    failureCounter.getAndIncrement();
                } finally {
                    runningCounter.getAndIncrement();
                }
                long endTimeMs = System.currentTimeMillis();
                runningTimeStamp.getAndAdd(endTimeMs - beginTimeMs);
            }
        }
        
    }
    
    public interface Runnable {
        
        /**
         * 可执行的函数
         *
         * @param service
         */
        void run(DaemonService service);
        
    }
    
}
