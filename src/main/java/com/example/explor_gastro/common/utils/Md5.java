package com.example.explor_gastro.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
    public static String MD5Encryption(String md5) throws NoSuchAlgorithmException {
        // 获取MD5加密实例
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 更新摘要
        md.update(md5.getBytes());
        // 计算摘要
        byte[] digest = md.digest();
        // 创建字符串缓冲区
        StringBuilder sb = new StringBuilder();
        // 将字节数组转换为十六进制字符串
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        System.out.println(sb);
        return sb.toString();
    }
}
