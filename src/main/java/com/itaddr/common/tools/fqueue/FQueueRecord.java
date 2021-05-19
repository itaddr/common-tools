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

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 数据结构:
 * <p>
 * ------------------------------------------------------------------
 * |         | 4B {@link FQueue#MAGIC_FLAG} | {@link FQueueBlock#EOF}
 * |         | 4B {@link FQueue#VERSION_FLAG}
 * |         | 8B recordId
 * |         | 8B timestamp
 * |         | 4B recordLen
 * ------------------------------------------------------------------
 * |         | 1B count
 * |         | 1B key(1)
 * |         | 2B valueLen(1)
 * |         | xx value(1)
 * | headers |    ...
 * |         | 1B key(n)
 * |         | 2B valueLen(n)
 * |         | xx value(n)
 * |         |    ...
 * ------------------------------------------------------------------
 * |         | 2B recordKeyLen
 * |         | xx recordKey
 * ------------------------------------------------------------------
 * |         | xx recordValue
 * ------------------------------------------------------------------
 *
 * @Author 马嘉祺
 * @Date 2020/2/15 0015 20 57
 * @Description <p></p>
 */
public class FQueueRecord implements Iterable<FQueueHeader> {
    
    private static final int MAX_KEY_LENGTH = 65535;
    
    private long id;
    
    private long timestamp;
    
    private int length;
    
    private FQueueHeader[] headers1, headers2;
    
    private byte[] recordKey;
    
    private byte[] recordValue;
    
    public FQueueRecord(long timestamp, byte[] recordKey, byte[] recordValue, FQueueHeader... headers) {
        if (timestamp < 0) {
            throw new IllegalArgumentException(String.format("不合法的时间戳：%d", timestamp));
        }
        int recordKeyLen = null == recordKey ? 0 : recordKey.length;
        if (recordKeyLen > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException(String.format("不合法的recordKey：recordKeyLen=%d，必须 recordKeyLen <= %d", recordKeyLen, MAX_KEY_LENGTH));
        }
        if (null == recordValue) {
            throw new NullPointerException("recordValue不能为空");
        }
        // timestamp
        this.timestamp = timestamp;
        // length
        this.length = 1;
        // headers
        if (null != headers && headers.length > 0) {
            int maxIdx = 0;
            for (int i = 0; i < headers.length; ++i) {
                FQueueHeader header = Objects.requireNonNull(headers[i], String.format("第%d个元素", i));
                length += 3 + header.getValue().length;
                if (maxIdx < header.getKey()) {
                    maxIdx = header.getKey();
                }
            }
            this.headers1 = headers;
            this.headers2 = new FQueueHeader[maxIdx + 1];
            for (FQueueHeader header : headers) {
                headers2[header.getKey()] = header;
            }
        } else {
            this.headers1 = new FQueueHeader[0];
            this.headers2 = new FQueueHeader[0];
        }
        // recordKey
        length += 2 + recordKeyLen;
        this.recordKey = recordKey;
        // recordValue
        length += recordValue.length;
        this.recordValue = recordValue;
    }
    
    FQueueRecord(ByteBuffer byteBuffer) {
        // recordId
        this.id = byteBuffer.getLong();
        // timestamp
        this.timestamp = byteBuffer.getLong();
        // recordLen
        this.length = byteBuffer.getInt();
        int recordValueOffset = 1;
        // headers
        int headerSize = byteBuffer.get() & 0xff;
        this.headers1 = new FQueueHeader[headerSize];
        int maxIdx = 0;
        for (int i = 0; i < headerSize; ++i) {
            int key = byteBuffer.get() & 0xff;
            if (maxIdx < key) {
                maxIdx = key;
            }
            byte[] value = new byte[byteBuffer.getShort() & 0xffff];
            byteBuffer.get(value);
            headers1[i] = new FQueueHeader(key, value);
            recordValueOffset += 3 + value.length;
        }
        this.headers2 = new FQueueHeader[maxIdx + 1];
        for (FQueueHeader header : headers1) {
            headers2[header.getKey()] = header;
        }
        // recordKey
        int recordKeyLen = byteBuffer.getShort() & 0xffff;
        recordValueOffset += 2 + recordKeyLen;
        if (recordKeyLen > 0) {
            this.recordKey = new byte[recordKeyLen];
            byteBuffer.get(recordKey);
        }
        // recordValue
        int recordValueLen = length - recordValueOffset;
        if (recordValueLen <= 0) {
            throw new NullPointerException("recordValue不能为空");
        }
        this.recordValue = new byte[recordValueLen];
        byteBuffer.get(recordValue);
    }
    
    void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public long getTimeStamp() {
        return timestamp;
    }
    
    public int getLength() {
        return length;
    }
    
    public int headerSize() {
        return headers1.length;
    }
    
    public FQueueHeader getHeader(int key) {
        return key >= headers2.length ? null : headers2[key];
    }
    
    @Override
    public Iterator<FQueueHeader> iterator() {
        return new Iterator<FQueueHeader>() {
            int cursor = 0;
            
            @Override
            public boolean hasNext() {
                return cursor < headerSize();
            }
            
            @Override
            public FQueueHeader next() {
                if (cursor >= headerSize()) {
                    throw new NoSuchElementException();
                }
                return headers1[cursor++];
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void forEachRemaining(Consumer<? super FQueueHeader> consumer) {
                Objects.requireNonNull(consumer);
                int headerLength = headerSize();
                if (cursor >= headerLength) {
                    return;
                }
                while (cursor < headerLength) {
                    consumer.accept(headers1[cursor++]);
                }
            }
        };
    }
    
    public byte[] getRecordKey() {
        return recordKey;
    }
    
    public byte[] getRecordValue() {
        return recordValue;
    }
    
}
