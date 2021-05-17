package com.lbole.common.tools.codec.offset;

import com.lbole.common.tools.utils.ByteUtil;

/**
 * @Author 马嘉祺
 * @Date 2019/8/29 0029 07 56
 * @Description
 */
public class ByteOffset implements Offset {
    
    private final int byteLength;
    
    private final int bitOffset;
    
    private final int bitLength;
    
    ByteOffset(int byteLength) {
        this.byteLength = byteLength;
        this.bitOffset = 0;
        this.bitLength = byteLength * Byte.SIZE;
    }
    
    @Override
    public int byteLength() {
        return byteLength;
    }
    
    @Override
    public int bitOffset() {
        return bitOffset;
    }
    
    @Override
    public int bitLength() {
        return bitLength;
    }
    
    @Override
    public int deint32(byte[] bytes, int byteOffset) {
        return ByteUtil.readInt(bytes, byteOffset + byteLength - 1, byteLength);
    }
    
    @Override
    public long deint64(byte[] bytes, int byteOffset) {
        return ByteUtil.readLong(bytes, byteOffset + byteLength - 1, byteLength);
    }
    
}
