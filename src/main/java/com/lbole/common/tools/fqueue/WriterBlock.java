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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author 马嘉祺
 * @Date 2020/3/22 0022 14 24
 * @Description <p></p>
 */
public class WriterBlock {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WriterBlock.class);
    
    static final int CONTENT_BEGIN_OFFSET = 8;
    private static final String BLOCK_FILE_SUFFIX = ".blk";
    final int EOF = -1;
    
    private static final ByteBuffer EOF_BUFFER;
    
    static {
        EOF_BUFFER = ByteBuffer.allocateDirect(4);
        EOF_BUFFER.putInt(-1).flip();
    }
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 队列偏移量
     */
    private FQueueOffset offset;
    
    private RandomAccessFile accessFile;
    
    private FileChannel channel;
    
    WriterBlock(FQueueOffset offset, String blockFilePath) {
        this.offset = offset;
        this.filePath = blockFilePath;
        File file = new File(blockFilePath);
        try {
            if (file.exists()) {
                this.accessFile = new RandomAccessFile(file, "rw");
                // 校验魔数
                if (FQueue.MAGIC_FLAG != accessFile.readInt()) {
                    throw new IllegalStateException(String.format("'%s'不是队列块量文件", blockFilePath));
                }
                // 校验版本
                int version;
                if (FQueue.VERSION_FLAG != (version = accessFile.readInt())) {
                    throw new IllegalStateException(String.format("块版本期望是v%d.%d, 实际版本是v%d.%d", FQueue.VERSION_FLAG >>> 2, FQueue.VERSION_FLAG & 0xffff, version >>> 2, version & 0xffff));
                }
                this.channel = accessFile.getChannel();
            } else {
                this.accessFile = new RandomAccessFile(file, "rw");
                // 预分配文件空间
                accessFile.seek(offset.getBlockSize() - 1);
                accessFile.writeByte(0);
                accessFile.seek(0);
                this.channel = accessFile.getChannel();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    String getFilePath() {
        return filePath;
    }
    
    static String formatFilePath(String queueName, long fileNum, String rootPath) {
        return String.format("%s%s%s%s%020d%s", rootPath, File.separator, queueName, File.separator, fileNum, BLOCK_FILE_SUFFIX);
    }
    
    public ReaderBlock readBlock() {
        return null;
    }
    
    void putEOF() {
        try {
            EOF_BUFFER.rewind();
            channel.write(EOF_BUFFER);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    boolean isSpaceAvailable(int length) {
        // 保证最后有4字节的空间可以写入EOF
        return offset.getBlockSize() >= offset.getWritePosition() + 28 + length + 4;
    }
    
    void write(FQueueRecord record) {
        int writePosition = offset.getWritePosition();
        
        try {
            channel.position(writePosition);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        ByteBuffer buffer = ByteBuffer.allocate(28 + record.getLength());
        buffer.putInt(FQueue.MAGIC_FLAG).putInt(FQueue.VERSION_FLAG).putLong(record.getId()).putLong(record.getTimeStamp()).putInt(record.getLength());
        buffer.put((byte) record.headerSize());
        for (FQueueHeader header : record) {
            buffer.put((byte) header.getKey());
            byte[] value = header.getValue();
            buffer.putShort((short) value.length);
            buffer.put(value);
        }
        byte[] recordKey = record.getRecordKey();
        if (null == recordKey || 0 == recordKey.length) {
            buffer.putShort((short) 0);
        } else {
            buffer.putShort((short) recordKey.length);
            buffer.put(recordKey);
        }
        buffer.put(record.getRecordValue());
        try {
            channel.write(buffer, 8);
            channel.write(buffer);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // 更新偏移量
        offset.putWritePosition(writePosition + 28 + record.getLength());
        offset.putWriteCounter(offset.getWriteCounter() + 1);
    }
    
    void sync() {
        try {
            channel.force(false);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    void close() {
        try {
            channel.force(false);
            accessFile.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        this.channel = null;
        this.accessFile = null;
    }
    
}
