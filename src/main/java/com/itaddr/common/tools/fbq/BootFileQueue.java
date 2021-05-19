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
package com.itaddr.common.tools.fbq;

import com.itaddr.common.tools.fbq.exception.QueueExistsException;
import com.itaddr.common.tools.fbq.exception.QueueNotExistsException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author 马嘉祺
 * @Date 2020/3/21 0021 17 33
 * @Description <p></p>
 */
public class BootFileQueue {
    
    public static final int MAGIC = 'A' << 3 | 'B' << 2 | 'C' << 1 | 'D';
    public static final int VERSION = 0x00010000;
    public static final int EOF = -1;
    
    private static int DEFAULT_BLOCK_SIZE = 1024 * 1024 * 1024;
    private static int MIN_BLOCK_SIZE = 32 * 1024 * 1024;
    private static int MAX_BLOCK_SIZE = DEFAULT_BLOCK_SIZE;
    private static String BLOCK_SIZE_WARNING = "队列默认块文件大小为%d，必须大于等于" + MIN_BLOCK_SIZE + "并且小于等于" + MAX_BLOCK_SIZE;
    
    private String rootDirPath;
    private Integer defaultSegSize;
    private Integer asyncThreadNum;
    private Integer asyncFlushSec;
    private Integer deleteSegSec;
    
    private QueueTimer flushAndDelTimer;
    
    private ConcurrentMap<String, FileQueue> fileQueueTable;
    
    private BootFileQueue(String rootDirPath, Integer defaultSegSize, Integer asyncThreadNum, Integer asyncFlushSec, Integer deleteSegSec) {
        this.rootDirPath = rootDirPath;
        this.defaultSegSize = defaultSegSize;
        this.asyncThreadNum = asyncThreadNum;
        this.asyncFlushSec = asyncFlushSec;
        this.deleteSegSec = deleteSegSec;
        this.flushAndDelTimer = new QueueTimer();
        this.fileQueueTable = new ConcurrentHashMap<>();
        flushAndDelTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                
                } catch (Exception ignore) {
                }
            }
        }, 3000, 10000);
    }
    
    private class QueueTimer extends Timer {
    }
    
    public void shutdown() {
        flushAndDelTimer.cancel();
    }
    
    public String getRootDirPath() {
        return rootDirPath;
    }
    
    public Integer getDefaultSegSize() {
        return defaultSegSize;
    }
    
    public Integer getAsyncThreadNum() {
        return asyncThreadNum;
    }
    
    public Integer getAsyncFlushSec() {
        return asyncFlushSec;
    }
    
    public Integer getDeleteSegSec() {
        return deleteSegSec;
    }
    
    /**
     * 获取队列
     *
     * @param queueName 队列名称
     * @return 返回队列
     */
    public FileQueue getQueue(String queueName) {
        return null;
    }
    
    /**
     * 创建队列
     *
     * @param queueName    队列名称
     * @param segSize      队列块大小
     * @param mustNotExist 队列已存在的时候是否抛出{@link QueueExistsException}异常
     * @return 返回队列
     * @throws QueueExistsException {@param mustNotExist}为true的时候，如果队列已存在则抛出该异常
     */
    public synchronized FileQueue createQueue(String queueName, int segSize, boolean mustNotExist) throws QueueExistsException {
        return null;
    }
    
    /**
     * 删除队列
     *
     * @param queueName 队列名称
     * @param mustExist 队列不存在的时候是否抛出{@link QueueNotExistsException}异常
     * @throws QueueNotExistsException {@param mustExist}为true的时候，如果队列不存在则抛出该异常
     */
    public synchronized void removeQueue(String queueName, boolean mustExist) throws QueueNotExistsException {
    
    }
    
    public static class Builder {
        
        
        public BootFileQueue build() {
            return null;
        }
        
    }
    
}
