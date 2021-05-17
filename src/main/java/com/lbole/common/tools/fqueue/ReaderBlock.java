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
 * @Author 马嘉祺
 * @Date 2020/3/22 0022 15 43
 * @Description <p></p>
 */
public class ReaderBlock {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderBlock.class);
    
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
    
    ReaderBlock(FQueueOffset offset, String blockFilePath) {
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
    
    String getFilePath() {
        return filePath;
    }
    
    static String formatFilePath(String queueName, long fileNum, String rootPath) {
        return String.format("%s%s%s%s%020d%s", rootPath, File.separator, queueName, File.separator, fileNum, BLOCK_FILE_SUFFIX);
    }
    
    boolean eof() {
        int readPosition = offset.getReadPosition();
        return readPosition > CONTENT_BEGIN_OFFSET && byteBuffer.getInt(readPosition) == EOF;
    }
    
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
    
    void sync() {
        metaBuffer.force();
    }
    
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
        this.metaBuffer = null;
        this.byteBuffer = null;
    }
    
}
