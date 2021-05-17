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
 * @Date 2019/9/2 0002 14 41
 * @Description
 */
public class Pair<L, R> {
    
    private L left;
    
    private R right;
    
    public Pair() {
    }
    
    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
    
    public static <L, R> Pair<L, R> of(L left) {
        return new Pair<>(left, null);
    }
    
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }
    
    public L getLeft() {
        return left;
    }
    
    public void setLeft(L left) {
        this.left = left;
    }
    
    public R getRight() {
        return right;
    }
    
    public void setRight(R right) {
        this.right = right;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
    
    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
