package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.UserService;
import com.example.explor_gastro.utils.JwtService;
import com.example.explor_gastro.utils.TXSendSms;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 用户表(User)表控制层
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
@Slf4j
@RestController
@RequestMapping("user")
@Tag(name = "用户管理")
public class UserController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TXSendSms txSendSms;
    @Resource
    private JwtService jwtService;

    /**
     * 登录功能
     * @param phone 手机号
     * @param pwd 密码
     * @return 响应内容
     */
    @PostMapping(value = "/loginIn", produces = "application/json")
    @Operation(summary = "用户登录")
    @Parameters({
            @Parameter(name = "phone", description = "手机号"),
            @Parameter(name = "pwd", description = "用户密码"),
    })
    public ResponseEntity<?> login(@RequestParam String phone, @RequestParam String pwd) {
        try {
            User user = userService.LoginIn(phone, pwd);
            String token = jwtService.generateToken(user);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    @GetMapping(value = "/user", produces = "application/json")
    @Operation(summary = "解析token")
    @Parameters({
            @Parameter(name = "Authorization", description = "用户token"),
    })
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
        try {
            User user = jwtService.parseToken(token);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 用户注册功能
     * @param user
     * @return
     */
    @PostMapping(value  =  "/register",produces  =  "text/plain;charset=UTF-8")
    @Operation(summary  =  "注册")
    @Parameters({
            @Parameter(name = "phone", description = "手机号",required = true),
            @Parameter(name = "name", description = "用户名",required = true),
            @Parameter(name = "pwd", description = "用户密码",required = true),
            @Parameter(name = "code", description = "验证码",required = true),
    })
    public  String  register(@RequestBody  User  user,  @RequestParam(value = "code",required = true) int  code) throws NoSuchAlgorithmException {
        if  (user.getPhone()  ==  null)  {
            return  "手机号不能为空";
        }  //  手机号正则校验，可自行定义
        if  (!Pattern.matches("^1[3-9]\\d{9}$",  user.getPhone()))  {
            return  "手机号格式不正确";
        }
        boolean  hasKey  = Boolean.TRUE.equals(stringRedisTemplate.hasKey(user.getPhone()));
        if  (!hasKey)  {
            return  "验证码已过期，请重新发送";
        }
        int  redisCode  =  Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(user.getPhone())));
        if  (code  !=  redisCode)  {
            return  "验证码错误";
        }
        boolean  success  =  userService.register(user);
        if  (success)  {
            return  "注册成功";
        }
        else  {  return  "注册失败，用户名或手机号已存在";  }  }

    /**
     * 短信获取
     * @param phoneNumber
     * @return
     * @throws TencentCloudSDKException
     */
    @PostMapping(value  =  "/message",produces  =  "text/plain;charset=UTF-8")
    @Operation(summary  =  "获取短信")
    @Parameters({
            @Parameter(name = "phoneNumber", description = "手机号",required = true),
    })
    public String sms(String phoneNumber) throws TencentCloudSDKException {
        return txSendSms.sms(phoneNumber);
    }
    /**
     * 修改数据
     *
     * @param user 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody User user) {
        return success(this.userService.updateById(user));
    }



    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public R<User> selectUserById(@PathVariable Integer userId) {
        User user = userService.selectUserById(userId);
        return R.ok(user);
    }

    /**
     * 修改用户信息
     *
     * @param userId      用户ID
     * @param name        用户名
     * @param description 用户简介
     * @param address     用户地址
     * @param signupTime  用户注册时间
     * @param phone       用户手机号
     * @return 是否修改成功
     */
    @PutMapping("/{userId}")
    public boolean updateUser(@PathVariable Integer userId,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam String address,
                              @RequestParam Date signupTime,
                              @RequestParam String phone) {
        boolean result = userService.updateUser(userId, name, description, address, signupTime, phone);
        return result;
    }




    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.userService.removeByIds(idList));
    }
}

