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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Cleaner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * 队列数据块
 *
 * @Author 马嘉祺
 * @Date 2018/10/22 0022 14 30
 * @Description
 */
public class FQueueBlock {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FQueueBlock.class);
    
    static final int CONTENT_BEGIN_OFFSET = 8;
    private static final String BLOCK_FILE_SUFFIX = ".blk";
    final int EOF = -1;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 队列偏移量
     */
    private FQueueOffset offset;
    
    private MappedByteBuffer metaBuffer;
    
    private ByteBuffer byteBuffer;
    
    FQueueBlock(FQueueOffset offset, String blockFilePath) {
        this.offset = offset;
        this.filePath = blockFilePath;
        File file = new File(blockFilePath);
        RandomAccessFile accessFile = null;
        try {
            if (file.exists()) {
                accessFile = new RandomAccessFile(file, "rw");
                // 校验魔数
                if (FQueue.MAGIC_FLAG != accessFile.readInt()) {
                    throw new IllegalStateException(String.format("'%s'不是队列块量文件", blockFilePath));
                }
                // 校验版本
                int version;
                if (FQueue.VERSION_FLAG != (version = accessFile.readInt())) {
                    throw new IllegalStateException(String.format("块版本期望是v%d.%d, 实际版本是v%d.%d", FQueue.VERSION_FLAG >>> 2, FQueue.VERSION_FLAG & 0xffff, version >>> 2, version & 0xffff));
                }
                this.metaBuffer = accessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, offset.getBlockSize());
                this.byteBuffer = metaBuffer.load();
            } else {
                accessFile = new RandomAccessFile(file, "rw");
                this.metaBuffer = accessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, offset.getBlockSize());
                this.metaBuffer.putInt(0, FQueue.MAGIC_FLAG).putInt(4, FQueue.VERSION_FLAG);
                this.byteBuffer = metaBuffer.load();
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
    
    private FQueueBlock(String blockFilePath, FQueueOffset offset, MappedByteBuffer metaBuffer, ByteBuffer byteBuffer) {
        this.filePath = blockFilePath;
        this.offset = offset;
        this.metaBuffer = metaBuffer;
        this.byteBuffer = byteBuffer;
    }
    
    /**
     * 复制块文件
     *
     * @return
     */
    FQueueBlock duplicate() {
        return new FQueueBlock(filePath, offset, metaBuffer, byteBuffer.duplicate());
    }
    
    /**
     * 根据队列名称、快文件号、队列文件根目录生成块文件路径
     *
     * @param queueName
     * @param fileNum
     * @param rootPath
     * @return
     */
    static String formatFilePath(String queueName, long fileNum, String rootPath) {
        return String.format("%s%s%s%s%020d%s", rootPath, File.separator, queueName, File.separator, fileNum, BLOCK_FILE_SUFFIX);
    }
    
    String getFilePath() {
        return filePath;
    }
    
    /**
     * 写入结尾符号
     */
    void putEOF() {
        this.byteBuffer.position(offset.getWritePosition());
        this.byteBuffer.putInt(EOF);
    }
    
    /**
     * 判断块文件是否能写入指定长度数据
     *
     * @param length
     * @return
     */
    boolean isSpaceAvailable(int length) {
        // 保证最后有4字节的空间可以写入EOF
        return offset.getBlockSize() >= offset.getWritePosition() + 28 + length + 4;
    }
    
    /**
     * 判断读索引是否已经到末尾
     *
     * @return
     */
    boolean eof() {
        final int readPosition = offset.getReadPosition();
        return readPosition > CONTENT_BEGIN_OFFSET && byteBuffer.getInt(readPosition) == EOF;
    }
    
    /**
     * 写入数据
     *
     * @param record
     * @return
     */
    void write(FQueueRecord record) {
        int writePosition = offset.getWritePosition();
        
        // 写入数据
        byteBuffer.position(writePosition);
        // 写入magicFlag
        byteBuffer.putInt(FQueue.MAGIC_FLAG);
        byteBuffer.putInt(FQueue.VERSION_FLAG);
        byteBuffer.putLong(record.getId());
        byteBuffer.putLong(record.getTimeStamp());
        byteBuffer.putInt(record.getLength());
        byteBuffer.put((byte) record.headerSize());
        for (FQueueHeader header : record) {
            byteBuffer.put((byte) header.getKey());
            byte[] value = header.getValue();
            byteBuffer.putShort((short) value.length);
            byteBuffer.put(value);
        }
        byte[] recordKey = record.getRecordKey();
        if (null == recordKey || 0 == recordKey.length) {
            byteBuffer.putShort((short) 0);
        } else {
            byteBuffer.putShort((short) recordKey.length);
            byteBuffer.put(recordKey);
        }
        byteBuffer.put(record.getRecordValue());
        
        // 更新偏移量
        offset.putWritePosition(writePosition + 28 + record.getLength());
        offset.putWriteCounter(offset.getWriteCounter() + 1);
    }
    
    /**
     * 仅读数据不更新索引
     *
     * @return
     */
    FQueueRecord peak() {
        // 获取相关长度和索引
        long readNum = offset.getReadFileNum();
        int readPosition = offset.getReadPosition();
        long writeNum = offset.getWriteFileNum();
        int writePosition = offset.getWritePosition();
        
        // 如果读写块文件号相等，并且读写索引值相等，则返回空正在写的数据不能立即返回
        if (readNum == writeNum && readPosition >= writePosition) {
            return null;
        }
        
        // 读取数据
        byteBuffer.position(readPosition);
        int magicFlag = byteBuffer.getInt();
        if (EOF == magicFlag) {
            return null;
        }
        if (FQueue.MAGIC_FLAG != magicFlag) {
            throw new IllegalStateException("消费到未知的异常数据: magicFlag=" + Integer.toHexString(magicFlag));
        }
        
        byteBuffer.position(readPosition + 8);
        return new FQueueRecord(byteBuffer);
    }
    
    long peakTimeStamp() {
        long readNum = offset.getReadFileNum();
        int readPos = offset.getReadPosition();
        long writeNum = offset.getWriteFileNum();
        int writePos = offset.getWritePosition();
        
        // 如果读写块文件号相等，并且读写索引值相等，则返回空正在写的数据不能立即返回
        if (readNum == writeNum && readPos >= writePos) {
            return -1;
        }
        
        // 读取数据
        byteBuffer.position(readPos);
        int magicFlag = byteBuffer.getInt();
        if (EOF == magicFlag) {
            return -1;
        }
        if (FQueue.MAGIC_FLAG != magicFlag) {
            throw new IllegalStateException("消费到未知的异常数据: magicFlag=" + Integer.toHexString(magicFlag));
        }
        
        return byteBuffer.getLong(readPos + 16);
    }
    
    /**
     * 读数据并更新索引
     *
     * @return
     */
    FQueueRecord read() {
        // 获取相关长度和索引
        long readNum = offset.getReadFileNum();
        int readPosition = offset.getReadPosition();
        long writeNum = offset.getWriteFileNum();
        int writePosition = offset.getWritePosition();
        
        // 如果读写块文件号相等，并且读写索引值相等，则返回空正在写的数据不能立即返回
        if (readNum == writeNum && readPosition >= writePosition) {
            return null;
        }
        
        // 读取数据
        byteBuffer.position(readPosition);
        int magicFlag = byteBuffer.getInt();
        if (EOF == magicFlag) {
            return null;
        }
        if (FQueue.MAGIC_FLAG != magicFlag) {
            throw new IllegalStateException("消费到未知的异常数据: magicFlag=" + Integer.toHexString(magicFlag));
        }
        
        byteBuffer.position(readPosition + 8);
        FQueueRecord record = new FQueueRecord(byteBuffer);
        
        // 更新索引数据
        offset.putReadPosition(readPosition + 28 + record.getLength());
        offset.putReadCounter(offset.getReadCounter() + 1);
        
        return record;
    }
    
    /**
     * 刷盘
     */
    void sync() {
        metaBuffer.force();
    }
    
    /**
     * 释放块文件资源
     */
    void close() {
        sync();
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                Method cleanerMethod = metaBuffer.getClass().getMethod("cleaner");
                cleanerMethod.setAccessible(true);
                Cleaner cleaner = (Cleaner) cleanerMethod.invoke(metaBuffer);
                cleaner.clean();
            } catch (Exception e) {
                LOGGER.error("关闭fqueue块文件失败", e);
            }
            return null;
        });
        metaBuffer = null;
        byteBuffer = null;
    }
    
}
