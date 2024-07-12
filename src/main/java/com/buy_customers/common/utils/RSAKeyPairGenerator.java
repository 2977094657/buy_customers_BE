package com.buy_customers.common.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Set;

@Component
@DependsOn
public class RSAKeyPairGenerator {
    private PublicKey publicKey; // RSA公钥
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        // 获取所有以特定前缀开始的键
        Set<String> keys = stringRedisTemplate.keys("MIIBIjANBgkqhkiG9*");

        // 如果有，则直接返回，不再生成新密钥对
        if (keys != null && !keys.isEmpty()) {
            return;
        }
        // 获取RSA密钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥长度为2048位
        keyPairGenerator.initialize(2048);
        // 生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 获取并保存私钥
        PrivateKey privateKey = keyPair.getPrivate();
        // 获取并保存公钥
        this.publicKey = keyPair.getPublic();

        // 将公钥和私钥转换为字符串
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());


        // 将公钥和私钥存储在Redis中
        stringRedisTemplate.opsForValue().set(publicKeyString, privateKeyString);
    }


    /**
     * 获取RSA公钥
     *
     * @return RSA公钥
     */
    public String getPublicKey() {
        if (publicKey == null) {
            throw new IllegalStateException("Public key has not been initialized yet.");
        }
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 使用AES加密数据
     *
     * @param data 需要加密的数据
     * @return 加密后的数据和AES密钥
     */
    public EncryptedData encryptData(String data) throws Exception {
        // 获取或生成AES密钥
        SecretKey aesKey = RequestContextHolder.getAesKey();
        if (aesKey == null) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            aesKey = keyGenerator.generateKey();
            RequestContextHolder.setAesKey(aesKey);
        }

        // 获取或生成IV
        byte[] iv = RequestContextHolder.getIv();
        if (iv == null) {
            iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            RequestContextHolder.setIv(iv);
        }

        // 创建Cipher实例进行AES加密
        Cipher aesCipher = Cipher.getInstance("AES/CTR/NoPadding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParameterSpec);
        byte[] encryptedData = aesCipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // 将AES密钥和IV转换为Base64编码的字符串
        String aesKeyString = Base64.getEncoder().encodeToString(aesKey.getEncoded());
        String ivString = Base64.getEncoder().encodeToString(iv);

        // 将加密后的数据、AES密钥和IV转换为Base64编码的字符串
        String encryptedDataString = Base64.getEncoder().encodeToString(encryptedData);
        return new EncryptedData(encryptedDataString, aesKeyString, ivString);
    }




    @Getter
    @Setter
    public static class EncryptedData {
        private String data;
        private String aesKey;
        private String iv;

        public EncryptedData(String data, String aesKey, String iv) {
            this.data = data;
            this.aesKey = aesKey;
            this.iv = iv;
        }
    }

    @Getter
    @Setter
    public static class RequestContextHolder {
        private static final ThreadLocal<SecretKey> aesKey = new ThreadLocal<>();
        private static final ThreadLocal<byte[]> iv = new ThreadLocal<>();

        public static SecretKey getAesKey() {
            return aesKey.get();
        }

        public static void setAesKey(SecretKey key) {
            aesKey.set(key);
        }

        public static byte[] getIv() {
            return iv.get();
        }

        public static void setIv(byte[] iv) {
            RequestContextHolder.iv.set(iv);
        }
    }

}

