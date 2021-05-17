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
package com.lbole.common.tools.fqueue;

import java.io.File;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 队列
 *
 * @Author 马嘉祺
 * @Date 2018/10/22 0022 14 29
 * @Description
 */
public class FQueue extends AbstractQueue<FQueueRecord> implements BlockingQueue<FQueueRecord> {
    
    static final int MAGIC_FLAG = 'A' << 3 | 'B' << 2 | 'C' << 1 | 'D';
    static final int VERSION_FLAG = 0x00010000;
    
    private final ReentrantLock writeLock = new ReentrantLock();
    private final ReentrantLock readLock = new ReentrantLock();
    private final Condition notEmpty = readLock.newCondition();
    private final ReentrantLock rotateLock = new ReentrantLock();
    
    private String queueName;
    
    private String rootPath;
    
    private FQueuePool queuePool;
    
    private AtomicLong size;
    
    private FQueueOffset offset;
    
    private volatile FQueueBlock readBlock;
    private volatile FQueueBlock writeBlock;
    
    FQueue(String queueName, String rootPath, FQueuePool queuePool, int blockSize) {
        this.queueName = queueName;
        this.rootPath = rootPath;
        this.queuePool = queuePool;
        
        // 判断如果队列目录不存在则需要创建
        final File queueDir = new File(this.rootPath + File.separator + this.queueName);
        if (!queueDir.exists() && !queueDir.mkdirs()) {
            throw new IllegalStateException("无法创建队列目录文件");
        }
        
        // 初始化偏移量
        this.offset = new FQueueOffset(FQueueOffset.formatFilePath(queueName, rootPath), blockSize);
        // 初始化队列大小
        this.size = new AtomicLong(offset.getWriteCounter() - offset.getReadCounter());
        // 初始化队列写块
        this.writeBlock = new FQueueBlock(offset, FQueueBlock.formatFilePath(queueName, offset.getWriteFileNum(), rootPath));
        
        // 初始化读块
        if (offset.getReadFileNum() == offset.getWriteFileNum()) {
            // 读文件号等于写文件号
            this.readBlock = this.writeBlock.duplicate();
        } else {
            this.readBlock = new FQueueBlock(offset, FQueueBlock.formatFilePath(queueName, offset.getReadFileNum(), rootPath));
        }
    }
    
    /**
     * 获取队列名称
     *
     * @return
     */
    public String getQueueName() {
        return this.queueName;
    }
    
    /**
     * 队列大小
     *
     * @return
     */
    @Override
    public int size() {
        return size.intValue();
    }
    
    /**
     * 遍历方法，暂时不实现
     *
     * @return
     */
    @Override
    public Iterator<FQueueRecord> iterator() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * 路由到下一个写块
     *
     * @param fileNum 下一个写入块的文件号
     */
    private void rotateNextWriteBlock(long fileNum) {
        // 为防止读块文件正在路由，判断下一个文件号是否等于写块文件号，所以这里需要加上个锁
        rotateLock.lock();
        try {
            // 向块文件中写入一个EOF
            writeBlock.putEOF();
            // 判断读块的文件号与写块的文件号是否相等
            if (offset.getReadFileNum() == offset.getWriteFileNum()) {
                // 如果相等则只需要将写入块刷盘，不能释放块文件的内存映射，因为读块可能正在进行映射
                writeBlock.sync();
            } else {
                // 不相等则可以直接释放内存映射
                writeBlock.close();
            }
            // 重新构造一个写入块
            writeBlock = new FQueueBlock(offset, FQueueBlock.formatFilePath(queueName, fileNum, rootPath));
            // 更新写入块文件号索引
            offset.putWriteFileNum(fileNum);
            offset.putWritePosition(FQueueBlock.CONTENT_BEGIN_OFFSET);
        } finally {
            rotateLock.unlock();
        }
    }
    
