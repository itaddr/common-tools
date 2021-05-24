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

import com.itaddr.common.tools.constants.IntegerValue;
import com.itaddr.common.tools.enums.AESPadEnum;
import com.itaddr.common.tools.enums.KeysEnum;
import com.itaddr.common.tools.enums.RSAPadEnum;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.CRC32;

/**
 * 字节流编解码相关工具
 *
 * @Author 马嘉祺
 * @Date 2019/5/16 0016 09 21
 * @Description
 */
public final class CodecUtil {

    public static final BigInteger RSA_PUBLIC_EXPONENT = BigInteger.valueOf(65537);

    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String SECURE_RANDOM_PROVIDER = "SUN";

    private CodecUtil() {
    }

    /**
     * 生成指定长度的随机字节数据
     *
     * @param length
     * @return
     */
    public static byte[] randomBytes(int length) {
        final byte[] randomBytes = new byte[length];
        ThreadLocalRandom.current().nextBytes(randomBytes);
        return randomBytes;
    }

    /**
     * 生成单个密钥
     *
     * @param mode
     * @return
     */
    public static SecretKey genKey(KeysEnum mode) {
        if (null == mode) {
            throw new IllegalArgumentException("mode can not be empty");
        }
        if (!mode.isHaveKey() || !mode.isSymmetric()) {
            return null;
        }
        // 安全性比Random高的随机数生成器
        // 参数一：指定算算法，主要为NativePRNG、SHA1PRNG，默认SHA1PRNG性能占优，NativePRNG安全占优
        // 参数二：指定算法程序包，默认为SUN
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM, SECURE_RANDOM_PROVIDER);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance(mode.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        generator.init(mode.getKeyBits(), secureRandom);
        return generator.generateKey();
    }

    /**
     * 生成密钥对
     *
     * @param mode
     * @return
     */
    public static KeyPair genKeyPair(KeysEnum mode) {
        if (null == mode) {
            throw new IllegalArgumentException("mode can not be empty");
        }
        if (!mode.isHaveKey() || mode.isSymmetric()) {
            return null;
        }
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM, SECURE_RANDOM_PROVIDER);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance(mode.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        generator.initialize(mode.getKeyBits(), secureRandom);
        return generator.generateKeyPair();
    }

    /**
     * 通过RSA私钥计算出公钥
     *
     * @param privateKey
     * @return
     * @throws InvalidKeySpecException
     */
    public static RSAPublicKey getRsaPublicKey(byte[] privateKey) throws InvalidKeySpecException {
        KeyFactory factory;
        try {
            factory = KeyFactory.getInstance(KeysEnum.RSA2048.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), RSA_PUBLIC_EXPONENT);
        return (RSAPublicKey) factory.generatePublic(publicKeySpec);
    }

