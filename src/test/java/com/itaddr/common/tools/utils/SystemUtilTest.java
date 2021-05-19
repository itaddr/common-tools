package com.itaddr.common.tools.utils;

import org.junit.Test;

import java.io.File;

/**
 * @Author 马嘉祺
 * @Date 2020/5/28 0028 17 25
 * @Description <p></p>
 */
public class SystemUtilTest {
    
    @Test
    public void loadLogbackConfig() {
        String logbackConfigPath = SystemUtil.BASE_DIR + File.separator + "test-classes" + File.separator + "logback.xml";
        System.out.println(logbackConfigPath);
        SystemUtil.loadLogbackConfig(logbackConfigPath);
    }
    
}