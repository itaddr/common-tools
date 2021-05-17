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
package com.lbole.common.tools.fbq.queue;

/**
 * @Author 马嘉祺
 * @Date 2020/3/21 0021 21 42
 * @Description <p></p>
 */
public interface QueuePromise {
    
    /**
     * 路由到下一个写Segment
     *
     * @param fileNum
     */
    void rotateNextWriteSegment(long fileNum);
    
    /**
     * 路由到下一个读Segment
     */
    void rotateNextReadSegment();
    
    /**
     * 数据刷盘
     */
    void sync();
    
    /**
     * 关闭并释放队列数据
     */
    void close();
    
}
