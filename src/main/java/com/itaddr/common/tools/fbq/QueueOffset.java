package com.itaddr.common.tools.fbq;

import com.itaddr.common.tools.fbq.queue.Segment;
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
 * @Date 2020/6/10 0010 09 50
 * @Description <p></p>
 */
public class QueueOffset {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueOffset.class);
    
    /**
     * 队列中OFFSET项索引项位置
     */
    private static final int COUNTER_OFFSET = 8;
    private static final int FILE_NO_OFFSET = 16;
    private static final int POSITION_OFFSET = 24;
    
    /**
     * 队列OFFSET文件名称
     */
    private static final String OFFSET_FILE_NAME = "__offset_checkpoint";
    
    /**
     * 队列OFFSET文件大小
     */
    private static final int OFFSET_FILE_SIZE = 28;
    
    /**
     * 已读的最后一个记录编号, 起始位置{@link #COUNTER_OFFSET}
     */
    private volatile long counter;
    
    /**
     * 正在读的LOG文件号, 起始位置{@link #FILE_NO_OFFSET}
     */
    private volatile long fileNo;
    
    /**
     * 正在读的文件中索引位置, 起始位置{@link #POSITION_OFFSET}
     */
    private volatile int position;
    
    /**
     * 读偏移量缓冲
     */
    private MappedByteBuffer byteBuffer;
    
    /**
     * 队列META对象
     */
    private QueueMeta queueMeta;
    
    QueueOffset(QueueMeta queueMeta, String offsetFileName, boolean fromBeginning) {
        this.queueMeta = queueMeta;
        File file = new File(offsetFileName);
        RandomAccessFile accessFile = null;
        try {
            // 判断索引文件是否存在
            if (file.exists()) {
                accessFile = new RandomAccessFile(file, "rw");
                // 校验魔数
                if (BootFileQueue.MAGIC != accessFile.readInt()) {
                    throw new IllegalStateException(String.format("'%s'不是offset文件", offsetFileName));
                }
                // 校验版本
                int version;
                if (BootFileQueue.VERSION != (version = accessFile.readInt())) {
                    throw new IllegalStateException(String.format("offset版本期望是v%d.%d, 实际版本是v%d.%d", BootFileQueue.VERSION >>> 2, BootFileQueue.VERSION & 0xffff, version >>> 2, version & 0xffff));
                }
                // 获取offset中各个索引数据项
                this.counter = accessFile.readLong();
                this.fileNo = accessFile.readLong();
                this.position = accessFile.readInt();
                // 获取meta缓冲区
                this.byteBuffer = accessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, OFFSET_FILE_SIZE).load();
            } else {
                accessFile = new RandomAccessFile(file, "rw");
                // 获取meta缓冲区
                this.byteBuffer = accessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, OFFSET_FILE_SIZE).load();
                // 填充魔数、版本、块文件大小
                byteBuffer.putInt(0, BootFileQueue.MAGIC).putInt(4, BootFileQueue.VERSION);
                // 初始化相关索引项
                putCounter(0);
                putFileNo(0);
                putPosition(Segment.BEGIN_OFFSET);
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
    
    public QueueMeta getQueueMeta() {
        return queueMeta;
    }
    
    public long getCounter() {
        return this.counter;
    }
    
    public long getFileNo() {
        return this.fileNo;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void putCounter(long counter) {
        byteBuffer.putLong(COUNTER_OFFSET, counter);
        this.counter = counter;
    }
    
    public void putFileNo(long fileNo) {
        byteBuffer.putLong(FILE_NO_OFFSET, fileNo);
        this.fileNo = fileNo;
    }
    
    public void putPosition(int position) {
        byteBuffer.putInt(POSITION_OFFSET, position);
        this.position = position;
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
