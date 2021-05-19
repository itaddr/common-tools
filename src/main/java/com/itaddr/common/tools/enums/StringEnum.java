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
package com.itaddr.common.tools.enums;

import com.itaddr.common.tools.utils.ByteUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * @Author 马嘉祺
 * @Date 2019/8/30 0030 17 15
 * @Description
 */
public enum StringEnum {
    
    /**
     * ASCII编解码
     */
    ASCII(StandardCharsets.US_ASCII, ByteUtil::toAsciiBytes, ByteUtil::parseAsciiString),
    
    /**
     * UTF8编解码
     */
    UTF8(StandardCharsets.UTF_8, v -> v.getBytes(StandardCharsets.UTF_8), v -> new String(v, StandardCharsets.UTF_8)),
    
    /**
     * GBK编解码
     */
    GBK(Charset.forName("GBK"), v -> v.getBytes(Charset.forName("GBK")), v -> new String(v, Charset.forName("GBK")));
    
    private final Charset charset;
    
    private final Function<String, byte[]> enfunc;
    
    private final Function<byte[], String> defunc;
    
    StringEnum(final Charset charset, Function<String, byte[]> enfunc, Function<byte[], String> defunc) {
        this.charset = charset;
        this.enfunc = enfunc;
        this.defunc = defunc;
    }
    
    public Charset charset() {
        return charset;
    }
    
    public byte[] encode(final String string) {
        return enfunc.apply(string);
    }
    
    public String decode(final byte[] bytes) {
        return defunc.apply(bytes);
    }
    
}
