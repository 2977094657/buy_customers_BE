package com.buy_customers.common.utils;

import com.buy_customers.common.config.api.ResultData;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;


@Component
public class MailUtil {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JavaMailSender emailSender;
    @Resource
    private SpringTemplateEngine templateEngine;

    public ResultData<String> sendEmailWithVerificationCode(String to) {

        // 去除邮箱地址前后的空格
        to = to.trim();

        // 验证邮箱地址是否合法
        String regex = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        if (!to.matches(regex)) {
            return ResultData.fail(400,"输入的邮箱地址不合法");
        }

        //            生成验证码
        int code = TXSendSms.test();
        Context context = new Context();    // 创建一个Context对象用于设置模板变量
        context.setVariable("verificationCode", code);    // 将验证码设置到context中

        String htmlContent = templateEngine.process("verification-code", context);    /* 使用模板引擎处理指定模板（"verification-code"），并将context中保存的数据渲染到模板中，最后生成HTML内容 */

        MimeMessage message = emailSender.createMimeMessage();    /* 调用emailSender（JavaMailSender）创建一个新的空白MimeMessage，MimeMessage表示一封电子邮件 */

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);    /* 创建一个MimeMessageHelper对象来帮助构建MimeMessage，第二个参数true表示这是一个multipart消息 */
            helper.setFrom("3434549571@qq.com");  // 这里的发件人地址必须与 spring.mail.username 的值相同
            helper.setTo(to);    /* 设置收信人邮箱地址 */
            helper.setSubject("欢迎使用百客");     /* 设置邮件主题 */
            helper.setText(htmlContent, true);     /* 设置邮件正文内容和是否为HTML格式,这里传递了true参数表示该邮件正文为HTML格式 */

            emailSender.send(message);     /* 调用emailSender（JavaMailSender）的send方法发送邮件 */
            stringRedisTemplate.opsForValue().set(to, String.valueOf(code),5, TimeUnit.MINUTES);
            return ResultData.success("邮箱验证码发送成功，请注意查看");
        } catch (MessagingException e) {
            // 处理异常...
        }
        return ResultData.fail(400,"邮箱验证码发送失败");
    }
}
