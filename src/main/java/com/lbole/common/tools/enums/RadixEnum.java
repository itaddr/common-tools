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
 * @Date 2019/5/16 0016 12 26
 * @Description
 */
public enum RadixEnum {
    
    /**
     * 16进制
     */
    HEX(16),
    
    /**
     * 10进制
     */
    DEC(10),
    
    /**
     * 8进制
     */
    OCT(8),
    
    /**
     * 2进制
     */
    BIN(2);
    
    private int radixValue;
    
    RadixEnum(final int radixValue) {
        this.radixValue = radixValue;
    }
    
    public int getRadixValue() {
        return radixValue;
    }
    
    public static RadixEnum parse(final int radixValue) {
        for (RadixEnum radix : RadixEnum.values()) {
            if (radix.getRadixValue() == radixValue) {
                return radix;
            }
        }
        return null;
    }
    
}
