package com.example.explor_gastro;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

class Sample {
    public static final String API_KEY = "4lHXDXoZ0ogXstLM8NR0ww0y";
    public static final String SECRET_KEY = "WCFBKmF6EWmBhqNoGHV3DGzW1CR2YEKA";

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static void main(String []args) throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"text\":\"红酒\",\"resolution\":\"1024*1024\",\"style\":\"写实风格\"}");
        Request request = new Request.Builder()
            .url("https://aip.baidubce.com/rpc/2.0/ernievilg/v1/txt2img?access_token=" + getAccessToken())
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        System.out.println(response.body().string());

    }
    
    
    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    static String getAccessToken() throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }
    
}