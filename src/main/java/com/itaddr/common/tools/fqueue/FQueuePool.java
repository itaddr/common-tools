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
package com.itaddr.common.tools.fqueue;

import com.itaddr.common.tools.utils.ThreadUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 队列池
 *
 * @Author 马嘉祺
 * @Date 2018/10/22 0022 14 28
 * @Description
 */
public class FQueuePool {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FQueuePool.class);
    
    private static final int DEFAULT_BLOCK_SIZE = 1024 * 1024 * 1024;
    private static final int MIN_BLOCK_SIZE = 32 * 1024 * 1024;
    private static final int MAX_BLOCK_SIZE = DEFAULT_BLOCK_SIZE;
    private static final String BLOCK_SIZE_WARNING = "队列默认块文件大小为%d，必须大于等于" + MIN_BLOCK_SIZE + "并且小于等于" + MAX_BLOCK_SIZE;
    
    private static final Pattern QUEUE_NAME_PATTERN = Pattern.compile("^[A-Za-z][_\\-.0-9A-Za-z]{1,63}$");
    
    private static final String EXPIRED_QUEUE_BLOCK = "__expired_queue_block";
    
    private String rootPath;
    
    private int defaultBlockSize;
    
    private ConcurrentMap<String, FQueue> queueTables;
    
    private FQueue expiredBlocks;
    
    private ScheduledThreadPoolExecutor asyncWorkerExecutor;
    
    private FQueuePool(final String rootPath, int asyncThreadNum, int defaultBlockSize, int asyncFlushSec, int deleteBlockSec) {
        // 队列文件根目录
        this.rootPath = rootPath;
        this.defaultBlockSize = defaultBlockSize;
        this.asyncWorkerExecutor = new ScheduledThreadPoolExecutor(asyncThreadNum, ThreadUtil.factory("FQueueAsyncWorker-"));
        // 如果目录不存在则创建
        final File rootPathDir = new File(rootPath);
        try {
            if (!rootPathDir.exists() && !rootPathDir.mkdirs()) {
                throw new IllegalArgumentException("创建队列根路径失败");
            }
        } catch (SecurityException e) {
            throw new IllegalArgumentException("创建队列根路径失败，由安全管理器造成", e);
        }
        
        // 扫描目录下文件，并初始化相应的队列
        this.queueTables = scanDir(rootPathDir);
        this.expiredBlocks = queueTables.containsKey(EXPIRED_QUEUE_BLOCK) ? queueTables.get(EXPIRED_QUEUE_BLOCK) : createNotExistQueue(EXPIRED_QUEUE_BLOCK, MIN_BLOCK_SIZE);
        
        this.asyncWorkerExecutor.scheduleWithFixedDelay(() -> {
            for (FQueue queue : queueTables.values()) {
                queue.sync();
            }
        }, asyncFlushSec, asyncFlushSec, TimeUnit.SECONDS);
        this.asyncWorkerExecutor.scheduleWithFixedDelay(this::deleteBlockFile, deleteBlockSec, deleteBlockSec, TimeUnit.SECONDS);
        LOGGER.info("文件队列池初始化成功:\n\trootPath={}\n\tasyncThreadNum={}\n\tdefaultBlockSize={}\n\tasyncFlushSec={}\n\tdeleteBlockSec={}",
                rootPath, asyncThreadNum, defaultBlockSize, asyncFlushSec, deleteBlockSec);
    }
    
    /**
     * 根据队列文件初始化已存在的队列
     *
     * @param fileBackupDir
     * @return
     */
    private ConcurrentMap<String, FQueue> scanDir(File fileBackupDir) {
        
        // 判断文件是否为目录
        if (!fileBackupDir.isDirectory()) {
            throw new IllegalArgumentException("这并不是一个目录");
        }
        final ConcurrentMap<String, FQueue> exitsQueues = new ConcurrentHashMap<>(32);
        
        // 获取所有队列目录，并恢复目录中的队列数据
        final File[] queueDirs = fileBackupDir.listFiles((dir, name) -> new File(FQueueOffset.formatFilePath(name, rootPath)).exists());
        
        if (null != queueDirs && ArrayUtils.isNotEmpty(queueDirs)) {
            for (final File queueDir : queueDirs) {
                // 创建队列
                exitsQueues.put(queueDir.getName(), new FQueue(queueDir.getName(), rootPath, this, DEFAULT_BLOCK_SIZE));
            }
        }
        
        return exitsQueues;
    }
    
    /**
     * 标记块文件可以被删除
     *
     * @param blockPath
     */
    void toClear(String blockPath) {
        expiredBlocks.add(new FQueueRecord(System.currentTimeMillis(), null, blockPath.getBytes(StandardCharsets.UTF_8)));
    }
    
    /**
     * 删除已消费的队列块文件
     */
    private void deleteBlockFile() {
        int size = expiredBlocks.size();
        for (int i = 0; i < size; ++i) {
            // 从标记队列中拉取数据
            FQueueRecord record = expiredBlocks.poll();
            if (null == record) {
                break;
            }
            byte[] recordValue = record.getRecordValue();
            String fileName;
            if (ArrayUtils.isEmpty(recordValue) || StringUtils.isBlank(fileName = new String(recordValue, StandardCharsets.UTF_8))) {
                continue;
            }
            File file = new File(fileName);
            if (!file.exists()) {
                continue;
            }
            try {
                if (!file.delete()) {
                    expiredBlocks.offer(new FQueueRecord(System.currentTimeMillis(), null, recordValue));
                    LOGGER.warn("删除过期块文件失败: {}", fileName);
                }
            } catch (SecurityException e) {
                expiredBlocks.offer(new FQueueRecord(System.currentTimeMillis(), null, recordValue));
                LOGGER.warn("删除过期块文件失败，安全管理器拒绝删除: {}", fileName);
            }
        }
    }
    
    /**
     * 释放队列池
     */
    public synchronized void destroy() {
        try {
            asyncWorkerExecutor.shutdown();
            asyncWorkerExecutor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
        
        queueTables.remove(EXPIRED_QUEUE_BLOCK);
        for (final FQueue queue : queueTables.values()) {
            queue.close();
        }
        
        deleteBlockFile();
        expiredBlocks.close();
        
        expiredBlocks = null;
        queueTables.clear();
        
    }
    
    /**
     * 获取队列池中的队列
     *
     * @param queueName
     * @return
     */
    public FQueue getQueue(String queueName) {
        if (StringUtils.isBlank(queueName)) {
            throw new IllegalArgumentException("队列名称不能为空");
        }
        if (EXPIRED_QUEUE_BLOCK.equals(queueName)) {
            throw new IllegalArgumentException(String.format("%s为内部保留的队列，不允许外部获取", queueName));
        }
        return queueTables.get(queueName);
    }
    
    /**
     * 创建队列
     *
     * @param queueName
     * @param blockSize
     * @return
     */
    private FQueue createNotExistQueue(String queueName, int blockSize) {
        if (queueTables.containsKey(queueName)) {
            return null;
        }
        final FQueue queue = new FQueue(queueName, rootPath, this, blockSize);
        queueTables.put(queueName, queue);
        return queue;
    }
    
    public synchronized FQueue createQueue(String queueName, int blockSize) {
        if (StringUtils.isBlank(queueName)) {
            throw new IllegalArgumentException("队列名称不能为空");
        }
        if (!QUEUE_NAME_PATTERN.matcher(queueName).find()) {
            throw new IllegalArgumentException(String.format("队列名称长度只能大于等于2小于等于64，只能是_ - . 0-9 A-Z a-z等字符组成，并且只能以A-Z a-z等字符开头: %s", queueName));
        }
        if (blockSize < MIN_BLOCK_SIZE || blockSize > MAX_BLOCK_SIZE) {
            throw new IllegalArgumentException(String.format("%s队列块文件大小为%d不合法，必须大于等于%d并且小于等于%d", queueName, blockSize, MIN_BLOCK_SIZE, MAX_BLOCK_SIZE));
        }
        return createNotExistQueue(queueName, blockSize);
    }
    
    public synchronized FQueue createQueue(String queueName) {
        return createNotExistQueue(queueName, defaultBlockSize);
    }
    
    public synchronized FQueue getOrCreateQueue(String queueName, int blockSize) {
        if (StringUtils.isBlank(queueName)) {
            throw new IllegalArgumentException("队列名称不能为空");
        }
        if (!QUEUE_NAME_PATTERN.matcher(queueName).find()) {
            throw new IllegalArgumentException(String.format("队列名称长度只能大于等于2小于等于64，只能是_ - . 0-9 A-Z a-z等字符组成，并且只能以A-Z a-z等字符开头: %s", queueName));
        }
        FQueue queue = queueTables.get(queueName);
        if (null == queue) {
            if (blockSize < MIN_BLOCK_SIZE || blockSize > MAX_BLOCK_SIZE) {
                throw new IllegalArgumentException(String.format("%s队列块文件大小为%d不合法，必须大于等于%d并且小于等于%d", queueName, blockSize, MIN_BLOCK_SIZE, MAX_BLOCK_SIZE));
            }
            queue = createNotExistQueue(queueName, blockSize);
        }
        return queue;
    }
    
    public synchronized FQueue getOrCreateQueue(String queueName) {
        return getOrCreateQueue(queueName, defaultBlockSize);
    }
    
    public static Builder builder(String rootPath) {
        return new Builder(rootPath);
    }
    
    public static class Builder {
        
        private String rootPath;
        private Integer defaultBlockSize;
        private Integer flushThreadNum;
        private Integer asyncFlushSec;
        private Integer deleteBlockSec;
        
        private Builder(String rootPath) {
            this.rootPath = Optional.ofNullable(rootPath).filter(StringUtils::isNotBlank).orElseThrow(() -> new IllegalStateException("队列根目录不能为空"));
        }
        
        public Builder setDefaultBlockSize(int size) {
            this.defaultBlockSize = Optional.of(size).filter(v -> v >= MIN_BLOCK_SIZE && v <= MAX_BLOCK_SIZE).orElseThrow(() -> new IllegalArgumentException(String.format(BLOCK_SIZE_WARNING, size)));
            return this;
        }
        
        public Builder setFlushThreadNum(int num) {
            this.flushThreadNum = Optional.of(num).filter(v -> v > 1).orElseThrow(() -> new IllegalArgumentException("异步刷盘线程数不能为: " + num));
            return this;
        }
        
        public Builder setAsyncFlushSec(int sec) {
            this.asyncFlushSec = Optional.of(sec).filter(v -> v > 1).orElseThrow(() -> new IllegalArgumentException(String.format("异步刷盘时间为%ds，间隔不能小于1", sec)));
            return this;
        }
        
        public Builder setDeleteBlockSec(int sec) {
            this.deleteBlockSec = Optional.of(sec).filter(v -> v > 1).orElseThrow(() -> new IllegalArgumentException(String.format("删除队列块间隔%ds，间隔不能小于1", sec)));
            return this;
        }
        
        public FQueuePool build() {
            int defaultBlockSize = Optional.ofNullable(this.defaultBlockSize).orElse(1024 * 1024 * 1024);
            int flushThreadNum = Optional.ofNullable(this.flushThreadNum).orElse(1);
            int asyncFlushSec = Optional.ofNullable(this.asyncFlushSec).orElse(1);
            int deleteBlockSec = Optional.ofNullable(this.deleteBlockSec).orElse(30);
            return new FQueuePool(rootPath, defaultBlockSize, flushThreadNum, asyncFlushSec, deleteBlockSec);
        }
    }
    
}
