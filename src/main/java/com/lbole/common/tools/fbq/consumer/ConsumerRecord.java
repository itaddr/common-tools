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
package com.lbole.common.tools.fbq.consumer;

import com.lbole.common.tools.beans.TimeStamp;

/**
 * @Author 马嘉祺
 * @Date 2020/3/21 0021 17 34
 * @Description <p></p>
 */
public interface ConsumerRecord {
    
    long id();
    
    TimeStamp timestamp();
    
    Headers headers();
    
    byte[] bytesKey();
    
    String asciiKey();
    
    String utf8Key();
    
    byte[] value();
    
    void release();
    
    boolean isReleased();
    
    interface Headers extends Iterable<Header> {
        
        int size();
        
        Header getHeader(int key);
        
    }
    
    interface Header {
        
        int getKey();
        
        byte[] getValue();
        
        int intValue();
        
        long longValue();
        
        String asciiValue();
        
        String utf8Value();
        
    }
    
}
