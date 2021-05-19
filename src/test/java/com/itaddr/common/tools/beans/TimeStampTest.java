package com.itaddr.common.tools.beans;

import org.junit.Test;

/**
 * @Author 马嘉祺
 * @Date 2020/3/6 0006 11 34
 * @Description <p></p>
 */
public class TimeStampTest {
    
    @Test
    public void fmtTimeStr() {
        System.out.println(new TimeStamp("2020-03-06 11:35:33.453").timestamp());
        System.out.println(new TimeStamp("2020-03-06T11:35:33.453").timestamp());
    }
    
}
