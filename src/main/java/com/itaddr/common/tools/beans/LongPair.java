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
package com.itaddr.common.tools.beans;

import java.util.Objects;

/**
 * @Author 马嘉祺
 * @Date 2020/12/17 0017 13 50
 * @Description <p></p>
 */
public class LongPair {
    
    private int left;
    
    private int right;
    
    public LongPair() {
    }
    
    private LongPair(int left, int right) {
        this.left = left;
        this.right = right;
    }
    
    public static LongPair of(int left, int right) {
        return new LongPair(left, right);
    }
    
    public int getLeft() {
        return left;
    }
    
    public void setLeft(int left) {
        this.left = left;
    }
    
    public int getRight() {
        return right;
    }
    
    public void setRight(int right) {
        this.right = right;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongPair longPair = (LongPair) o;
        return left == longPair.left &&
                right == longPair.right;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
    
    @Override
    public String toString() {
        return "LongPair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
    
}
