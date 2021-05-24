package com.itaddr.common.tools.enums;

/**
 * RSA算法加解密模式和填充模式
 * 加密算法/加密模式/填充类型
 * <p>
 * 跟DES，AES一样，　RSA也是一个块加密算法（ block cipher algorithm），总是在一个固定长度的块上进行操作。但跟AES等不同的是，　block length是跟key length有关的。每次RSA加密的明文的长度是受RSA填充模式限制的，但是RSA每次加密的块长度就是key length
 *
 * @Author 马嘉祺
 * @Date 2019/5/16 0016 09 35
 * @Description
 */
public enum RSAPadEnum {

    /**
     * 不填充
     * 输入：可以和RSA钥模长一样长，如果输入的明文过长，必须切割，　然后填充
     * 输出：和modulus一样长
     * <p>
     * 当你在客户端选择RSA_NO_PADDING填充模式时，如果你的明文不够128字节加密的时候会在你的明文前面，前向的填充零。解密后的明文也会包括前面填充的零，这是服务器需要注意把解密后的字段前向填充的零去掉，才是真正之前加密的明文
     */
    RSA_NO_PADDING("RSA/ECB/NoPadding"),

    /**
     * 默认方式
     * 输入：必须 比 RSA 钥模长(modulus) 短至少11个字节, 也就是　RSA_size(rsa) – 11。如果输入的明文过长，必须切割，　然后填充
     * 输出：和modulus一样长
     * <p>
     * 当你选择RSA_PKCS1_PADDING填充模式时，如果你的明文不够128字节加密的时候会在你的明文中随机填充一些数据，所以会导致对同样的明文每次加密后的结果都不一样。对加密后的密文，服务器使用相同的填充方式都能解密。解密后的明文也就是之前加密的明文
     */
    RSA_PKCS1_PADDING("RSA/ECB/PKCS1Padding"),

    /**
     * 输入：RSA_size(rsa) – 41
     * 输出：和modulus一样长
     * <p>
     * RSA_PKCS1_OAEP_PADDING是PKCS#1推出的新的填充方式，安全性是最高的，和前面RSA_PKCS1_PADDING的区别就是加密前的编码方式不一样
     */
    RSA_PKCS1_OAEP_PADDING("RSA/ECB/OAEPPadding");

    private final String name;

    RSAPadEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RSAPadEnum parse(final String name) {
        for (RSAPadEnum aes : RSAPadEnum.values()) {
            if (aes.getName().equals(name)) {
                return aes;
            }
        }
        return null;
    }

}
