package com.itaddr.common.tools.utils;

/**
 * @Author 马嘉祺
 * @Date 2020/7/9 0009 15 57
 * @Description <p></p>
 */
public class HexUtil {
    
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
    
    public static String toBinString(byte... value) {
        return null;
    }
    
    public static String toBinString(byte[] value, char delimiter) {
        return null;
    }
    
    public static String toBinString(short value) {
        return null;
    }
    
    public static String toBinString(short value, char delimiter) {
        return null;
    }
    
    public static String toBinString(int value) {
        return null;
    }
    
    public static String toBinString(int value, char delimiter) {
        return null;
    }
    
    public static String toBinString(long value) {
        return null;
    }
    
    public static String toBinString(long value, char delimiter) {
        return null;
    }
    
    public static byte[] parseBinBytes(String binString) {
        return null;
    }
    
    public static short parseBinShort(String binString) {
        return 0;
    }
    
    public static int parseBinInt(String binString) {
        return 0;
    }
    
    public static long parseBinLong(String binString) {
        return 0;
    }
    
    public static String toLHexString(byte... value) {
        return null;
    }
    
    public static String toUHexString(byte... value) {
        return null;
    }
    
    public static String toLHexString(short value) {
        return null;
    }
    
    public static String toUHexString(short value) {
        return null;
    }
    
    public static String toLHexString(int value) {
        return null;
    }
    
    public static String toUHexString(int value) {
        return null;
    }
    
    public static String toLHexString(long value) {
        return null;
    }
    
    public static String toUHexString(long value) {
        return null;
    }
    
    public static byte[] parseHexBytes(String hexString) {
        return null;
    }
    
    public static short parseHexShort(String hexString) {
        return 0;
    }
    
    public static int parseHexInt(String hexString) {
        return 0;
    }
    
    public static long parseHexLong(String hexString) {
        return 0;
    }
    
    public static String toBase64String(byte... value) {
        return null;
    }
    
    public static byte[] parseBase64Bytes(String base64String) {
        return null;
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
