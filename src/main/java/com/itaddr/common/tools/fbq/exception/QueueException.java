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
package com.itaddr.common.tools.fbq.exception;

/**
 * @Author 马嘉祺
 * @Date 2020/6/8 0008 16 12
 * @Description <p></p>
 */
public class QueueException extends RuntimeException {
    
    public QueueException() {
    }
    
    public QueueException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
