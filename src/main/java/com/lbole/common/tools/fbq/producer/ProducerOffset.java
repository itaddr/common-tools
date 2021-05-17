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
package com.lbole.common.tools.fbq.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author 马嘉祺
 * @Date 2020/3/23 0023 09 19
 * @Description <p></p>
 */
public class ProducerOffset {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerOffset.class);
    
    private static final String OFFSET_FILE_NAME = "__queue_offset_checkpoint";
    
    private static final int OFFSET_FILE_SIZE = 52;
    
    private static final int BLOCK_SIZE_OFFSET = 8,
            READ_NUM_OFFSET = 12, READ_POS_OFFSET = 20, READ_CNT_OFFSET = 24,
            WRITE_NUM_OFFSET = 32, WRITE_POS_OFFSET = 40, WRITE_CNT_OFFSET = 44;
    
}
