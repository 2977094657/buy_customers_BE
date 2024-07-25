package com.buy_customers.common.config.DataEncryptionAndDecryption;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.buy_customers.common.config.DecryptHttpServletRequestWrapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class EncryptionFilter implements Filter {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ServletRequest processedRequest = requestWrapper;
        String requestURI = requestWrapper.getRequestURI();
        // 检查请求头中的 'X-Needs-Decryption' 字段
        String needsDecryption = requestWrapper.getHeader("X-Needs-Decryption");
        if ("true".equals(needsDecryption)) {
            // 读取请求体
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = requestWrapper.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String requestBody = stringBuilder.toString();
            // 使用 fastjson 解析 JSON 请求体
            JSONObject jsonObject = JSON.parseObject(requestBody);
            String rsaPublicKey = jsonObject.getString("rsaPublicKey");
            String encryptedAESKey = jsonObject.getString("aesKey");
            // 从Redis中获取对应的RSA私钥
            String privateKeyString = stringRedisTemplate.opsForValue().get(rsaPublicKey);
            PrivateKey privateKey;
            try {
                privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString)));
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            // 使用RSA私钥对加密的AES密钥进行解密
            Cipher cipher;
            try {
                cipher = Cipher.getInstance("RSA");
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }
            try {
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            byte[] aesKeyBytes = Base64.getDecoder().decode(encryptedAESKey);
            byte[] decryptedAESKeyBytes;
            try {
                decryptedAESKeyBytes = cipher.doFinal(aesKeyBytes);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
            String aesKey = new String(decryptedAESKeyBytes, StandardCharsets.UTF_8);
            // 对每个参数进行解密
            JSONObject decryptedBodyJson = new JSONObject();
            for (String key : jsonObject.keySet()) {
                if (!"rsaPublicKey".equals(key) && !"aesKey".equals(key)) {
                    Object param = jsonObject.get(key);
                    if (param instanceof JSONObject encryptedParameter) {
                        String ciphertext = encryptedParameter.getString("ciphertext");
                        String ivParameter = encryptedParameter.getString("iv");
                        String decryptedParameter;
                        try {
                            decryptedParameter = decryptData(ciphertext, aesKey, ivParameter);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        decryptedBodyJson.put(key, decryptedParameter);
                        request.setAttribute(key, decryptedParameter);
                    }
                }
            }
            String decryptedBody = decryptedBodyJson.toJSONString();
            // 创建一个包含解密后请求体的 DecryptHttpServletRequestWrapper 实例
            processedRequest = new DecryptHttpServletRequestWrapper(requestWrapper, decryptedBody);
        }
        chain.doFilter(processedRequest, response);
    }



    private String decryptData(String encryptedData, String aesKey, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.getDecoder().decode(aesKey), "AES"), ivParameterSpec);
        byte[] bytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(bytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}


