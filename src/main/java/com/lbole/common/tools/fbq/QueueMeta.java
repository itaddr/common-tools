package com.lbole.common.tools.fbq;

import com.lbole.common.tools.fbq.queue.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Cleaner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @Author 马嘉祺
 * @Date 2020/6/9 0009 11 00
 * @Description <p></p>
 */
public class QueueMeta {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueMeta.class);
    
    /**
     * 队列中各META项位置
     */
    private static final int LOG_SIZE_OFFSET = 8;
    
    private static final long EAR_COUNTER_OFFSET = 12;
    private static final long EAR_FILE_NO_OFFSET = 20;
    private static final long LAT_COUNTER_OFFSET = 28;
    private static final long LAT_FILE_NO_OFFSET = 36;
    private static final int POSITION_OFFSET = 44;
    
    private static final int WRITE_NUM_OFFSET = 12;
    private static final int WRITE_POS_OFFSET = 20;
    private static final int WRITE_CNT_OFFSET = 24;
    
    /**
     * 队列META文件名称
     */
    private static final String META_FILE_NAME = "__meta_checkpoint";
    
    /**
     * 队列META文件大小
     */
    private static final int META_FILE_SIZE = 32;
    
    /**
     * LOG文件大小, 起始位置{@link #LOG_SIZE_OFFSET}
     */
    private volatile int logSize;
    
    
    private volatile long earCounter;
    
    private volatile long earFileNo;
    
    private volatile long latCounter;
    
    private volatile long latFileNo;
    
    private volatile int position;
    
    
    /**
     * 正在写的LOG文件号, 起始位置{@link #WRITE_NUM_OFFSET}
     */
    private volatile long writeFileNum;
    
    /**
     * 正在写的索引位置, 起始位置{@link #WRITE_POS_OFFSET}
     */
    private volatile int writePosition;
    
    /**
     * 已写Record计数器值, 起始位置{@link #WRITE_CNT_OFFSET}
     */
    private volatile long writeCounter;
    
    /**
     * META缓冲区
     */
    private MappedByteBuffer byteBuffer;
    
    public QueueMeta(String metaFileName, int logSize) {
        File file = new File(metaFileName);
        RandomAccessFile accessFile = null;
        try {
            // 判断索引文件是否存在
            if (file.exists()) {
                accessFile = new RandomAccessFile(file, "rw");
                // 校验魔数
                if (BootFileQueue.MAGIC != accessFile.readInt()) {
                    throw new IllegalStateException(String.format("'%s'不是meta文件", metaFileName));
                }
                // 校验版本
                int version;
                if (BootFileQueue.VERSION != (version = accessFile.readInt())) {
                    throw new IllegalStateException(String.format("meta版本期望是v%d.%d, 实际版本是v%d.%d", BootFileQueue.VERSION >>> 2, BootFileQueue.VERSION & 0xffff, version >>> 2, version & 0xffff));
                }
                // 获取meta中各个索引数据项
                this.logSize = accessFile.readInt();
                this.writeFileNum = accessFile.readLong();
                this.writePosition = accessFile.readInt();
                this.writeCounter = accessFile.readLong();
                // 获取meta缓冲区
                this.byteBuffer = accessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, META_FILE_SIZE).load();
            } else {
                this.logSize = logSize;
                accessFile = new RandomAccessFile(file, "rw");
                // 获取meta缓冲区
                this.byteBuffer = accessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, META_FILE_SIZE).load();
                // 填充魔数、版本、块文件大小
                byteBuffer.putInt(0, BootFileQueue.MAGIC).putInt(4, BootFileQueue.VERSION).putInt(LOG_SIZE_OFFSET, logSize);
                // 初始化相关索引项
                putWriteFileNum(0);
                putWritePosition(Segment.BEGIN_OFFSET);
                putWriteCounter(0);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (null != accessFile) {
                try {
                    accessFile.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
    
    /**
     * 根据队列名称与队列文件根目录获取索引文件名称
     *
     * @param rootDirPath
     * @param queueName
     * @return
     */
    public static String formatFilePath(String rootDirPath, String queueName) {
        return rootDirPath + File.separator + queueName + File.separator + META_FILE_NAME;
    }
    
    public int getLogSize() {
        return logSize;
    }
    
    public long getWriteFileNum() {
        return writeFileNum;
    }
    
    public int getWritePosition() {
        return writePosition;
    }
    
    public long getWriteCounter() {
        return writeCounter;
    }
    
    public void putWritePosition(int writePosition) {
        byteBuffer.putInt(WRITE_POS_OFFSET, writePosition);
        this.writePosition = writePosition;
    }
    
    public void putWriteFileNum(long writeNum) {
        byteBuffer.putLong(WRITE_NUM_OFFSET, writeNum);
        this.writeFileNum = writeNum;
    }
    
    public void putWriteCounter(long writeCounter) {
        byteBuffer.putLong(WRITE_CNT_OFFSET, writeCounter);
        this.writeCounter = writeCounter;
    }
    
    public void sync() {
        byteBuffer.force();
    }
    
    public void close() {
        sync();
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                Method cleanerMethod = byteBuffer.getClass().getMethod("cleaner");
                cleanerMethod.setAccessible(true);
                Cleaner cleaner = (Cleaner) cleanerMethod.invoke(byteBuffer);
                cleaner.clean();
            } catch (Exception e) {
                LOGGER.error("关闭fqueue索引文件失败", e);
            }
            return null;
        });
        this.byteBuffer = null;
    }
    
}
