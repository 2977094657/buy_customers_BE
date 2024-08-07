package com.buy_customers.common.utils;

import com.buy_customers.common.config.api.ResultData;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class TXSendSms {
    private final StringRedisTemplate stringRedisTemplate;

    public TXSendSms(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成指定长度的随机数
     * @param length 随机数的长度，必须在4和6之间
     * @return 生成的随机数
     */
    public static int generateCode(int length) {
        // 确保长度在4和6之间
        if (length < 4) {
            length = 4;
        } else if (length > 6) {
            length = 6;
        }
        // 计算随机数的最小值和最大值
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        // 在最小值和最大值之间生成一个随机数
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
    public static int test() {
        // 随机生成一个介于4和6之间的整数
        Random rand = new Random();
        int length = rand.nextInt(3) + 4;
        // 调用generateCode方法生成随机数
        return generateCode(length);
    }

    /**
     * 30秒内只能发送2条消息
     *
     */
    public SendSmsResponse sms(String phoneNumber) throws TencentCloudSDKException {
        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential("AKIDRilbDbkfNr7DuNccOxRVWQAqW6Yt69vZ", "rQp281hAPQEsKabNzOFtIXYMxNbfV9c2");
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        SendSmsRequest req = new SendSmsRequest();
        String generateCode = String.valueOf(test());
        String[] templateParamSet1 = {generateCode, "5"};//模板的参数 第一个是验证码，第二个是过期时间
        req.setTemplateParamSet(templateParamSet1);//发送验证码
        String[] phoneNumberSet1 = {phoneNumber};//手机号
        req.setPhoneNumberSet(phoneNumberSet1);
        req.setSmsSdkAppId("1400810808");
        req.setSignName("链式云公众号");
        req.setTemplateId("1762705");
        // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
        SendSmsResponse resp = client.SendSms(req);
        // 将验证码存入redis限时5分钟
        stringRedisTemplate.opsForValue().set(phoneNumber,generateCode,5, TimeUnit.MINUTES);
        return resp;
    }

}
