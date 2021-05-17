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
package com.lbole.common.tools.enums;

import com.lbole.common.tools.utils.ByteUtil;

import java.util.function.Function;

/**
 * @Author 马嘉祺
 * @Date 2019/9/1 0001 09 44
 * @Description
 */
public enum BinaryEnum {
    
    /**
     * 而进制相关
     */
    BINARY(ByteUtil::toBinaryString, ByteUtil::parseBinaryBytes),
    
    /**
     * 16进制小写字母相关
     */
    HEX_LOWER(ByteUtil::toLowerHexString, ByteUtil::parseHexBytes),
    
    /**
     * 16进制大写字母相关
     */
    HEX_UPPER(ByteUtil::toUpperHexString, ByteUtil::parseHexBytes),
    
    /**
     * BASE64相关
     */
    BASE64(ByteUtil::toBase64String, ByteUtil::parseBase64Bytes);
    
    private final Function<byte[], String> format;
    
    private final Function<String, byte[]> parse;
    
    BinaryEnum(Function<byte[], String> format, Function<String, byte[]> parse) {
        this.format = format;
        this.parse = parse;
    }
    
    public String format(byte[] bytes) {
        if (null == bytes || 0 == bytes.length) {
            return null;
        }
        return format.apply(bytes);
    }
    
    public byte[] parse(String format) {
        if (null == format || 0 == format.length()) {
            return new byte[0];
        }
        return parse.apply(format);
    }
    
}
