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

/**
 * AES算法加解密模式和填充模式
 * 加密算法/加密模式/填充类型
 *
 * @Author 马嘉祺
 * @Date 2019/5/16 0016 09 35
 * @Description
 */
public enum AESPadEnum {
    
    /**
     * 字节加密后数据长度: 16; 不满16字节加密后长度: 不支持
     */
    CBC_NO_PADDING("AES/CBC/NoPadding"),
    
    /**
     * 字节加密后数据长度: 32; 不满16字节加密后长度: 16
     */
    CBC_PKCS5_PADDING("AES/CBC/PKCS5Padding"),
    
    /**
     * 字节加密后数据长度: 32; 不满16字节加密后长度: 16
     */
    CBC_ISO10126_PADDING("AES/CBC/ISO10126Padding"),
    
    /**
     * 字节加密后数据长度: 16; 不满16字节加密后长度: 原始数据长度
     */
    CFB_NO_PADDING("AES/CFB/NoPadding"),
    
    /**
     * 字节加密后数据长度: 32; 不满16字节加密后长度: 16
     */
    CFB_ISO10126_PADDING("AES/CFB/ISO10126Padding"),
    
    /**
     * 不带模式和填充来获取AES算法的时候，其默认使用AES/ECB/PKCS5Padding（输入可以不是16字节，也不需要填充向量）
     * 字节加密后数据长度: 32; 不满16字节加密后长度: 16
     */
    ECB_PKCS5_PADDING("AES/ECB/PKCS5Padding"),
    
    /**
     * 字节加密后数据长度: 32; 不满16字节加密后长度: 16
     */
    ECB_ISO10126_PADDING("AES/ECB/ISO10126Padding"),
    
    /**
     * 字节加密后数据长度: 16; 不满16字节加密后长度: 原始数据长度
     */
    OFB_NO_PADDING("AES/OFB/NoPadding"),
    
    /**
     * 字节加密后数据长度: 32; 不满16字节加密后长度: 16
     */
    OFB_PKCS5_PADDING("AES/OFB/PKCS5Padding"),
    
    /**
     * 字节加密后数据长度: 32; 不满16字节加密后长度: 16
     */
    OFB_ISO10126_PADDING("AES/OFB/ISO10126Padding"),
    
    /**
     * 字节加密后数据长度: 16; 不满16字节加密后长度: 不支持
     */
    PCBC_NO_PADDING("AES/PCBC/NoPadding"),
    
    /**
     * 字节加密后数据长度: 32; 不满16字节加密后长度: 16
     */
    PCBC_PKCS5_PADDING("AES/PCBC/PKCS5Padding"),
    
    /**
     * 字节加密后数据长度: 32; 不满16字节加密后长度: 16
     */
    PCBC_ISO10126_PADDING("AES/PCBC/ISO10126Padding");
    
    private final String name;
    
    AESPadEnum(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public static AESPadEnum parse(final String name) {
        for (AESPadEnum aes : AESPadEnum.values()) {
            if (aes.getName().equals(name)) {
                return aes;
            }
        }
        return null;
    }
    
}
