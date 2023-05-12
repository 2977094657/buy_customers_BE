package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.UserService;
import com.example.explor_gastro.utils.Md5;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
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

    /**
     * 登录功能
     * @param phone 手机号
     * @param pwd 密码
     * @return true登录成功，false登录失败
     */
    @PostMapping(value = "/loginIn",produces  =  "text/plain;charset=UTF-8")
    @Operation(summary = "用户登录")
    @Parameters({
            @Parameter(name = "phone", description = "手机号"),
            @Parameter(name = "pwd", description = "用户密码"),
    })
    public String login(@RequestParam(value = "phone",required = true) String phone, @RequestParam(value = "pwd",required = true) String pwd){
        try{
            userService.LoginIn(phone,pwd);
            return "登录成功";
        }catch (RuntimeException e){
            return e.getMessage();
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

