package com.buy_customers.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Response;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class PromptImg {
    public static final String API_KEY = "4lHXDXoZ0ogXstLM8NR0ww0y";
    public static final String SECRET_KEY = "WCFBKmF6EWmBhqNoGHV3DGzW1CR2YEKA";

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static String promptImg(String prompt) throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = String.format("{\"text\":\"%s\",\"resolution\":\"1024*1024\",\"style\":\"写实风格\"}", prompt);
        RequestBody body = RequestBody.create(mediaType, requestBody);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ernievilg/v1/txt2img?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        okhttp3.Response response = HTTP_CLIENT.newCall(request).execute();
        if (response.body() != null) {
            return response.body().string();
        }
        return "请求失败";
    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    @Nullable
    static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        if (response.body() != null) {
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            return jsonObject.getString("access_token");
        }
        return null;
    }


    public static String getImg(String taskId) throws IOException{
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, String.format("{\"taskId\":\"%s\"}", taskId));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ernievilg/v1/getImg?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return response.body().string();
    }
}
