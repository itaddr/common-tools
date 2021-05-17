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
package com.lbole.common.tools.beans;

import java.util.Objects;

/**
 * @Author 马嘉祺
 * @Date 2020/12/17 0017 13 52
 * @Description <p></p>
 */
public class IntLPair {
    
    private int left;
    
    private long right;
    
    public IntLPair() {
    }
    
    private IntLPair(int left, long right) {
        this.left = left;
        this.right = right;
    }
    
    public static IntLPair of(int left, long right) {
        return new IntLPair(left, right);
    }
    
    public int getInt() {
        return left;
    }
    
    public void setInt(int left) {
        this.left = left;
    }
    
    public long getLong() {
        return right;
    }
    
    public void setLong(long right) {
        this.right = right;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntLPair intLPair = (IntLPair) o;
        return left == intLPair.left &&
                right == intLPair.right;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
    
    @Override
    public String toString() {
        return "IntLPair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
    
}
