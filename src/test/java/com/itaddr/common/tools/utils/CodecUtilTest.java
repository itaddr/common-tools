package com.itaddr.common.tools.utils;

import com.itaddr.common.tools.enums.AES128Enum;
import com.itaddr.common.tools.enums.KeysEnum;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * 字节流编解码相关工具
 *
 * @Author 马嘉祺
 * @Date 2019/5/16 0016 09 21
 * @Description
 */
public final class CodecUtilTest {

    @Test
    public void secureRandom() {
        SecureRandom random = new SecureRandom();
        System.out.println(random.getAlgorithm());
        System.out.println(random.getProvider());
    }

    @Test
    public void randomBytes() {
        byte[] randomBytes = CodecUtil.randomBytes(16);
        System.out.println(Base64.getEncoder().encodeToString(randomBytes));
    }

    @Test
    public void genKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        SecretKey key = CodecUtil.genKey(KeysEnum.AES128);
        assert key != null;

        byte[] keyBytes = key.getEncoded();
        System.out.println(key.getAlgorithm());
        System.out.println(key.getFormat());
        System.out.println(key.isDestroyed());
        System.out.println(Base64.getEncoder().encodeToString(keyBytes));

        // 这里可以随意将key转成任何类型的，但是转成的不对应的key就不能用来加解密了
        SecretKeySpec aes128BeySpec = new SecretKeySpec(keyBytes, KeysEnum.AES128.getName());
        System.out.println(aes128BeySpec);
        SecretKeySpec hmacMD5keySpec = new SecretKeySpec(keyBytes, KeysEnum.HmacMD5.getName());
        System.out.println(hmacMD5keySpec);
    }

    @Test
    public void genKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        KeyPair keyPair = CodecUtil.genKeyPair(KeysEnum.RSA2048);
        assert keyPair != null;

        PrivateKey privateKey = keyPair.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        System.out.println(privateKey.getAlgorithm());
        System.out.println(privateKey.getFormat());
        System.out.println(Base64.getEncoder().encodeToString(privateKeyBytes));

        PublicKey publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        System.out.println(publicKey.getAlgorithm());
        System.out.println(publicKey.getFormat());
        System.out.println(Base64.getEncoder().encodeToString(publicKeyBytes));

        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        System.out.println(priKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        System.out.println(pubKey);
    }

    @Test
    public void getRsaPublicKey() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPair keyPair = CodecUtil.genKeyPair(KeysEnum.RSA2048);
        assert keyPair != null;

        PrivateKey privateKey = keyPair.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        System.out.println(Base64.getEncoder().encodeToString(privateKeyBytes));

        PublicKey publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        System.out.println(Base64.getEncoder().encodeToString(publicKeyBytes));

        System.out.println();
        byte[] extractRsaPublicKey = CodecUtil.getRsaPublicKey(privateKeyBytes).getEncoded();
        System.out.println(Base64.getEncoder().encodeToString(extractRsaPublicKey));
    }

    @Test
    public void md5() throws NoSuchAlgorithmException {
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.md5(sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void sha1() throws NoSuchAlgorithmException {
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.sha1(sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void sha256() throws NoSuchAlgorithmException {
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.sha256(sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void sha384() throws NoSuchAlgorithmException {
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.sha384(sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void sha512() throws NoSuchAlgorithmException {
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.sha512(sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void crc32() {
        byte[] sources = CodecUtil.randomBytes(32);
        int crc32 = CodecUtil.crc32(sources);
        System.out.println(crc32);
    }

    @Test
    public void hmacMD5() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        byte[] keys = Objects.requireNonNull(CodecUtil.genKey(KeysEnum.HmacMD5)).getEncoded();
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.hmacMD5(keys, sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void hmacSHA1() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        byte[] keys = Objects.requireNonNull(CodecUtil.genKey(KeysEnum.HmacSHA1)).getEncoded();
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.hmacSHA1(keys, sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void hmacSHA256() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        byte[] keys = Objects.requireNonNull(CodecUtil.genKey(KeysEnum.HmacSHA256)).getEncoded();
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.hmacSHA256(keys, sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void hmacSHA384() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        byte[] keys = Objects.requireNonNull(CodecUtil.genKey(KeysEnum.HmacSHA384)).getEncoded();
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.hmacSHA384(keys, sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void hmacSHA512() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        byte[] keys = Objects.requireNonNull(CodecUtil.genKey(KeysEnum.HmacSHA512)).getEncoded();
        byte[] sources = CodecUtil.randomBytes(32);
        byte[] bytes = CodecUtil.hmacSHA512(keys, sources);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void murmur2() {
        String source = "舞狮hfdsfd";
        System.out.println(CodecUtil.murmur2(source.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void aes128() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        byte[] keys = Objects.requireNonNull(CodecUtil.genKey(KeysEnum.AES128)).getEncoded();

        byte[] sources = CodecUtil.randomBytes(128);
        System.out.println("sources : " + Base64.getEncoder().encodeToString(sources));

        byte[] enaes128 = CodecUtil.enaes128(AES128Enum.ECB_PKCS5_PADDING, keys, sources);
        System.out.println("enaes128: " + Base64.getEncoder().encodeToString(enaes128));

        byte[] deaes128 = CodecUtil.deaes128(AES128Enum.ECB_PKCS5_PADDING, keys, enaes128);
        System.out.println("deaes128: " + Base64.getEncoder().encodeToString(deaes128));
    }

    @Test
    public void codecrsa() throws NoSuchProviderException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        KeyPair keyPair = CodecUtil.genKeyPair(KeysEnum.RSA2048);
        assert keyPair != null;
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        byte[] sources = CodecUtil.randomBytes(128);
        System.out.println("sources : " + Base64.getEncoder().encodeToString(sources));

        byte[] prienrsa = CodecUtil.prienrsa(privateKey.getEncoded(), sources);
        System.out.println("prienrsa: " + Base64.getEncoder().encodeToString(prienrsa));
        byte[] pubdersa = CodecUtil.pubdersa(publicKey.getEncoded(), prienrsa);
        System.out.println("pubdersa: " + Base64.getEncoder().encodeToString(pubdersa));

        byte[] pubenrsa = CodecUtil.pubenrsa(publicKey.getEncoded(), sources);
        System.out.println("pubenrsa: " + Base64.getEncoder().encodeToString(pubenrsa));
        byte[] pridersa = CodecUtil.pridersa(privateKey.getEncoded(), pubenrsa);
        System.out.println("pridersa: " + Base64.getEncoder().encodeToString(pridersa));

    }

}