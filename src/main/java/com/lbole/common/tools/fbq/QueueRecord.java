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
package com.lbole.common.tools.fbq;

import com.lbole.common.tools.beans.Pair;

import java.util.Iterator;

/**
 * @Author 马嘉祺
 * @Date 2020/6/8 0008 17 20
 * @Description <p></p>
 */
public interface QueueRecord extends Iterable<Pair<Integer, byte[]>> {
    
    /**
     * 消息ID
     *
     * @return
     */
    long id();
    
    /**
     * 消息时间戳
     *
     * @return
     */
    long timestamp();
    
    /**
     * 消息长度
     *
     * @return
     */
    int length();
    
    /**
     * header数量
     *
     * @return
     */
    int headerSize();
    
    /**
     * 根据headerKey获取headerValue
     *
     * @param key
     * @return
     */
    byte[] getHeader(int key);
    
    /**
     * 遍历header
     *
     * @return
     */
    @Override
    Iterator<Pair<Integer, byte[]>> iterator();
    
    /**
     * 获取recordKey
     *
     * @return
     */
    byte[] getRecordKey();
    
    /**
     * 获取recordValue
     *
     * @return
     */
    byte[] getRecordValue();
    
    /**
     * 释放一个块文件的持有句柄
     *
     * @return
     */
    int release();
    
}
