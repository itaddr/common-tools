package com.itaddr.common.tools.beans;

import com.itaddr.common.tools.utils.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author 马嘉祺
 * @Date 2020/3/16 0016 22 06
 * @Description <p></p>
 */
public class DaemonServiceTest {
    
    @Test
    public void test01() {
        
        DaemonService daemonService = new DaemonService("test", (service) -> {
            System.out.println("轮询执行 " + service.runningCounter() + " " + service.runningTimeStamp());
            ThreadUtil.sleepUninterruptibly(3, TimeUnit.SECONDS);
        }).start();
        
        ThreadUtil.sleepUninterruptibly(10, TimeUnit.SECONDS);
        daemonService.stop();
        
        while (daemonService.isRunning()) {
            ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
        System.out.println("轮询执行 " + daemonService.runningCounter() + " " + daemonService.runningTimeStamp());
    }
    
}
