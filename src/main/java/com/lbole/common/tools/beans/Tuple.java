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
 * @Date 2019/9/2 0002 14 45
 * @Description
 */
public class Tuple<L, C, R> {
    
    private L left;
    
    private C center;
    
    private R right;
    
    public Tuple() {
    }
    
    private Tuple(L left, C center, R right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }
    
    public static <L, C, R> Tuple<L, C, R> of(L left) {
        return new Tuple<>(left, null, null);
    }
    
    public static <L, C, R> Tuple<L, C, R> of(L left, C center) {
        return new Tuple<>(left, center, null);
    }
    
    public static <L, C, R> Tuple<L, C, R> of(L left, C center, R right) {
        return new Tuple<>(left, center, right);
    }
    
    public L getLeft() {
        return left;
    }
    
    public void setLeft(L left) {
        this.left = left;
    }
    
    public C getCenter() {
        return center;
    }
    
    public void setCenter(C center) {
        this.center = center;
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
        Tuple<?, ?, ?> tuple = (Tuple<?, ?, ?>) o;
        return Objects.equals(left, tuple.left) &&
                Objects.equals(center, tuple.center) &&
                Objects.equals(right, tuple.right);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(left, center, right);
    }
    
    @Override
    public String toString() {
        return "Tuple{" +
                "left=" + left +
                ", center=" + center +
                ", right=" + right +
                '}';
    }
}