    /**
     * MD5摘要算法
     *
     * @param message
     * @return
     */
    public static byte[] md5(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(KeysEnum.MD5.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(message);
        return digest.digest();
    }

    /**
     * SHA-1摘要算法
     *
     * @param message
     * @return
     */
    public static byte[] sha1(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(KeysEnum.SHA1.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(message);
        return digest.digest();
    }

    /**
     * SHA-256摘要算法
     *
     * @param message
     * @return
     */
    public static byte[] sha256(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(KeysEnum.SHA256.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(message);
        return digest.digest();
    }

    /**
     * SHA-384摘要算法
     *
     * @param message
     * @return
     */
    public static byte[] sha384(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(KeysEnum.SHA384.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(message);
        return digest.digest();
    }

    /**
     * SHA-512摘要算法
     *
     * @param message
     * @return
     */
    public static byte[] sha512(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(KeysEnum.SHA512.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(message);
        return digest.digest();
    }

    /**
     * CRC32摘要算法
     *
     * @param message
     * @return
     */
    public static int crc32(byte[] message) {
        CRC32 crc32 = new CRC32();
        crc32.update(message);
        return (int) crc32.getValue();
    }

    private static Mac makeMac(SecretKey secretKey) {
        Mac mac;
        try {
            mac = Mac.getInstance(secretKey.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            mac.init(secretKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return mac;
    }

    /**
     * HmacMD5摘要算法
     *
     * @param keys
     * @param message
     * @return
     */
    public static byte[] hmacMD5(byte[] keys, byte[] message) {
        SecretKey secretKey = new SecretKeySpec(keys, KeysEnum.HmacMD5.getName());
        return makeMac(secretKey).doFinal(message);
    }

    /**
     * HmacSHA1摘要算法
     *
     * @param keys
     * @param message
     * @return
     */
    public static byte[] hmacSHA1(byte[] keys, byte[] message) {
        SecretKey secretKey = new SecretKeySpec(keys, KeysEnum.HmacSHA1.getName());
        return makeMac(secretKey).doFinal(message);
    }

    /**
     * HmacSHA256散列
     *
     * @param keys
     * @param message
     * @return
     */
    public static byte[] hmacSHA256(byte[] keys, byte[] message) {
        SecretKey secretKey = new SecretKeySpec(keys, KeysEnum.HmacSHA256.getName());
        return makeMac(secretKey).doFinal(message);
    }

    /**
     * HmacSHA384散列
     *
     * @param keys
     * @param message
     * @return
     */
    public static byte[] hmacSHA384(byte[] keys, byte[] message) {
        SecretKey secretKey = new SecretKeySpec(keys, KeysEnum.HmacSHA384.getName());
        return makeMac(secretKey).doFinal(message);
    }

    /**
     * HmacSHA512散列
     *
     * @param keys
     * @param message
     * @return
     */
    public static byte[] hmacSHA512(byte[] keys, byte[] message) {
        SecretKey secretKey = new SecretKeySpec(keys, KeysEnum.HmacSHA512.getName());
        return makeMac(secretKey).doFinal(message);
    }


    /**
     * 从字节数组生成32位murmur2哈希
     *
     * @param message 要散列的字节数组
     * @return 给定数组的32位散列
     */
    public static int murmur2(byte[] message) {
        int length = message.length;
        int seed = 0x9747b28c;
        // 'm'和'r'是离线生成的混合常量。
        // They're not really 'magic', they just happen to work well.
        // 它们并不是真正的“magic”，它们恰好运作良好。
        final int m = 0x5bd1e995;
        final int r = 24;

        // 将哈希初始化为随机值
        int h = seed ^ length;
        int length4 = length / 4;

        for (int i = 0; i < length4; i++) {
            final int i4 = i * 4;
            int k = (message[i4] & 0xff) + ((message[i4 + 1] & 0xff) << 8) + ((message[i4 + 2] & 0xff) << 16) + ((message[i4 + 3] & 0xff) << 24);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        // 处理输入数组的最后几个字节
        final int val = length % IntegerValue.FOUR;
        if (IntegerValue.THREE == val) {
            h ^= (message[(length & ~3) + 2] & 0xff) << 16;
        } else if (IntegerValue.TWO == val) {
            h ^= (message[(length & ~3) + 1] & 0xff) << 8;
        } else if (IntegerValue.ONE == val) {
            h ^= message[length & ~3] & 0xff;
            h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }


    /**
     * AES128算法
     *
     * @param pad     AES加解密模式和填充模式
     * @param mode    编解码模式（加密或者解密）
     * @param keys    加密密码字节数组
     * @param message 明文字节数组，待加密的字节数组
     * @return 返回加密后的密文字节数组，加密错误返回null
     * @throws InvalidKeyException
     * @throws BadPaddingException
     */
    private static byte[] aes128(int mode, AESPadEnum pad, byte[] keys, byte[] message) throws InvalidKeyException, BadPaddingException {
        // 1 获取加密密钥
        SecretKeySpec keySpec = new SecretKeySpec(keys, KeysEnum.AES128.getName());
        // 2 获取Cipher实例
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(pad.getName());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        // 查看数据块位数 默认为16(byte) * 8 = 128bit
        // 3 初始化Cipher实例。设置执行模式以及加密密钥
        cipher.init(mode, keySpec);
        try {
            // 4 执行
            return cipher.doFinal(message);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES128解密算法
     *
     * @param pad     AES加解密和填充模式模式
     * @param keys    加密密码字节数组
     * @param message 明文字节数组，待加密的字节数组
     * @return 返回加密后的密文字节数组，加密错误返回null
     * @throws InvalidKeyException
     * @throws BadPaddingException
     */
    public static byte[] enaes128(AESPadEnum pad, byte[] keys, byte[] message) throws InvalidKeyException, BadPaddingException {
        return aes128(Cipher.ENCRYPT_MODE, pad, keys, message);
    }

    /**
     * AES128解密算法
     *
     * @param pad     AES加解密和填充模式模式
     * @param keys    加密密码字节数组
     * @param message 明文字节数组，待加密的字节数组
     * @return 返回加密后的密文字节数组，加密错误返回null
     * @throws InvalidKeyException
     * @throws BadPaddingException
     */
    public static byte[] deaes128(AESPadEnum pad, byte[] keys, byte[] message) throws InvalidKeyException, BadPaddingException {
        return aes128(Cipher.DECRYPT_MODE, pad, keys, message);
    }

    /**
     * AES128解密算法
     *
     * @param keys    加密密码字节数组
     * @param message 明文字节数组，待加密的字节数组
     * @return 返回加密后的密文字节数组，加密错误返回null
     * @throws InvalidKeyException
     * @throws BadPaddingException
     */
    public static byte[] enaes128(byte[] keys, byte[] message) throws InvalidKeyException, BadPaddingException {
        return aes128(Cipher.ENCRYPT_MODE, AESPadEnum.ECB_PKCS5_PADDING, keys, message);
    }

    /**
     * AES128解密算法
     *
     * @param keys    加密密码字节数组
     * @param message 明文字节数组，待加密的字节数组
     * @return 返回加密后的密文字节数组，加密错误返回null
     * @throws InvalidKeyException
     * @throws BadPaddingException
     */
    public static byte[] deaes128(byte[] keys, byte[] message) throws InvalidKeyException, BadPaddingException {
        return aes128(Cipher.DECRYPT_MODE, AESPadEnum.ECB_PKCS5_PADDING, keys, message);
    }

    private static byte[] prirsa(int mode, RSAPadEnum pad, byte[] privateKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyFactory factory;
        try {
            factory = KeyFactory.getInstance(KeysEnum.RSA2048.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        PrivateKey priKey = factory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));

        Cipher cipher;
        try {
            cipher = Cipher.getInstance(pad.getName());
            System.out.println(cipher.getBlockSize());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        cipher.init(mode, priKey);
        return cipher.doFinal(message);
    }

    private static byte[] pubrsa(int mode, RSAPadEnum pad, byte[] publicKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyFactory factory;
        try {
            factory = KeyFactory.getInstance(KeysEnum.RSA2048.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        PublicKey pubKey = factory.generatePublic(new X509EncodedKeySpec(publicKey));

        Cipher cipher;
        try {
            cipher = Cipher.getInstance(pad.getName());
            System.out.println(cipher.getBlockSize());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        cipher.init(mode, pubKey);
        return cipher.doFinal(message);
    }

    /**
     * RSA私钥签名数据
     *
     * @param pad
     * @param privateKey
     * @param message
     * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] prienrsa(RSAPadEnum pad, byte[] privateKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return prirsa(Cipher.ENCRYPT_MODE, pad, privateKey, message);
    }

    /**
     * RSA私钥解密数据
     *
     * @param pad
     * @param privateKey
     * @param message
     * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] pridersa(RSAPadEnum pad, byte[] privateKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return prirsa(Cipher.DECRYPT_MODE, pad, privateKey, message);
    }

    /**
     * RSA公钥加密数据
     *
     * @param pad
     * @param publicKey
     * @param message
     * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] pubenrsa(RSAPadEnum pad, byte[] publicKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return pubrsa(Cipher.ENCRYPT_MODE, pad, publicKey, message);
    }

    /**
     * RSA公钥解密签名
     *
     * @param pad
     * @param publicKey
     * @param message
     * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] pubdersa(RSAPadEnum pad, byte[] publicKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return pubrsa(Cipher.DECRYPT_MODE, pad, publicKey, message);
    }

    /**
     * RSA私钥签名数据
     *
     * @param privateKey
     * @param message
     * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] prienrsa(byte[] privateKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return prirsa(Cipher.ENCRYPT_MODE, RSAPadEnum.RSA_PKCS1_PADDING, privateKey, message);
    }

    /**
     * RSA私钥解密数据
     *
     * @param privateKey
     * @param message
     * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] pridersa(byte[] privateKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return prirsa(Cipher.DECRYPT_MODE, RSAPadEnum.RSA_PKCS1_PADDING, privateKey, message);
    }

    /**
     * RSA公钥加密数据
     *
     * @param publicKey
     * @param message
     * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] pubenrsa(byte[] publicKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return pubrsa(Cipher.ENCRYPT_MODE, RSAPadEnum.RSA_PKCS1_PADDING, publicKey, message);
    }

    /**
     * RSA公钥解密签名
     *
     * @param publicKey
     * @param message
     * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] pubdersa(byte[] publicKey, byte[] message) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return pubrsa(Cipher.DECRYPT_MODE, RSAPadEnum.RSA_PKCS1_PADDING, publicKey, message);
    }

}