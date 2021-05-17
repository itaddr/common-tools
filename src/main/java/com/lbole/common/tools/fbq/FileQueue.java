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

import com.lbole.common.tools.beans.TimeStamp;

import java.util.concurrent.TimeUnit;

/**
 * @Author 马嘉祺
 * @Date 2020/3/21 0021 17 29
 * @Description <p></p>
 */
public interface FileQueue {
    
    /**
     * 队列名称
     *
     * @return
     */
    String queueName();
    
    /**
     * 队列长度
     *
     * @return
     */
    long size();
    
    /**
     * 向队列中写入消息
     *
     * @param message
     * @return
     */
    boolean offer(QueueRecord message);
    
    /**
     * 队列之间消息传输
     *
     * @param src
     * @param size
     */
    void transferFrom(FileQueue src, int size);
    
    /**
     * 查看最新的消息
     *
     * @return
     */
    QueueRecord peek();
    
    /**
     * 查看最新的时间戳
     *
     * @return
     */
    TimeStamp timestamp();
    
    /**
     * 消费队列头消息
     *
     * @return
     */
    QueueRecord take();
    
    /**
     * 消费队列头消息
     *
     * @return
     * @throws InterruptedException
     */
    QueueRecord poll() throws InterruptedException;
    
    /**
     * 消费队列头消息
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    QueueRecord poll(long timeout, TimeUnit unit) throws InterruptedException;
    
    /**
     * 队列见消息传输
     *
     * @param target
     * @param size
     */
    void transferTo(FileQueue target, int size);
    
}
