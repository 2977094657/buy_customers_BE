package com.buy_customers.common.config.DataEncryptionAndDecryption;

import com.buy_customers.common.annotation.EncryptResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;

@ControllerAdvice
public class EncryptionResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Resource
    private RSAKeyPairGenerator rsaKeyPairGenerator;

    @Override
    public boolean supports(MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethodAnnotation(EncryptResponse.class) != null;
    }


    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType, @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        try {
            // 将响应体转换为JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.valueToTree(body);

            // 递归地遍历JsonNode，对每个值进行加密，并获取AES密钥和IV
            EncryptedJson result = encryptJsonValues(jsonNode);
            JsonNode encryptedJsonNode = result.getData();
            String aesKey = result.getAesKey();
            String iv = result.getIv();

            // 将AES密钥和IV添加到加密数据里面
            if (encryptedJsonNode.isObject()) {
                ((ObjectNode) encryptedJsonNode).put("aesKey", aesKey);
                ((ObjectNode) encryptedJsonNode).put("iv", iv);
            } else {
                throw new RuntimeException("Encrypted data is not a JSON object.");
            }

            // 设置响应头
            response.getHeaders().add("X-Encrypted", "true");

            // 返回包含加密数据、AES密钥和IV的JsonNode
            return encryptedJsonNode;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EncryptedJson encryptJsonValues(JsonNode jsonNode) throws Exception {
        String aesKey = null;
        String iv = null;
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                JsonNode fieldValue = field.getValue();
                String valueToEncrypt;
                if (fieldValue.isTextual()) {
                    valueToEncrypt = fieldValue.asText();
                } else if (fieldValue.isNumber()) {
                    valueToEncrypt = fieldValue.toString(); // 将数字转换为字符串
                } else {
                    // 递归地对子对象或数组进行加密
                    encryptJsonValues(fieldValue);
                    continue;
                }
                // 对值进行加密
                RSAKeyPairGenerator.EncryptedData encryptedData = rsaKeyPairGenerator.encryptData(valueToEncrypt);
                String encryptedValue = encryptedData.getData();
                aesKey = encryptedData.getAesKey();
                iv = encryptedData.getIv();
                objectNode.put(field.getKey(), encryptedValue);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode arrayElement = arrayNode.get(i);
                String valueToEncrypt;
                if (arrayElement.isTextual()) {
                    valueToEncrypt = arrayElement.asText();
                } else if (arrayElement.isNumber()) {
                    valueToEncrypt = arrayElement.toString(); // 将数字转换为字符串
                } else {
                    // 递归地对子对象或数组进行加密
                    encryptJsonValues(arrayElement);
                    continue;
                }
                // 对值进行加密
                RSAKeyPairGenerator.EncryptedData encryptedData = rsaKeyPairGenerator.encryptData(valueToEncrypt);
                String encryptedValue = encryptedData.getData();
                arrayNode.set(i, new TextNode(encryptedValue));
            }
        }
        return new EncryptedJson(jsonNode, aesKey, iv);
    }

    @Getter
    @Setter
    public static class EncryptedJson {
        private JsonNode data;
        private String aesKey;
        private String iv;

        public EncryptedJson(JsonNode data, String aesKey, String iv) {
            this.data = data;
            this.aesKey = aesKey;
            this.iv = iv;
        }
    }



}
