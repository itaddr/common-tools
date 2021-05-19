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

import com.itaddr.common.tools.utils.ByteUtil;

import java.nio.charset.StandardCharsets;

/**
 * @Author 马嘉祺
 * @Date 2020/2/19 0019 14 59
 * @Description <p></p>
 */
public class FQueueHeader {
    
    private static final int MIN_KEY_VALUE = 0, MAX_KEY_VALUE = 255;
    private static final int MAX_VALUE_LENGTH = 65535;
    
    private int key;
    
    private byte[] value;
    
    public FQueueHeader(int key, byte... value) {
        if (key < MIN_KEY_VALUE || key > MAX_KEY_VALUE) {
            throw new IllegalArgumentException(String.format("key: %d 为错误的值，正确为 key >= %d && key <= %d", key, MIN_KEY_VALUE, MAX_KEY_VALUE));
        }
        if (null == value) {
            throw new NullPointerException("value不能为空");
        }
        if (value.length > MAX_VALUE_LENGTH) {
            throw new IllegalArgumentException(String.format("value.length: %d 为错误的长度，正确为 value.length <= %d", value.length, MAX_VALUE_LENGTH));
        }
        this.key = key;
        this.value = value;
    }
    
    public int getKey() {
        return key;
    }
    
    public byte[] getValue() {
        return value;
    }
    
    public int intValue() {
        return ByteUtil.readInt(value, value.length - 1);
    }
    
    public long longValue() {
        return ByteUtil.readLong(value, value.length - 1);
    }
    
    public String asciiValue() {
        return new String(value, StandardCharsets.US_ASCII);
    }
    
    public String utf8Value() {
        return new String(value, StandardCharsets.UTF_8);
    }
    
}
