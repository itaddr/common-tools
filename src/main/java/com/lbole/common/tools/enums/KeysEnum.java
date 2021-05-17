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

/**
 * @Author 马嘉祺
 * @Date 2019/5/16 0016 09 38
 * @Description
 */
public enum KeysEnum {
    
    /**
     * MD5
     */
    MD5("MD5", false, false, 0),
    
    /**
     * SHA
     */
    SHA1("SHA-1", false, false, 0),
    SHA256("SHA-256", false, false, 0),
    SHA384("SHA-384", false, false, 0),
    SHA512("SHA-512", false, false, 0),
    
    /**
     * Hmac
     */
    HmacMD5("HmacMD5", true, true, 128),
    HmacSHA1("HmacSHA1", true, true, 128),
    HmacSHA256("HmacSHA256", true, true, 128),
    HmacSHA384("HmacSHA384", true, true, 128),
    HmacSHA512("HmacSHA512", true, true, 128),
    
    /**
     * CRC32
     */
    CRC32("CRC32", false, false, 0),
    
    /**
     * AES128
     */
    AES128("AES", true, true, 128),
    
    /**
     * RSA
     */
    RSA2048("RSA", true, false, 2048);
    
    private String name;
    
    private int keyBits;
    
    private boolean haveKey;
    
    private boolean symmetric;
    
    KeysEnum(String name, boolean haveKey, boolean symmetric, int keyBits) {
        this.name = name;
        this.haveKey = haveKey;
        this.symmetric = symmetric;
        this.keyBits = keyBits;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isHaveKey() {
        return haveKey;
    }
    
    public boolean isSymmetric() {
        return symmetric;
    }
    
    public int getKeyBits() {
        return keyBits;
    }
    
    public static KeysEnum parse(final String name) {
        for (KeysEnum keyMode : KeysEnum.values()) {
            if (keyMode.getName().equals(name)) {
                return keyMode;
            }
        }
        return null;
    }
    
}
