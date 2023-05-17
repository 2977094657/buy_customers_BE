package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.dao.VendorDao;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.entity.Vendor;
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
import org.springframework.http.HttpStatus;
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
@CrossOrigin
public class UserController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;

    @Resource
    private UserDao userDao;
    @Resource
    private VendorDao vendorDao;

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
    @PostMapping(value = "/login", produces = "application/json")
    @Operation(summary = "用户登录")
    @Parameters({
            @Parameter(name = "phone", description = "手机号"),
            @Parameter(name = "pwd", description = "用户密码"),
    })
    public ResponseEntity<?> login(@RequestParam(defaultValue = "12345678955") String phone,
                                   @RequestParam(defaultValue = "888") String pwd) {
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
     * 搜索商家功能
     * @param keyword
     * @return
     */
    @GetMapping("/search")
    @Operation(summary = "搜索商家")
    @Parameters({
            @Parameter(name = "keyword",description = "关键词，示例值：江湖菜;瓦香鸡"),
    })
    public List<Vendor> searchVendors(@RequestParam(required = false,defaultValue = "江湖菜") String keyword) {
        return vendorDao.searchVendors(keyword);
    }


    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    //  通过  Get  请求访问URL中的  {userId}，并返回相应的用户信息
    @GetMapping("/{userId}")
    //  自定义接口描述信息
    @Operation(summary = "获取用户")
    //  定义一个通用的返回结果类型，封装操作结果以及数据
    public R<User> selectUserById(@PathVariable Integer userId) {
        //  根据传入的  userId  查询对应的用户信息
        User user = userService.selectUserById(userId);
        //  将查询结果封装到通用返回结果类型中，并返回
        return R.ok(user);
    }


    /**
     *
     * @param user
     * @return
     */
    @PutMapping("/updateUser")
    @Operation(summary  =  "用户修改")
    public  ResponseEntity<Boolean>  updateUser(@RequestBody  User  user)  {

        try  {
            //  校验用户信息是否为空
            if  (user  ==  null)  {
                return  ResponseEntity.badRequest().body(false);
            }

            //  从数据库中获取待修改的用户信息
            User  oldUser  =  userService.getById(user.getUserId());
            if  (oldUser  ==  null)  {
                //  待修改用户不存在
                return  ResponseEntity.unprocessableEntity().body(false);
            }

            //  更新用户信息到数据库
            boolean  result  =  userService.updateById(user);
            if  (result)  {
                //  更新成功
                return  ResponseEntity.ok(true);
            }  else  {
                //  更新失败
                return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
            }
        }  catch  (Exception  e)  {
            //  捕获异常并返回错误信息
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }


        @PostMapping(value = "/{userId}/password",produces = "text/plain;charset=UTF-8")
        @Operation(summary  =  "用户修改密码")
        @Parameters({
                @Parameter(name = "userId", description = "用户id",required = true),
                @Parameter(name = "oldPassword", description = "旧密码",required = true),
                @Parameter(name = "newPassword", description = "新密码",required = true),
                @Parameter(name = "confirmPassword", description = "确认密码",required = true),
        })
        public ResponseEntity<String> updatePassword(@PathVariable("userId") Integer userId,
                @RequestParam("oldPassword") String oldPassword,
                @RequestParam("newPassword") String newPassword,
                @RequestParam("confirmPassword") String confirmPassword) {
            User user = userDao.selectByUserId1(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body("未找到用户");
            }

            if (!user.getPwd().equals(oldPassword)) {
                return ResponseEntity.badRequest().body("旧密码不匹配");
            }

            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("新密码和确认密码不匹配");
            }

            user.setPwd(newPassword);
            userDao.updateById(user);

            return ResponseEntity.ok("密码更新成功");
        }
}

