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

import com.itaddr.common.tools.fbq.QueueMeta;
import com.itaddr.common.tools.fbq.QueueOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * @Author 马嘉祺
 * @Date 2020/4/19 0019 16 57
 * @Description <p></p>
 */
public class SegmentReader implements Segment, ReadableByteChannel {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentReader.class);
    
    private boolean open;
    
    private String logFilePath;
    
    private QueueMeta queueMeta;
    
    private QueueOffset queueOffset;
    
    private RandomAccessFile accessFile;
    
    private FileChannel channel;
    
    
    private MappedByteBuffer metaBuffer;
    
    
    public SegmentReader(QueueOffset offset, String logFilePath) {
        
        this.open = true;
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
    public int read(ByteBuffer dst) throws IOException {
    
        dst.limit();
        
        channel.read(dst);
        
        // 返回读取的字节数，可能为0，如果通道已到达文件末尾，则为-1
        return 0;
    }
    
}
