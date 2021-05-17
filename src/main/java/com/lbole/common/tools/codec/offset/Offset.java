package com.lbole.common.tools.codec.offset;

/**
 * @Author 马嘉祺
 * @Date 2019/8/29 0029 07 56
 * @Description
 */
public interface Offset {
    
    /**
     * 字节长度
     *
     * @return
     */
    int byteLength();
    
    /**
     * bit起始位置
     *
     * @return
     */
    int bitOffset();
    
    /**
     * bit长度
     *
     * @return
     */
    int bitLength();
    
    /**
     * 解码字节数组
     *
     * @param bytes
     * @param byteOffset
     * @return
     */
    default byte[] deint8(final byte[] bytes, final int byteOffset) {
        final byte[] values = new byte[byteLength()];
        System.arraycopy(bytes, byteOffset, values, 0, byteLength());
        return values;
    }
    
    /**
     * 解码int
     *
     * @param bytes
     * @param byteOffset
     * @return
     */
    int deint32(byte[] bytes, int byteOffset);
    
    /**
     * 解码long
     *
     * @param bytes
     * @param byteOffset
     * @return
     */
    long deint64(byte[] bytes, int byteOffset);
    
    /**
     * 创建Offset
     *
     * @param byteLength
     * @return
     */
    static Offset of(final int byteLength) {
        return new ByteOffset(byteLength);
    }
    
    /**
     * 创建Offset
     *
     * @param byteLength
     * @param bitOffset
     * @param bitLength
     * @return
     */
    static Offset of(final int byteLength, final int bitOffset, final int bitLength) {
        return new BitOffset(byteLength, bitOffset, bitLength);
    }
    
}
