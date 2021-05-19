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
package com.itaddr.common.tools.exception;

/**
 * 主要用来替换必须要捕捉的Exception，让开发人员自己选择是否需要捕捉异常
 *
 * @Author 马嘉祺
 * @Date 2020/4/6 0006 10 53
 * @Description <p></p>
 */
public class NotCaptureException extends RuntimeException {
    
    public NotCaptureException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NotCaptureException(Throwable cause) {
        super(cause);
    }
    
}
