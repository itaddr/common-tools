/*
 *  The lBole licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.itaddr.common.tools.utils;

import com.itaddr.common.tools.beans.IntLPair;
import com.itaddr.common.tools.beans.IntPair;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 字节数组相关工具
 *
 * @Author 马嘉祺
 * @Date 2019/5/16 0016 16 29
 * @Description
 */
public final class ByteUtil {
    
    private static final char[] HEX_CHAR_OF_UPPER, HEX_CHAR_OF_LOWER, BASE64_CHARS;
    private static final byte[] HEX_ASCII_CODES, BASE64_ASCII_CODES;
    private static final byte BASE64_FILL_CHAR = '=';
    
    static {
        HEX_CHAR_OF_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        HEX_CHAR_OF_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        HEX_ASCII_CODES = new byte[256];
        for (int i = 0; i < HEX_ASCII_CODES.length; ++i) {
            if (i >= '0' && i <= '9') {
                HEX_ASCII_CODES[i] = (byte) (i - '0');
            } else if (i >= 'A' && i <= 'F') {
                HEX_ASCII_CODES[i] = (byte) (i - 'A' + 10);
            } else if (i >= 'a' && i <= 'z') {
                HEX_ASCII_CODES[i] = (byte) (i - 'a' + 10);
            } else {
                HEX_ASCII_CODES[i] = -1;
            }
        }
        
        BASE64_CHARS = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '+', '/'
        };
        BASE64_ASCII_CODES = new byte[256];
        for (int i = 0; i < BASE64_ASCII_CODES.length; ++i) {
            if (i >= '0' && i <= '9') {
                BASE64_ASCII_CODES[i] = (byte) (i - '0' + 52);
            } else if (i >= 'A' && i <= 'Z') {
                BASE64_ASCII_CODES[i] = (byte) (i - 'A');
            } else if (i >= 'a' && i <= 'z') {
                BASE64_ASCII_CODES[i] = (byte) (i - 'a' + 26);
            } else {
                BASE64_ASCII_CODES[i] = -1;
            }
        }
        BASE64_ASCII_CODES['+'] = 62;
        BASE64_ASCII_CODES['/'] = 63;
    }
    
    private ByteUtil() {
    }
    
    /**
     * 将Short按大端字节序转为字节数组
     *
     * @param value
     * @return
     */
    public static byte[] readBytes(short value) {
        return new byte[]{(byte) (value >>> 8), (byte) value};
    }
    
    /**
     * 将Short按小端字节序转为字节数组
     *
     * @param value
     * @return
     */
    public static byte[] readBytesLE(short value) {
        return new byte[]{(byte) value, (byte) (value >>> 8)};
    }
    
    /**
     * 将Int按大端字节序转为字节数组
     *
     * @param value
     * @return
     */
    public static byte[] readBytes(int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
    }
    
    /**
     * 将Int按小端字节序转为字节数组
     *
     * @param value
     * @return
     */
    public static byte[] readBytesLE(int value) {
        return new byte[]{(byte) value, (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24)};
    }
    
    /**
     * 将Long按大端字节序转为字节数组
     *
     * @param value
     * @return
     */
    public static byte[] readBytes(long value) {
        return new byte[]{
                (byte) (value >>> 56), (byte) (value >>> 48), (byte) (value >>> 40), (byte) (value >>> 32),
                (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value
        };
    }
    
    /**
     * 将Long按小端字节序转为字节数组
     *
     * @param value
     * @return
     */
    public static byte[] readBytesLE(long value) {
        return new byte[]{
                (byte) value, (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24),
                (byte) (value >>> 32), (byte) (value >>> 40), (byte) (value >>> 48), (byte) (value >>> 56)
        };
    }
    
    /**
     * 按大端字节序读取Short
     *
     * @param buffer
     * @return
     */
    public static short readShort(byte[] buffer) {
        return (short) readInt(buffer, buffer.length - 1, Short.BYTES);
    }
    
    /**
     * 按大端字节序读取Short
     *
     * @param buffer
     * @param offset
     * @return
     */
    public static short readShort(byte[] buffer, int offset) {
        return (short) readInt(buffer, offset, Short.BYTES);
    }
    
    /**
     * 按小端字节序读取Short
     *
     * @param buffer
     * @return
     */
    public static short readShortLE(byte[] buffer) {
        return (short) readIntLE(buffer, 0, Short.BYTES);
    }
    
    /**
     * 按小端字节序读取Short
     *
     * @param buffer
     * @param offset
     * @return
     */
    public static short readShortLE(byte[] buffer, int offset) {
        return (short) readIntLE(buffer, offset, Short.BYTES);
    }
    
    /**
     * 按大端字节序读取Int
     *
     * @param buffer
     * @return
     */
    public static int readInt(byte[] buffer) {
        return readInt(buffer, buffer.length - 1, Integer.BYTES);
    }
    
    /**
     * 按大端字节序读取Int
     *
     * @param buffer
     * @param offset
     * @return
     */
    public static int readInt(byte[] buffer, int offset) {
        return readInt(buffer, offset, Integer.BYTES);
    }
    
    /**
     * 按小端字节序读取Int
     *
     * @param buffer
     * @return
     */
    public static int readIntLE(byte[] buffer) {
        return readIntLE(buffer, 0, Integer.BYTES);
    }
    
    /**
     * 按小端字节序读取Int
     *
     * @param buffer
     * @param offset
     * @return
     */
    public static int readIntLE(byte[] buffer, int offset) {
        return readIntLE(buffer, offset, Integer.BYTES);
    }
    
    /**
     * 按大端字节序读取Int值
     *
     * @param buffer
     * @param offset
     * @param length
     * @return
     */
    public static int readInt(byte[] buffer, int offset, int length) {
        isNotBlank(null != buffer && buffer.length > 0, "buffer must have length");
        isLegalArg(offset >= 0 && offset < buffer.length, String.format("offset=%d, bufferLen=%d, Must meet: offset >= 0 && offset < %d", offset, buffer.length, buffer.length));
        isLegalArg(length > 0 && length <= Integer.BYTES, "length=" + length + ", Must meet: length > 0 && length <= 4");
        int resultValue = 0;
        for (int i = offset, minIdx = offset - length; i >= 0 && i > minIdx; --i) {
            resultValue |= (buffer[i] & 0xff) << (offset - i) * Byte.SIZE;
        }
        return resultValue;
    }
    
    /**
     * 按大端字节序读取Int值
     *
     * @param buffer
     * @param byteOffset
     * @param bitOffset
     * @param bitLength
     * @return
     */
    public static int readInt(byte[] buffer, int byteOffset, int bitOffset, int bitLength) {
        isNotBlank(null != buffer && buffer.length > 0, "buffer must have length");
        int absoluteOffset;
        isLegalArg(byteOffset >= 0 && byteOffset < buffer.length, String.format("offset=%d, bufferLen=%d, Must meet: offset >= 0 && offset < %d", byteOffset, buffer.length, buffer.length));
        isLegalArg((absoluteOffset = byteOffset * Byte.SIZE + Byte.SIZE - 1 - bitOffset) >= 0, String.format("bufferLen=%d, illegal combination of byteOffset=%d, bitOffset=%d", buffer.length, byteOffset, bitOffset));
        isLegalArg(bitLength > 0 && bitLength <= Integer.SIZE, "illegal bitLength: " + bitLength + ", bitLength > 0 && bitLength <= 32");
        // beginBitIdx: 大端时bit的开始位置(该段数据的最后一个bit位)
        // endBitIdx: 大端时bit的结束位置(该段数据的第一个bit位)
        final int beginBitIdx = absoluteOffset;
        final int endBitIdx = beginBitIdx >= bitLength ? beginBitIdx - bitLength + 1 : 0;
        // beginByteIdx: 大端时byte的开始位置(该段数据的最后一个byte)
        // endByteIdx: 大端时byte的结束位置(该段数据的第一个byte)
        final int beginByteIdx = beginBitIdx / Byte.SIZE;
        final int endByteIdx = endBitIdx / Byte.SIZE;
        // 结果数据值
        int resultValue = 0;
        // 每个字节需要右移的值
        final int rightShiftFactor = bitOffset % Byte.SIZE;
        // 向又进行bit对齐后的字节长度
        final int rightAlignedLength = (bitOffset + bitLength) % Byte.SIZE;
        for (int i = beginByteIdx; i >= endByteIdx; --i) {
            // 当前累计的bit数
            final int currentBitSize = (beginByteIdx - i + 1) * Byte.SIZE - rightShiftFactor;
            // 如果currentBitSize大于bitLength，则需要将多出的高位与上0
            final int tmpValue = currentBitSize > bitLength ? (buffer[i] & 0xff) & 0xff >>> Byte.SIZE - rightAlignedLength : buffer[i] & 0xff;
            // 当前字节需要左移的值
            final int leftShiftFactor = (beginByteIdx - i) * Byte.SIZE;
            resultValue |= leftShiftFactor < rightShiftFactor ? tmpValue >>> rightShiftFactor - leftShiftFactor : tmpValue << leftShiftFactor - rightShiftFactor;
        }
        return resultValue;
    }
    
    /**
     * 按小端字节序读取Int值
     *
     * @param buffer
     * @param offset
     * @param length
     * @return
     */
    public static int readIntLE(byte[] buffer, int offset, int length) {
        isNotBlank(null != buffer && buffer.length > 0, "buffer must have length");
        isLegalArg(offset >= 0 && offset < buffer.length, String.format("offset=%d, bufferLen=%d, Must meet: offset >= 0 && offset < %d", offset, buffer.length, buffer.length));
        isLegalArg(length > 0 && length <= Integer.BYTES, "length=" + length + ", Must meet: length > 0 && length <= 4");
        int resultValue = 0;
        for (int i = offset, maxIdx = offset + length; i < buffer.length && i < maxIdx; ++i) {
            resultValue |= (buffer[i] & 0xff) << (i - offset) * Byte.SIZE;
        }
        return resultValue;
    }
    
    /**
     * 按大端字节序读取Long值
     *
     * @param buffer
     * @return
     */
    public static long readLong(byte[] buffer) {
        return readLong(buffer, buffer.length - 1, Long.BYTES);
    }
    
    /**
     * 按大端字节序读取Long值
     *
     * @param buffer
     * @param offset
     * @return
     */
    public static long readLong(byte[] buffer, int offset) {
        return readLong(buffer, offset, Long.BYTES);
    }
    
    /**
     * 按小端字节序读取Long值
     *
     * @param buffer
     * @return
     */
    public static long readLongLE(byte[] buffer) {
        return readLongLE(buffer, 0, Long.BYTES);
    }
    
    /**
     * 按小端字节序读取Long值
     *
     * @param buffer
     * @param offset
     * @return
     */
    public static long readLongLE(byte[] buffer, int offset) {
        return readLongLE(buffer, offset, Long.BYTES);
    }
    
    /**
     * 按大端字节序读取Long值
     *
     * @param buffer
     * @param offset
     * @param length
     * @return
     */
    public static long readLong(byte[] buffer, int offset, int length) {
        isNotBlank(null != buffer && buffer.length > 0, "buffer must have length");
        isLegalArg(offset >= 0 && offset < buffer.length, String.format("offset=%d, bufferLen=%d, Must meet: offset >= 0 && offset < %d", offset, buffer.length, buffer.length));
        isLegalArg(length > 0 && length <= Long.BYTES, "length=" + length + ", Must meet: length > 0 && length <= 8");
        long resultValue = 0;
        for (int i = offset, minIdx = offset - length; i >= 0 && i > minIdx; --i) {
            resultValue |= (buffer[i] & 0xffL) << (offset - i) * Byte.SIZE;
        }
        return resultValue;
    }
    
    /**
     * 按大端字节序读取Long值
     *
     * @param buffer
     * @param byteOffset
     * @param bitOffset
     * @param bitLength
     * @return
     */
    public static long readLong(byte[] buffer, int byteOffset, int bitOffset, int bitLength) {
        isNotBlank(null != buffer && buffer.length > 0, "buffer must have length");
        int absoluteOffset;
        isLegalArg(byteOffset >= 0 && byteOffset < buffer.length, String.format("offset=%d, bufferLen=%d, Must meet: offset >= 0 && offset < %d", byteOffset, buffer.length, buffer.length));
        isLegalArg((absoluteOffset = byteOffset * Byte.SIZE + Byte.SIZE - 1 - bitOffset) >= 0, String.format("bufferLen=%d, illegal combination of byteOffset=%d, bitOffset=%d", buffer.length, byteOffset, bitOffset));
        isLegalArg(bitLength > 0 && bitLength <= Long.SIZE, "illegal bitLength: " + bitLength + ", bitLength > 0 && bitLength <= 64");
        // beginBitIdx: 大端时bit的开始位置(该段数据的最后一个bit位)
        // endBitIdx: 大端时bit的结束位置(该段数据的第一个bit位)
        final int beginBitIdx = absoluteOffset;
        final int endBitIdx = beginBitIdx >= bitLength ? beginBitIdx - bitLength + 1 : 0;
        // beginByteIdx: 大端时byte的开始位置(该段数据的最后一个byte)
        // endByteIdx: 大端时byte的结束位置(该段数据的第一个byte)
        final int beginByteIdx = beginBitIdx / Byte.SIZE;
        final int endByteIdx = endBitIdx / Byte.SIZE;
        // 计算结果值
        long resultValue = 0;
        // 每个字节需要右移的值
        final int rightShiftFactor = bitOffset % Byte.SIZE;
        // 向又进行位对齐后的字节长度
        final int rightAlignedLength = (bitOffset + bitLength) % Byte.SIZE;
        for (int i = beginByteIdx; i >= endByteIdx; --i) {
            // 当前累计的bit数
            final int currentBitSize = (beginByteIdx - i + 1) * Byte.SIZE - rightShiftFactor;
            // 如果currentBitSize大于bitLength，则需要将多出的高位与上0
            final long tmpValue = currentBitSize > bitLength ? (buffer[i] & 0xffL) & 0xffL >>> Byte.SIZE - rightAlignedLength : buffer[i] & 0xffL;
            // 当前字节需要左移的值
            final int leftShiftFactor = (beginByteIdx - i) * Byte.SIZE;
            resultValue |= leftShiftFactor < rightShiftFactor ? tmpValue >>> rightShiftFactor - leftShiftFactor : tmpValue << leftShiftFactor - rightShiftFactor;
        }
        return resultValue;
    }
    
    /**
     * 按小端字节序读取Long值
     *
     * @param offset
     * @param length
     * @param buffer
     * @return
     */
    public static long readLongLE(byte[] buffer, int offset, int length) {
        isNotBlank(null != buffer && buffer.length > 0, "buffer must have length");
        isLegalArg(offset >= 0 && offset < buffer.length, String.format("offset=%d, bufferLen=%d, Must meet: offset >= 0 && offset < %d", offset, buffer.length, buffer.length));
        isLegalArg(length > 0 && length <= Long.BYTES, "length=" + length + ", Must meet: length > 0 && length <= 8");
        long resultValue = 0;
        for (int i = offset, maxIdx = offset + length; i < buffer.length && i < maxIdx; ++i) {
            resultValue |= (buffer[i] & 0xffL) << (i - offset) * Byte.SIZE;
        }
        return resultValue;
    }
    
    public void writeBytes(byte[] buffer, int offset, short value) {
        writeBytes(buffer, offset, 2, value & 0xffff);
    }
    
    public void writeBytesLE(byte[] buffer, int offset, short value) {
        writeBytesLE(buffer, offset, 2, value & 0xffff);
    }
    
    public void writeBytes(byte[] buffer, int offset, int value) {
        writeBytes(buffer, offset, 4, value);
    }
    
    public void writeBytes(byte[] buffer, int offset, int length, int value) {
    
    }
    
    public void writeBytesLE(byte[] buffer, int offset, int value) {
        writeBytesLE(buffer, offset, 4, value);
    }
    
    public void writeBytesLE(byte[] buffer, int offset, int length, int value) {
    
    }
    
    public void writeBytes(byte[] buffer, int offset, long value) {
        writeBytes(buffer, offset, 8, value);
    }
    
    public void writeBytes(byte[] buffer, int offset, int length, long value) {
    
    }
    
    public void writeBytesLE(byte[] buffer, int offset, long value) {
        writeBytesLE(buffer, offset, 8, value);
    }
    
    public void writeBytesLE(byte[] buffer, int offset, int length, long value) {
    
    }
    
    /**
     * 获取整数按var128编码之后的字节长度
     *
     * @param value
     * @return
     */
    public static int varIntLen(int value) {
        int len = 1;
        while ((value & 0xffffff80) != 0) {
            value >>>= 7;
            ++len;
        }
        return len;
    }
    
    /**
     * 获取整数按var128编码之后的字节长度
     *
     * @param value
     * @return
     */
    public static int varIntLen(long value) {
        int len = 1;
        while ((value & 0xffffffffffffff80L) != 0) {
            value >>>= 7;
            ++len;
        }
        return len;
    }
    
    /**
     * 将整数按var128编码之后的数据填充到buffer缓冲区
     *
     * @param value
     * @param offset
     * @param payloads
     */
    public static void enVarInt(int value, int offset, byte[] payloads) {
        for (; (value & 0xffffff80) != 0; value >>>= 7, ++offset) {
            payloads[offset] = (byte) (value & 0x7f | 0x80);
        }
        payloads[offset] = (byte) value;
    }
    
    /**
     * 将整数按var128编码
     *
     * @param value
     * @return
     */
    public static byte[] enVarInt(int value) {
        int size = 0;
        byte[] temps = new byte[5];
        for (; (value & 0xffffff80) != 0; value >>>= 7, ++size) {
            temps[size] = (byte) (value & 0x7f | 0x80);
        }
        temps[size] = (byte) value;
        byte[] result = new byte[size + 1];
        System.arraycopy(temps, 0, result, 0, result.length);
        return result;
    }
    
    /**
     * 将整数按var128编码之后的数据填充到buffer缓冲区
     *
     * @param value
     * @param offset
     * @param payloads
     */
    public static void enVarInt(long value, int offset, byte[] payloads) {
        for (; (value & 0xffffffffffffff80L) != 0; value >>>= 7, ++offset) {
            payloads[offset] = (byte) (value & 0x7fL | 0x80L);
        }
        payloads[offset] = (byte) value;
    }
    
    /**
     * 编码var128
     *
     * @param value
     * @return
     */
    public static byte[] enVarInt(long value) {
        int size = 0;
        byte[] temps = new byte[10];
        for (; (value & 0xffffffffffffff80L) != 0; value >>>= 7, ++size) {
            temps[size] = (byte) (value & 0x7fL | 0x80L);
        }
        temps[size] = (byte) value;
        byte[] result = new byte[size + 1];
        System.arraycopy(temps, 0, result, 0, result.length);
        return result;
    }
    
    /**
     * 解码var128
     *
     * @param buffer
     * @param offset
     * @return
     */
    public static IntPair deVarInt(byte[] buffer, int offset) {
        isLegalArg(offset >= 0 && offset < buffer.length, String.format("offset=%d, bufferLen=%d, Must meet: offset >= 0 && offset < %d", offset, buffer.length, buffer.length));
        int bitSize = 0, result = 0, length = 0;
        for (; offset < buffer.length && bitSize < Integer.SIZE; ++offset) {
            int value = buffer[offset] & 0xff;
            result |= (value & 0x7f) << bitSize;
            bitSize += 7;
            ++length;
            if (0 == (value & 0x80)) {
                break;
            }
        }
        return IntPair.of(length, result);
    }
    
    /**
     * 解码var128
     *
     * @param buffer
     * @param offset
     * @return
     */
    public static IntLPair deVarLong(byte[] buffer, int offset) {
        isLegalArg(offset >= 0 && offset < buffer.length, String.format("offset=%d, bufferLen=%d, Must meet: offset >= 0 && offset < %d", offset, buffer.length, buffer.length));
        int bitSize = 0, length = 0;
        long result = 0;
        for (; offset < buffer.length && bitSize < Long.SIZE; ++offset) {
            int value = buffer[offset] & 0xff;
            result |= (value & 0x7fL) << bitSize;
            bitSize += 7;
            ++length;
            if (0 == (value & 0x80)) {
                break;
            }
        }
        return IntLPair.of(length, result);
    }
    
    public static int enZigZag(int value) {
        return (value << 1) ^ (value >> 31);
    }
    
    public static long enZigZag(long value) {
        return (value << 1) ^ (value >> 63);
    }
    
    public static int deZigZag(int value) {
        return (value >>> 1) ^ -(value & 1);
    }
    
    public static long deZigZag(long value) {
        return (value >>> 1) ^ -(value & 1);
    }
    
    /**
     * 将字节数组编码为二进制字符串
     *
     * @param buffer
     * @return
     */
    public static String toBinaryString(byte[] buffer) {
        isNotBlank(null != buffer, "buffer must have length");
        if (buffer.length == 0) {
            return null;
        }
        return toBinaryString(buffer, buffer.length * Byte.SIZE);
    }
    
    private static String toBinaryString(byte[] buffer, int length) {
        final int bs = Byte.SIZE;
        char[] results = new char[length];
        for (int i = 0; i < buffer.length; ++i) {
            for (int n = 0; n < bs; ++n) {
                results[i * bs + n] = (char) ((buffer[i] >>> (bs - n - 1) & 0x01) + '0');
            }
        }
        return new String(results);
    }
    
    /**
     * 将Short编码为二进制字符串
     *
     * @param value
     * @return
     */
    public static String toBinaryString(short value) {
        return toBinaryString(new byte[]{(byte) (value >>> 8), (byte) value}, 16);
    }
    
    /**
     * 将Int编码为二进制字符串
     *
     * @param value
     * @return
     */
    public static String toBinaryString(int value) {
        return toBinaryString(new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value}, 32);
    }
    
    /**
     * 将Long编码为二进制字符串
     *
     * @param value
     * @return
     */
    public static String toBinaryString(long value) {
        return toBinaryString(new byte[]{
                (byte) (value >>> 56), (byte) (value >>> 48), (byte) (value >>> 40), (byte) (value >>> 32),
                (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value
        }, 64);
    }
    
    /**
     * 将二进制字符串解码为字节数组
     *
     * @param binaryString
     * @return
     */
    public static byte[] parseBinaryBytes(String binaryString) {
        if (null == binaryString || 0 == binaryString.length()) {
            return new byte[0];
        }
        if (binaryString.length() % Byte.SIZE != 0) {
            throw new IllegalArgumentException("hexString length must be a multiple of 8");
        }
        char[] sources = binaryString.toCharArray();
        byte[] results = new byte[sources.length / Byte.SIZE];
        for (int i = 0; i < results.length; ++i) {
            for (int n = 0; n < Byte.SIZE; ++n) {
                char asciiCode = sources[i * Byte.SIZE + n];
                if (asciiCode != '0' && asciiCode != '1') {
                    throw new NumberFormatException(String.format("'%c' is not a binary character", asciiCode));
                }
                results[i] |= (byte) ((asciiCode - '0') << (Byte.SIZE - n - 1));
            }
        }
        return results;
    }
    
    /**
     * 将字节数组编码成16进制字符串（大写字母）
     *
     * @param buffer
     * @return
     */
    public static String toUpperHexString(byte[] buffer) {
        if (null == buffer || 0 == buffer.length) {
            return null;
        }
        return toUpperHexString(buffer, buffer.length * 2);
    }
    
    public static String toUpperHexString(byte[] buffer, int length) {
        char[] results = new char[length];
        for (int i = 0; i < buffer.length; ++i) {
            results[i * 2] = HEX_CHAR_OF_UPPER[buffer[i] >>> 4 & 0x0f];
            results[i * 2 + 1] = HEX_CHAR_OF_UPPER[buffer[i] & 0x0f];
        }
        return new String(results);
    }
    
    /**
     * 将Short编码成16进制字符串（大写字母）
     *
     * @param value
     * @return
     */
    public static String toUpperHexString(short value) {
        return toUpperHexString(new byte[]{(byte) (value >>> 8), (byte) value}, 4);
    }
    
    /**
     * 将Int编码成16进制字符串（大写字母）
     *
     * @param value
     * @return
     */
    public static String toUpperHexString(int value) {
        return toUpperHexString(new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value}, 8);
    }
    
    /**
     * 将Long编码成16进制字符串（大写字母）
     *
     * @param value
     * @return
     */
    public static String toUpperHexString(long value) {
        return toUpperHexString(new byte[]{
                (byte) (value >>> 56), (byte) (value >>> 48), (byte) (value >>> 40), (byte) (value >>> 32),
                (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value
        }, 16);
    }
    
    /**
     * 将字节数组编码成16进制字符串（小写字母）
     *
     * @param buffer
     * @return
     */
    public static String toLowerHexString(byte[] buffer) {
        if (null == buffer || 0 == buffer.length) {
            return null;
        }
        return toLowerHexString(buffer, buffer.length * 2);
    }
    
    public static String toLowerHexString(byte[] buffer, int length) {
        char[] results = new char[length];
        for (int i = 0; i < buffer.length; ++i) {
            results[i * 2] = HEX_CHAR_OF_LOWER[buffer[i] >>> 4 & 0x0f];
            results[i * 2 + 1] = HEX_CHAR_OF_LOWER[buffer[i] & 0x0f];
        }
        return new String(results);
    }
    
    /**
     * 将Short编码成16进制字符串（小写字母）
     *
     * @param value
     * @return
     */
    public static String toLowerHexString(short value) {
        return toLowerHexString(new byte[]{(byte) (value >>> 8), (byte) value}, 4);
    }
    
    /**
     * 将Int编码成16进制字符串（小写字母）
     *
     * @param value
     * @return
     */
    public static String toLowerHexString(int value) {
        return toLowerHexString(new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value}, 8);
    }
    
    /**
     * 将Long编码成16进制字符串（小写字母）
     *
     * @param value
     * @return
     */
    public static String toLowerHexString(long value) {
        return toLowerHexString(new byte[]{
                (byte) (value >>> 56), (byte) (value >>> 48), (byte) (value >>> 40), (byte) (value >>> 32),
                (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value
        }, 16);
    }
    
    /**
     * 将16进制字符串解码为字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] parseHexBytes(String hexString) {
        if (null == hexString || 0 == hexString.length()) {
            return new byte[0];
        }
        if (hexString.length() % Short.BYTES != 0) {
            throw new IllegalArgumentException("hexString length must be even");
        }
        byte[] sources = hexString.getBytes(StandardCharsets.US_ASCII);
        byte[] results = new byte[sources.length / 2];
        for (int i = 0; i < results.length; ++i) {
            int highAscii = sources[i * 2] & 0xff;
            int lowAscii = sources[i * 2 + 1] & 0xff;
            byte highValue;
            if ((highValue = HEX_ASCII_CODES[highAscii]) == -1) {
                throw new NumberFormatException(String.format("'%c' is not a hexadecimal character", highAscii));
            }
            byte lowValue;
            if ((lowValue = HEX_ASCII_CODES[lowAscii]) == -1) {
                throw new NumberFormatException(String.format("'%c' is not a hexadecimal character", lowAscii));
            }
            results[i] = (byte) (highValue << 4 | lowValue & 0xff);
        }
        return results;
    }
    
    /**
     * 将字节数组编码为Base64字符串
     *
     * @param buffer
     * @return
     */
    public static String toBase64String(byte[] buffer) {
        if (null == buffer || 0 == buffer.length) {
            return null;
        }
        char[] results;
        int byteLen = buffer.length, resultLen;
        int gnum, mod = byteLen % 3;
        if (0 == mod) {
            gnum = byteLen / 3 - 1;
            resultLen = gnum * 4 + 4;
            results = new char[resultLen];
            int b1 = buffer[byteLen - 3], b2 = buffer[byteLen - 2], b3 = buffer[byteLen - 1];
            results[resultLen - 4] = BASE64_CHARS[b1 >>> 2 & 0b00111111];
            results[resultLen - 3] = BASE64_CHARS[b1 << 4 & 0b00110000 | b2 >>> 4 & 0b00001111];
            results[resultLen - 2] = BASE64_CHARS[b2 << 2 & 0b00111100 | b3 >>> 6 & 0b00000011];
            results[resultLen - 1] = BASE64_CHARS[b3 & 0b00111111];
        } else if (1 == mod) {
            gnum = byteLen / 3;
            resultLen = gnum * 4 + 4;
            results = new char[resultLen];
            int b1 = buffer[byteLen - 1];
            results[resultLen - 4] = BASE64_CHARS[b1 >>> 2 & 0b00111111];
            results[resultLen - 3] = BASE64_CHARS[b1 << 4 & 0b00110000];
            results[resultLen - 1] = BASE64_FILL_CHAR;
            results[resultLen - 2] = BASE64_FILL_CHAR;
        } else {
            gnum = byteLen / 3;
            resultLen = gnum * 4 + 4;
            results = new char[resultLen];
            int b1 = buffer[byteLen - 2], b2 = buffer[byteLen - 1];
            results[resultLen - 4] = BASE64_CHARS[b1 >>> 2 & 0b00111111];
            results[resultLen - 3] = BASE64_CHARS[b1 << 4 & 0b00110000 | b2 >>> 4 & 0b00001111];
            results[resultLen - 2] = BASE64_CHARS[b2 << 2 & 0b00111100];
            results[resultLen - 1] = BASE64_FILL_CHAR;
        }
        for (int i = 0; i < gnum; ++i) {
            int sIdx = i * 3, rIdx = i * 4;
            int b1 = buffer[sIdx], b2 = buffer[sIdx + 1], b3 = buffer[sIdx + 2];
            results[rIdx] = BASE64_CHARS[b1 >>> 2 & 0b00111111];
            results[rIdx + 1] = BASE64_CHARS[b1 << 4 & 0b00110000 | b2 >>> 4 & 0b00001111];
            results[rIdx + 2] = BASE64_CHARS[b2 << 2 & 0b00111100 | b3 >>> 6 & 0b00000011];
            results[rIdx + 3] = BASE64_CHARS[b3 & 0b00111111];
        }
        return new String(results);
    }
    
    /**
     * 将Base64字符串解码为字节数组
     *
     * @param base64String
     * @return
     */
    public static byte[] parseBase64Bytes(String base64String) {
        if (null == base64String || 0 == base64String.length()) {
            return new byte[0];
        }
        byte[] sources = base64String.getBytes(StandardCharsets.US_ASCII), results;
        int sourceLen = sources.length, resultLen;
        int gnum = sourceLen / 4 - 1, mod = sourceLen % 4;
        if (0 != mod) {
            throw new IllegalArgumentException("base64String length must be a multiple of 4");
        }
        byte c1 = sources[sourceLen - 4], c2 = sources[sourceLen - 3], c3 = sources[sourceLen - 2], c4 = sources[sourceLen - 1];
        byte b1 = BASE64_ASCII_CODES[c1], b2 = BASE64_ASCII_CODES[c2], b3 = BASE64_ASCII_CODES[c3], b4 = BASE64_ASCII_CODES[c4];
        if (-1 == b1 || -1 == b2) {
            throw new NumberFormatException(String.format("'%c%c' is not a base64 character", c1, c2));
        }
        if (BASE64_FILL_CHAR == c3 && BASE64_FILL_CHAR == c4) {
            resultLen = sourceLen / 4 * 3 - 2;
            results = new byte[resultLen];
            results[resultLen - 1] = (byte) (b1 << 2 & 0b11111100 | b2 >>> 4 & 0b00000011);
        } else if (-1 != b3 && BASE64_FILL_CHAR == c4) {
            resultLen = sourceLen / 4 * 3 - 1;
            results = new byte[resultLen];
            results[resultLen - 2] = (byte) (b1 << 2 & 0b11111100 | b2 >>> 4 & 0b00000011);
            results[resultLen - 1] = (byte) (b2 << 4 & 0b11110000 | b3 >>> 2 & 0b00001111);
        } else if (-1 != b3 && -1 != b4) {
            resultLen = sourceLen / 4 * 3;
            results = new byte[resultLen];
            results[resultLen - 3] = (byte) (b1 << 2 & 0b11111100 | b2 >>> 4 & 0b00000011);
            results[resultLen - 2] = (byte) (b2 << 4 & 0b11110000 | b3 >>> 2 & 0b00001111);
            results[resultLen - 1] = (byte) (b3 << 6 & 0b11000000 | b4 & 0b00111111);
        } else {
            throw new NumberFormatException(String.format("'%c%c' is not a base64 character", c3, c4));
        }
        for (int i = 0; i < gnum; ++i) {
            int sIdx = i * 4, rIdx = i * 3;
            c1 = sources[sIdx];
            c2 = sources[sIdx + 1];
            c3 = sources[sIdx + 2];
            c4 = sources[sIdx + 3];
            b1 = BASE64_ASCII_CODES[c1];
            b2 = BASE64_ASCII_CODES[c2];
            b3 = BASE64_ASCII_CODES[c3];
            b4 = BASE64_ASCII_CODES[c4];
            if (-1 == b1 || -1 == b2 || -1 == b3 || -1 == b4) {
                throw new NumberFormatException(String.format("'%c%c%c%c' is not a base64 character", c1, c2, c3, c4));
            }
            results[rIdx] = (byte) (b1 << 2 & 0b11111100 | b2 >>> 4 & 0b00000011);
            results[rIdx + 1] = (byte) (b2 << 4 & 0b11110000 | b3 >>> 2 & 0b00001111);
            results[rIdx + 2] = (byte) (b3 << 6 & 0b11000000 | b4 & 0b00111111);
        }
        return results;
    }
    
    /**
     * 将字符串按ASCII编码，并将unicode代码做转换
     *
     * @param string
     * @return
     */
    public static byte[] toAsciiBytes(String string) {
        if (null == string || 0 == string.length()) {
            return new byte[0];
        }
        byte[] bytes = string.getBytes(StandardCharsets.US_ASCII);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        for (int i = 0; i < bytes.length; ++i) {
            int hex1 = 0, hex2 = 0;
            // 判断剩余字符个数不小于6，并且格式为“\\u00xx”
            boolean isEscapeUnreadChar = '\\' == bytes[i] && bytes.length - i >= 6 && 'u' == bytes[i + 1] && '0' == bytes[i + 2] && '0' == bytes[i + 3]
                    && -1 != (hex1 = HEX_ASCII_CODES[bytes[i + 4] & 0xff]) && -1 != (hex2 = HEX_ASCII_CODES[bytes[i + 5] & 0xff]);
            if (isEscapeUnreadChar) {
                byteBuffer.put((byte) (hex1 << 4 | hex2));
                i += 5;
            } else {
                byteBuffer.put(bytes[i]);
            }
        }
        byte[] results = new byte[byteBuffer.flip().limit()];
        byteBuffer.get(results);
        return results;
    }
    
    /**
     * 将字节数组按ASCII解码，并将不可打印字符转义为unicode代码
     *
     * @param buffer
     * @return
     */
    public static String parseAsciiString(byte[] buffer) {
        if (null == buffer || 0 == buffer.length) {
            return null;
        }
        StringBuilder sb = new StringBuilder(buffer.length);
        for (byte b : buffer) {
            if (b >= 32 && b <= 126) {
                sb.append((char) b);
            } else {
                sb.append(String.format("\\u%04x", b & 0xff));
            }
        }
        return sb.toString();
    }
    
    private static void isNotBlank(boolean isNotBlank, String message) {
        if (!isNotBlank) {
            throw new NullPointerException(message);
        }
    }
    
    private static void isLegalArg(boolean isLegalArg, String message) {
        if (!isLegalArg) {
            throw new IllegalArgumentException(message);
        }
    }
    
}
