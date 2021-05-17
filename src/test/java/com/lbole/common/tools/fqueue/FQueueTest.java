package com.lbole.common.tools.fqueue;

import com.lbole.common.tools.utils.ByteUtil;
import com.lbole.common.tools.utils.ThreadUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author 马嘉祺
 * @Date 2020/2/10 0010 17 32
 * @Description <p></p>
 */
public class FQueueTest {
    
    private FQueuePool queuePool;
    
    private volatile boolean stopping = false;
    
    @Before
    public void before() {
        queuePool = FQueuePool.builder("D:\\FQueueTest").setDeleteBlockSec(10).build();
        Runtime.getRuntime().addShutdownHook(new Thread(this::after1));
    }
    
    private synchronized void after1() {
        ThreadUtil.sleepUninterruptibly(2, TimeUnit.SECONDS);
        if (!stopping) {
            stopping = true;
            queuePool.destroy();
            queuePool = null;
        }
    }
    
    @After
    public void after() {
        after1();
    }
    
    @Test
    public void test01() {
        for (int i = 0; i < 1000; ++i) {
            byte[] bytes = ByteUtil.parseHexBytes("232301fe56494e303030303030303030303030383000002b140307132f2a00024943434944303030303030303030303030303030010d53554253595354454d30303031b1");
            FQueue queue = queuePool.getOrCreateQueue("cusc-nev-01", 32 * 1024 * 1024);
            queue.offer(new FQueueRecord(System.currentTimeMillis(), null, bytes, new FQueueHeader(0, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9})));
            FQueueRecord poll = queue.poll();
            System.out.println(ByteUtil.toLowerHexString(poll.getRecordValue()));
        }
    }
    
    @Test
    public void producer() {
        FQueue queue = queuePool.getOrCreateQueue("cusc-nev-01", 1024 * 1024 * 1024);
        long millis = System.currentTimeMillis();
        
        byte[] content = new byte[1024];
        FQueueHeader header = new FQueueHeader(0, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07});
        
        FQueueRecord record = null;
        for (int i = 0; i < 1000000; ++i) {
            record = new FQueueRecord(System.currentTimeMillis(), null, content, header);
            boolean offer = queue.offer(record);
            if (!offer) {
                System.out.println(record.getId());
            }
        }
        queue.sync();
        
        System.out.println(System.currentTimeMillis() - millis);
        
        System.out.println(record.getId() + " " + record.getTimeStamp());
    }
    
    @Test
    public void consumer() {
        FQueue queue = queuePool.getOrCreateQueue("cusc-nev-01", 1024 * 1024 * 1024);
        FQueueRecord record = null;
        while (queue.size() > 0) {
            record = queue.poll();
        }
        if (null != record) {
            System.out.println(record.getId() + " " + record.getTimeStamp());
        }
        queue.sync();
    }
    
    @Test
    public void loopTest() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), ThreadUtil.factory("loopTestQueue-"));
        
        FQueue queue = queuePool.getOrCreateQueue("loop_test_queue", 32 * 1024 * 1024);
        byte[] content = new byte[2048];
        content[0] = 0x12;
        content[content.length - 1] = 0x34;
        FQueueHeader header = new FQueueHeader(0, "this is first header".getBytes(StandardCharsets.US_ASCII));
        
        executor.submit(() -> {
            for (int i = 0; !stopping; ++i) {
                FQueueRecord record = new FQueueRecord(System.currentTimeMillis(), String.format("%07d", i).getBytes(StandardCharsets.US_ASCII), content, header);
                boolean offer = queue.offer(record);
                if (offer) {
                    System.out.printf("1 ++++++++++ id=%016x, timestamp=%d\n", record.getId(), record.getTimeStamp());
                }
                ThreadUtil.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
            }
        });
        executor.submit(() -> {
            for (int i = 0; !stopping; ++i) {
                FQueueRecord record = new FQueueRecord(System.currentTimeMillis(), String.format("%07d", i).getBytes(StandardCharsets.US_ASCII), content, header);
                boolean offer = queue.offer(record);
                if (offer) {
                    System.out.printf("2 ++++++++++ id=%016x, timestamp=%d\n", record.getId(), record.getTimeStamp());
                }
                ThreadUtil.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
            }
        });
        executor.submit(() -> {
            while (!stopping) {
                try {
                    // timestamp
                    long timestamp = queue.peekTimeStamp();
                    // record
                    FQueueRecord record = queue.take();
                    // header
                    FQueueHeader hd = record.getHeader(0);
                    String headerValue = null == hd ? "" : new String(hd.getValue(), StandardCharsets.US_ASCII);
                    // recordKey
                    byte[] recordKeyBytes = record.getRecordKey();
                    String recordKey = null == recordKeyBytes || 0 == recordKeyBytes.length ? "" : new String(recordKeyBytes, StandardCharsets.US_ASCII);
                    // recordValue
                    byte[] recordValue = record.getRecordValue();
                    String recordValueStr = String.format("%X...%X", recordValue[0], recordValue[recordValue.length - 1]);
                    System.out.printf("1 ---------- id=%016x, timestamp=%d, header=%s, recordKey=%s, recordValue=%s\n", record.getId(), timestamp, headerValue, recordKey, recordValueStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        executor.submit(() -> {
            while (!stopping) {
                try {
                    // timestamp
                    long timestamp = queue.peekTimeStamp();
                    // record
                    FQueueRecord record = queue.take();
                    // header
                    FQueueHeader hd = record.getHeader(0);
                    String headerValue = null == hd ? "" : new String(hd.getValue(), StandardCharsets.US_ASCII);
                    // recordKey
                    byte[] recordKeyBytes = record.getRecordKey();
                    String recordKey = null == recordKeyBytes || 0 == recordKeyBytes.length ? "" : new String(recordKeyBytes, StandardCharsets.US_ASCII);
                    // recordValue
                    byte[] recordValue = record.getRecordValue();
                    String recordValueStr = String.format("%X...%X", recordValue[0], recordValue[recordValue.length - 1]);
                    System.out.printf("2 ---------- id=%016x, timestamp=%d, header=%s, recordKey=%s, recordValue=%s\n", record.getId(), timestamp, headerValue, recordKey, recordValueStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        while (!stopping) {
            ThreadUtil.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
    }
    
}
