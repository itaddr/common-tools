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
package com.itaddr.common.tools.fbq.queue;

import com.itaddr.common.tools.fbq.BootFileQueue;
import com.itaddr.common.tools.fbq.QueueMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @Author 马嘉祺
 * @Date 2020/4/19 0019 16 57
 * @Description <p></p>
 */
public class SegmentWriter implements Segment, WritableByteChannel {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentWriter.class);
    
    private static final ByteBuffer EOF_BUFFER;
    
    static {
        EOF_BUFFER = ByteBuffer.allocateDirect(4);
        EOF_BUFFER.putInt(-1).flip();
    }
    
    private boolean open;
    
    private String logFilePath;
    
    private QueueMeta queueMeta;
    
    private RandomAccessFile accessFile;
    
    private FileChannel channel;
    
    
    private MappedByteBuffer metaBuffer;
    
    
    public SegmentWriter(QueueMeta queueMeta, String logFilePath) {
        this.queueMeta = queueMeta;
        this.logFilePath = logFilePath;
        File file = new File(logFilePath);
        try {
            if (file.exists()) {
                this.accessFile = new RandomAccessFile(file, "rw");
                // 校验魔数
                if (BootFileQueue.MAGIC != accessFile.readInt()) {
                    throw new IllegalStateException(String.format("'%s'不是队列log文件", logFilePath));
                }
                // 校验版本
                int version;
                if (BootFileQueue.VERSION != (version = accessFile.readInt())) {
                    throw new IllegalStateException(String.format("log文件版本期望是v%d.%d, 实际版本是v%d.%d", BootFileQueue.VERSION >>> 2, BootFileQueue.VERSION & 0xffff, version >>> 2, version & 0xffff));
                }
                this.channel = accessFile.getChannel();
            } else {
                this.accessFile = new RandomAccessFile(file, "rw");
                // 预分配文件空间
                accessFile.seek(queueMeta.getLogSize() - 1);
                accessFile.writeByte(0);
                accessFile.seek(0);
                this.channel = accessFile.getChannel();
            }
            this.open = true;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    @Override
    public String filePath() {
        return null;
    }
    
    @Override
    public long fileNum() {
        return 0;
    }
    
    @Override
    public FileChannel channel() {
        return null;
    }
    
    @Override
    public void sync() {
    
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
    @Override
    public void close() {
        
        this.open = false;
    }
    
    @Override
    public int write(ByteBuffer src) throws IOException {
        int limit = src.limit();
        byte b = src.get();
        
        channel.write(src);
        
        // 返回写入的字节数，可能为0
        return 0;
    }
    
    
}
