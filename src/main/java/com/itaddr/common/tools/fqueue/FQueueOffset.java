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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Cleaner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * 队列偏移量索引
 *
 * @Author 马嘉祺
 * @Date 2018/10/22 0022 14 30
 * @Description
 */
public class FQueueOffset {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FQueueOffset.class);
    
    /**
     * 偏移量文件后缀
     */
    private static final String OFFSET_FILE_NAME = "__queue_offset_checkpoint";
    
    /**
     * 偏移量文件大小
     */
    private static final int OFFSET_FILE_SIZE = 52;
    
    /**
     * 偏移量中各索引项数据在文件中的起始位置
     */
    private static final int BLOCK_SIZE_OFFSET = 8,
            READ_NUM_OFFSET = 12, READ_POS_OFFSET = 20, READ_CNT_OFFSET = 24,
            WRITE_NUM_OFFSET = 32, WRITE_POS_OFFSET = 40, WRITE_CNT_OFFSET = 44;
    
    /**
     * 块文件大小, 起始位置{@link #BLOCK_SIZE_OFFSET}
     */
    private final int blockSize;
    
    /**
     * 读块文件号, 起始位置{@link #READ_NUM_OFFSET}
     */
    private volatile long readFileNum;
    
    /**
     * 读偏移量位置值, 起始位置{@link #READ_POS_OFFSET}
     */
    private volatile int readPosition;
    
    /**
     * 已读计数器值, 起始位置{@link #READ_CNT_OFFSET}
     */
    private volatile long readCounter;
    
    /**
     * 写块文件号, 起始位置{@link #WRITE_NUM_OFFSET}
     */
    private volatile long writeFileNum;
    
    /**
     * 写索引位置, 起始位置{@link #WRITE_POS_OFFSET}
     */
    private volatile int writePosition;
    
    /**
     * 已写文计数器值, 起始位置{@link #WRITE_CNT_OFFSET}
     */
    private volatile long writeCounter;
    
    /**
     * 写偏移量缓冲
     */
    private MappedByteBuffer writeBuffer;
    
    /**
     * 读偏移量缓冲（由写索引复制而来）
     */
    private MappedByteBuffer readBuffer;
    
    FQueueOffset(String offsetFileName, int blockSize) {
        File file = new File(offsetFileName);
        RandomAccessFile accessFile = null;
        try {
            // 判断索引文件是否存在
            if (file.exists()) {
                accessFile = new RandomAccessFile(file, "rw");
                // 校验魔数
                if (FQueue.MAGIC_FLAG != accessFile.readInt()) {
                    throw new IllegalStateException(String.format("'%s'不是队列块量文件", offsetFileName));
                }
                // 校验版本
                int version;
                if (FQueue.VERSION_FLAG != (version = accessFile.readInt())) {
                    throw new IllegalStateException(String.format("偏移量版本期望是v%d.%d, 实际版本是v%d.%d", FQueue.VERSION_FLAG >>> 2, FQueue.VERSION_FLAG & 0xffff, version >>> 2, version & 0xffff));
                }
                // 获取偏移量中各个索引数据项
                this.blockSize = accessFile.readInt();
                this.readFileNum = accessFile.readLong();
                this.readPosition = accessFile.readInt();
                this.readCounter = accessFile.readLong();
                this.writeFileNum = accessFile.readLong();
                this.writePosition = accessFile.readInt();
                this.writeCounter = accessFile.readLong();
                // 获写读缓冲区
                this.writeBuffer = accessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, OFFSET_FILE_SIZE).load();
                // 将读缓冲区拷贝一份，数据共享，其他pos、limit、capacity等元数据独享
                this.readBuffer = (MappedByteBuffer) writeBuffer.duplicate();
            } else {
                this.blockSize = blockSize;
                accessFile = new RandomAccessFile(file, "rw");
                // 获写读缓冲区
                this.writeBuffer = accessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, OFFSET_FILE_SIZE).load();
                // 填充魔数、版本、块文件大小
                this.writeBuffer.putInt(0, FQueue.MAGIC_FLAG).putInt(4, FQueue.VERSION_FLAG).putInt(BLOCK_SIZE_OFFSET, blockSize);
                // 将读缓冲区拷贝一份，数据共享，其他pos、limit、capacity等元数据独享
                this.readBuffer = (MappedByteBuffer) writeBuffer.duplicate();
                // 初始化相关索引项
                putReadFileNum(0);
                putReadPosition(FQueueBlock.CONTENT_BEGIN_OFFSET);
                putReadCounter(0);
                putWriteFileNum(0);
                putWritePosition(FQueueBlock.CONTENT_BEGIN_OFFSET);
                putWriteCounter(0);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (null != accessFile) {
                try {
                    accessFile.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
    
    /**
     * 根据队列名称与队列文件根目录获取索引文件名称
     *
     * @param queueName
     * @param rootPath
     * @return
     */
    static String formatFilePath(String queueName, String rootPath) {
        return rootPath + File.separator + queueName + File.separator + OFFSET_FILE_NAME;
    }
    
    int getBlockSize() {
        return this.blockSize;
    }
    
    long getReadFileNum() {
        return this.readFileNum;
    }
    
    int getReadPosition() {
        return this.readPosition;
    }
    
    long getReadCounter() {
        return this.readCounter;
    }
    
    long getWriteFileNum() {
        return this.writeFileNum;
    }
    
    int getWritePosition() {
        return this.writePosition;
    }
    
    long getWriteCounter() {
        return this.writeCounter;
    }
    
    void putWritePosition(int writePosition) {
        this.writeBuffer.putInt(WRITE_POS_OFFSET, writePosition);
        this.writePosition = writePosition;
    }
    
    void putWriteFileNum(long writeNum) {
        this.writeBuffer.putLong(WRITE_NUM_OFFSET, writeNum);
        this.writeFileNum = writeNum;
    }
    
    void putWriteCounter(long writeCounter) {
        this.writeBuffer.putLong(WRITE_CNT_OFFSET, writeCounter);
        this.writeCounter = writeCounter;
    }
    
    void putReadFileNum(long readNum) {
        this.readBuffer.putLong(READ_NUM_OFFSET, readNum);
        this.readFileNum = readNum;
    }
    
    void putReadPosition(int readPosition) {
        this.readBuffer.putInt(READ_POS_OFFSET, readPosition);
        this.readPosition = readPosition;
    }
    
    void putReadCounter(long readCounter) {
        this.readBuffer.putLong(READ_CNT_OFFSET, readCounter);
        this.readCounter = readCounter;
    }
    
    void sync() {
        writeBuffer.force();
    }
    
    void close() {
        sync();
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                Method cleanerMethod = writeBuffer.getClass().getMethod("cleaner");
                cleanerMethod.setAccessible(true);
                Cleaner cleaner = (Cleaner) cleanerMethod.invoke(writeBuffer);
                cleaner.clean();
            } catch (Exception e) {
                LOGGER.error("关闭fqueue索引文件失败", e);
            }
            return null;
        });
        writeBuffer = null;
        readBuffer = null;
    }
    
}
