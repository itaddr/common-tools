package com.itaddr.common.tools.utils;

import com.itaddr.common.tools.beans.IntLPair;
import com.itaddr.common.tools.beans.IntPair;
import org.junit.Test;

import java.util.Base64;

/**
 * @Author 马嘉祺
 * @Date 2020/5/31 0031 22 41
 * @Description <p></p>
 */
public class ByteUtilTest {
    
    @Test
    public void readInt() {
        
        byte[] bytes = {
                (byte) 0b10101101, (byte) 0b01010111, (byte) 0b10000001, (byte) 0b10011001,
                (byte) 0b11010101, (byte) 0b10101111, (byte) 0b11010111, (byte) 0b11010111
        };
        System.out.println(ByteUtil.readInt(bytes, 4, 38, 10));
        
    }
    
    @Test
    public void readLong() {
        byte[] bytes = {
                (byte) 0b10101101, (byte) 0b01010111, (byte) 0b10000001, (byte) 0b10011001,
                (byte) 0b11010101, (byte) 0b10101111, (byte) 0b11010111, (byte) 0b11010111
        };
        System.out.println(ByteUtil.readLong(bytes, 4, 40, 10));
    }
    
    @Test
    public void varIntLen() {
        int value1 = 0b00010000_00111111;
        int result1 = ByteUtil.varIntLen(value1);
        System.out.println("result1: " + result1);
        
        long value2 = 0b11111111_11111111L;
        int result2 = ByteUtil.varIntLen(value2);
        System.out.println("result2: " + result2);
    }
    
    @Test
    public void enVarInt() {
        int value1 = 0b00000000011000000000111111011111;
        byte[] result1 = ByteUtil.enVarInt(value1);
        for (byte b : result1) {
            System.out.print(ByteUtil.toBinaryString(b) + ' ');
        }
        System.out.println();
        
        long value2 = 0b0111111111111111111111111000000000000000011111111111111111111111L;
        byte[] result2 = ByteUtil.enVarInt(value2);
        for (byte b : result2) {
            System.out.print(ByteUtil.toBinaryString(b) + ' ');
        }
        System.out.println();
    }
    
    @Test
    public void deVarInt() {
        byte[] bytes1 = new byte[]{(byte) 0b11011111, (byte) 0b10011111, (byte) 0b10000000, (byte) 0b00000011};
        byte[] bytes2 = new byte[]{(byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b10000011, (byte) 0b10000000, (byte) 0b11110000, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b01111111};
        
        IntPair result1 = ByteUtil.deVarInt(bytes1, 0);
        System.out.println(result1.getLeft() + " " + ByteUtil.toBinaryString(result1.getRight()) + " " + ByteUtil.toLowerHexString(result1.getRight()));
        
        IntLPair result2 = ByteUtil.deVarLong(bytes2, 0);
        System.out.println(result2.getInt() + " " + ByteUtil.toBinaryString(result2.getLong()) + " " + ByteUtil.toLowerHexString(result2.getLong()));
    }
    
    @Test
    public void enZigZag() {
        int value1 = 0b10000000_00000000_00000000_00000110;
        long value2 = 0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000110L;
        
        int result1 = ByteUtil.enZigZag(value1);
        long result2 = ByteUtil.enZigZag(value2);
        
        System.out.println(ByteUtil.toBinaryString(result1));
        System.out.println(ByteUtil.toBinaryString(result2));
    }
    
    @Test
    public void deZigZag() {
        int value1 = 0b11111111_11111111_11111111_11110011;
        long value2 = 0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11110011L;
        
        int result1 = ByteUtil.deZigZag(value1);
        long result2 = ByteUtil.deZigZag(value2);
        
        System.out.println(ByteUtil.toBinaryString(result1));
        System.out.println(ByteUtil.toBinaryString(result2));
    }
    
    @Test
    public void toBase64String() {
        byte[] bytes = ByteUtil.parseHexBytes("400280003e06746eec5103422030bbe4157589d7da5180a553754aed7a328c08e9969bff63ad2bbb181c108649b1151f705d15966f227745549aeb0abfa2");
        System.out.println("byteLen=" + bytes.length);
        
        String base64Str1 = Base64.getEncoder().encodeToString(bytes);
        System.out.println("base64Str1: " + base64Str1);
        
        String base64Str2 = ByteUtil.toBase64String(bytes);
        System.out.println("base64Str2: " + base64Str2);
        System.out.println("base64Str1 == base64Str2: " + (base64Str1.equals(base64Str2)));
    }
    
    @Test
    public void parseBase64Bytes() {
        byte[] bytes = ByteUtil.parseHexBytes("400280003e06746eec5103422030bbe4157589d7da5180a553754aed7a328c08e9969bff63ad2bbb181c108649b1151f705d15966f227745549aeb0abfa2");
        System.out.println("byteLen=" + bytes.length);
        String base64String = Base64.getEncoder().encodeToString(bytes);
        System.out.println("base64Len=" + base64String.length());
        
        String hexString1 = ByteUtil.toLowerHexString(Base64.getDecoder().decode(base64String));
        System.out.println("hexString1=" + hexString1);
        
        String hexString2 = ByteUtil.toLowerHexString(ByteUtil.parseBase64Bytes(base64String));
        System.out.println("hexString2=" + hexString2);
        System.out.println("hexString1 == hexString2: " + (hexString1.equals(hexString2)));
    }
    
    
}