    private void signalNotEmpty() {
        final ReentrantLock readLock = this.readLock;
        readLock.lock();
        try {
            notEmpty.signal();
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 向队列中写入数据
     *
     * @param record
     * @return
     */
    @Override
    public boolean offer(FQueueRecord record) {
        if (null == record) {
            return false;
        }
        long count;
        writeLock.lock();
        try {
            // 获取消息记录ID
            long id = offset.getWriteCounter() + 1;
            record.setId(id);
            
            // 判断当前写入块是否还有空间写入当前数据
            if (!writeBlock.isSpaceAvailable(record.getLength())) {
                // 当前块文件没有空间写入数据，需要路由到下一个写入块进行写入文件
                rotateNextWriteBlock(id);
            }
            
            // 向队列中写入数据
            writeBlock.write(record);
            // 队列大小加一
            count = size.getAndIncrement();
        } finally {
            writeLock.unlock();
        }
        if (0 == count) {
            signalNotEmpty();
        }
        return true;
    }
    
    @Override
    public boolean offer(FQueueRecord record, long timeout, TimeUnit unit) throws InterruptedException {
        if (null == record) {
            return false;
        }
        long count;
        writeLock.lockInterruptibly();
        try {
            long id = offset.getWriteCounter() + 1;
            record.setId(id);
            if (!writeBlock.isSpaceAvailable(record.getLength())) {
                rotateNextWriteBlock(id);
            }
            writeBlock.write(record);
            count = size.getAndIncrement();
        } finally {
            writeLock.unlock();
        }
        if (0 == count) {
            signalNotEmpty();
        }
        return true;
    }
    
    @Override
    public void put(FQueueRecord record) throws InterruptedException {
        if (null == record) {
            return;
        }
        long count;
        writeLock.lockInterruptibly();
        try {
            long id = offset.getWriteCounter() + 1;
            record.setId(id);
            if (!writeBlock.isSpaceAvailable(record.getLength())) {
                rotateNextWriteBlock(id);
            }
            writeBlock.write(record);
            count = size.getAndIncrement();
        } finally {
            writeLock.unlock();
        }
        if (0 == count) {
            signalNotEmpty();
        }
    }
    
    /**
     * 队列间数据传输
     *
     * @param queue
     * @param size
     */
    public void transferFrom(FQueue queue, int size) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * 路由到下一个读块
     */
    private void rotateNextReadBlock() {
        // 判断下一个读块文件号与写块文件号是否相等，为防止写块正在路由，所以这里需要加上锁
        rotateLock.lock();
        try {
            // 获取下一个读块文件号
            long nextReadFileNum = offset.getReadCounter() + 1;
            // 获取当前读块文件路径
            String blockPath = readBlock.getFilePath();
            // 释放读当前读块
            readBlock.close();
            // 判断下一个读块文件号与写入块文件号是否相等
            if (nextReadFileNum == offset.getWriteFileNum()) {
                // 相等直接使用读块
                readBlock = writeBlock.duplicate();
            } else {
                // 不相等，需要实例化读块
                readBlock = new FQueueBlock(offset, FQueueBlock.formatFilePath(queueName, nextReadFileNum, rootPath));
            }
            // 更新读索引
            offset.putReadFileNum(nextReadFileNum);
            offset.putReadPosition(FQueueBlock.CONTENT_BEGIN_OFFSET);
            queuePool.toClear(blockPath);
        } finally {
            rotateLock.unlock();
        }
        
        // 标记块文件可以被删除
    }
    
    @Override
    public FQueueRecord peek() {
        readLock.lock();
        try {
            // 当前读块文件号小于写块文件号，并且当前读块文件已没有可读的数据
            if (offset.getReadFileNum() < offset.getWriteFileNum() && readBlock.eof()) {
                // 路由到下一个块文件
                rotateNextReadBlock();
            }
            return readBlock.peak();
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 从队列中拉取数据
     *
     * @return
     */
    @Override
    public FQueueRecord poll() {
        readLock.lock();
        try {
            if (offset.getReadFileNum() < offset.getWriteFileNum() && readBlock.eof()) {
                rotateNextReadBlock();
            }
            if (size.get() > 0) {
                FQueueRecord record = readBlock.read();
                // 将队列长度减一
                if (size.getAndDecrement() > 1) {
                    // 还有可读的数据，唤醒其他读线程
                    notEmpty.signal();
                }
                return record;
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public FQueueRecord poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        
        readLock.lockInterruptibly();
        try {
            // 阻塞等待队列中存在数据
            while (size.get() == 0) {
                if (nanos <= 0) {
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            if (offset.getReadFileNum() < offset.getWriteFileNum() && readBlock.eof()) {
                rotateNextReadBlock();
            }
            FQueueRecord record = readBlock.read();
            if (size.decrementAndGet() > 1) {
                notEmpty.signal();
            }
            return record;
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public FQueueRecord take() throws InterruptedException {
        readLock.lockInterruptibly();
        try {
            while (size.get() == 0) {
                notEmpty.await();
            }
            if (offset.getReadFileNum() < offset.getWriteFileNum() && readBlock.eof()) {
                rotateNextReadBlock();
            }
            FQueueRecord record = readBlock.read();
            if (size.decrementAndGet() > 1) {
                notEmpty.signal();
            }
            return record;
        } finally {
            readLock.unlock();
        }
    }
    
    public long peekTimeStamp() {
        readLock.lock();
        try {
            // 当前读块文件号小于写块文件号，并且当前读块文件已没有可读的数据
            if (offset.getReadFileNum() < offset.getWriteFileNum() && readBlock.eof()) {
                // 路由到下一个块文件
                rotateNextReadBlock();
            }
            return readBlock.peakTimeStamp();
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 队列间数据传输
     *
     * @param size
     * @param queue
     */
    public void transferTo(int size, FQueue queue) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public int drainTo(Collection c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int drainTo(Collection c, int maxElements) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * 索引和写块刷盘
     */
    public void sync() {
        offset.sync();
        writeBlock.sync();
    }
    
    /**
     * 释放队列相关资源
     */
    public void close() {
        // 释放写块资源
        writeBlock.close();
        // 判断读块文件号与写块文件号是否不相等
        if (offset.getReadFileNum() != offset.getWriteFileNum()) {
            // 文件号不相等，则需要额外释放读块的资源
            readBlock.close();
        }
        // 释放索引资源
        offset.close();
        writeBlock = null;
        readBlock = null;
        offset = null;
    }
    
}
