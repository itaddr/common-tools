package com.lbole.common.tools.codec.offset;

import com.lbole.common.tools.utils.ByteUtil;

/**
 * @Author 马嘉祺
 * @Date 2019/8/29 0029 07 56
 * @Description
 */
public class BitOffset implements Offset {
    
    private final int byteLength;
    
    private final int bitOffset;
    
    private final int bitLength;
    
    BitOffset(int byteLength, int bitOffset, int bitLength) {
        this.byteLength = byteLength;
        this.bitOffset = bitOffset;
        this.bitLength = bitLength;
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
        return ByteUtil.readInt(bytes, byteOffset + byteLength - 1, bitOffset, bitLength);
    }
    
    @Override
    public long deint64(byte[] bytes, int byteOffset) {
        return ByteUtil.readLong(bytes, byteOffset + byteLength - 1, bitOffset, bitLength);
    }
    
}
