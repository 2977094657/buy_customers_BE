package com.buy_customers.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class ErnieBotTurbo {
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static JSON vendorPrompt(String prompt) throws IOException {
        //1、获取token
        String access_token = new ErnieBotTurbo().getWenxinToken();
        //2、访问数据
        String requestMethod = "POST";
        URLEncoder.encode("junshi", "UTF-8");
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant?access_token="+access_token;//post请求时格式
        //测试：访问聚合数据的地区新闻api
        HashMap<String, String> msg = new HashMap<>();
        msg.put("role","user");
        msg.put("content", "你是一个拥有深度电商经验和专业知识的人员，现在我需要你为我提供5个"+prompt+"的淘宝同款商品标题，标题关键词应该尽可能多的展示此产品，你只需要回复生成的标题，不需要多余回复，标题应该包含以下几点：产品名称、关键属性、产品亮点/卖点、品牌名、调性词，请记住生成的每个标题必须要20字符上下，这点很重要，也应该吸引人，同时也应遵守淘宝的相关规则和指导原则。");
        ArrayList<HashMap> messages = new ArrayList<>();
        messages.add(msg);
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        String outputStr = JSON.toJSONString(requestBody);
        return HttpRequest.httpRequest(url,requestMethod,outputStr,"application/json");
    }

    public String getWenxinToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=EAVVEeofpxr80tlCBhqwWdSl&client_secret=oGcmkyI2vtVONWaSRxUfU969YqzdYvhz&grant_type=client_credentials") //按官网要求填写你申请的key和相关秘钥
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String s = null;
        if (response.body() != null) {
            s = response.body().string();
        }
        JSONObject objects = JSONArray.parseObject(s);
        String msg = objects.getString("access_token");
        System.out.println(msg);
        return msg;
    }

}
