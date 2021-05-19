package com.itaddr.common.tools.fbq.exception;

/**
 * @Author 马嘉祺
 * @Date 2020/12/14 0014 11 26
 * @Description <p></p>
 */
public class RecordReleasedException extends QueueException {
    
    public RecordReleasedException() {
        super();
    }
    
    public RecordReleasedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
