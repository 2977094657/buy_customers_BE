package com.buy_customers.common.utils;


import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpRequest {
    /**
     * post/GET远程请求接口并得到返回结果
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方法post/GET
     */
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr, String contentType) {
        JSONObject jsonObject = null;
        StringBuilder buffer = new StringBuilder();
        try {

            HttpURLConnection httpUrlConn = getHttpURLConnection(requestUrl, requestMethod, contentType);
            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes(StandardCharsets.UTF_8));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            httpUrlConn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (Exception ignored) {
        }
        return jsonObject;
    }

    @NotNull
    private static HttpURLConnection getHttpURLConnection(String requestUrl, String requestMethod, String contentType) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection httpUrlConn = (HttpURLConnection)url.openConnection();
        httpUrlConn.setDoOutput(true);
        httpUrlConn.setDoInput(true);
        httpUrlConn.setUseCaches(false);
        // 设置请求方式（GET/POST）
        httpUrlConn.setRequestMethod(requestMethod);
        //设置头信息
//            httpUrlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        httpUrlConn.setRequestProperty("Content-Type", contentType);
        httpUrlConn.setRequestProperty("Accept","application/json;charset=UTF-8");
        // 设置连接主机服务器的超时时间：15000毫秒
        httpUrlConn.setConnectTimeout(15000);
        // 设置读取远程返回的数据时间：60000毫秒
        httpUrlConn.setReadTimeout(60000);
        if ("GET".equalsIgnoreCase(requestMethod)){
            httpUrlConn.connect();
        }
        return httpUrlConn;
    }

}
