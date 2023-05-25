package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.dao.VendorDao;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.entity.Vendor;
import com.example.explor_gastro.service.UserService;
import com.example.explor_gastro.utils.ImageUpload;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
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
    @Resource
    private ImageUpload imageUpload;

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
                                   @RequestParam(defaultValue = "8888") String pwd) {
        try {
            User user = userService.LoginIn(phone, pwd);
            String token = jwtService.generateToken(user);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            Integer userId = user.getUserId();
            response.put("userid", userId);
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
     * @param
     * @return
     */
    @PostMapping(value = "/register", produces = "text/plain;charset=UTF-8")
    @Operation(summary = "注册")
    @Parameters({
            @Parameter(name = "phone", description = "手机号", required = true),
            @Parameter(name = "name", description = "用户名", required = true),
            @Parameter(name = "pwd", description = "用户密码", required = true),
            @Parameter(name = "code", description = "验证码", required = true),
    })
    public String register(@RequestParam(value = "phone") String phone,
                           @RequestParam(value = "name") String name,
                           @RequestParam(value = "pwd") String pwd,
                           @RequestParam(value = "code") int code) throws NoSuchAlgorithmException {
        // 校验参数
        if (phone == null) {
            return "手机号不能为空";
        }
        // 手机号正则校验，可自行定义
        if (!Pattern.matches("^1[3-9]\\d{9}$", phone)) {
            return "手机号格式不正确";
        }

        User user = new User();
        user.setPhone(phone);
        user.setName(name);
        user.setPwd(pwd);

        // 校验验证码
        boolean hasKey = Boolean.TRUE.equals(stringRedisTemplate.hasKey(phone));
        if (!hasKey) {
            return "验证码已过期，请重新发送";
        }
        int redisCode = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(phone)));
        if (code != redisCode) {
            return "验证码错误";
        }

        // 注册用户
        boolean success = userService.register(user);
        if (success) {
            return "注册成功";
        } else {
            return "注册失败，用户名或手机号已存在";
        }
    }


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
     *
     * @param userId
     * @return
     */
    //  通过  Get  请求访问URL中的  {userId}，并返回相应的用户信息
    @GetMapping("all")
    //  自定义接口描述信息
    @Operation(summary = "个人中心的获取用户")
    public R<User> selectUserById(@RequestParam Integer userId) {
        //  根据传入的  userId  查询对应的用户信息
        User user = userService.selectUserById(userId);
        //  将查询结果封装到通用返回结果类型中，并返回
        return R.ok(user);
    }

    @PutMapping(value = "updateUser", produces = "text/plain;charset=UTF-8")
    @Operation(summary = "个人中心的用户修改")
    @Parameters({
            @Parameter(name = "userId", description = "根据用户id修改"),
            @Parameter(name = "name", description = "用户名字"),
            @Parameter(name = "phone", description = "用户手机11位"),
            @Parameter(name = "description", description = "用户简介"),
            @Parameter(name = "address", description = "地址")
    })
    //  定义了一个公共方法，返回类型为ResponseEntity<String>
    public  ResponseEntity<String>  updateUser(
            //  指定要接收的参数userId，类型为int
            @RequestParam("userId")  int  userId,
            //  指定要接收的参数name，类型为String，可选参数
            @RequestParam(value  =  "name",  required  =  false)  String  name,
            //  指定要接收的参数phone，类型为String，可选参数
            @RequestParam(value  =  "phone",  required  =  false)  String  phone,
            //  指定要接收的参数description，类型为String，可选参数
            @RequestParam(value  =  "description",  required  =  false)  String  description,
            //  指定要接收的参数address，类型为String，可选参数
            @RequestParam(value  =  "address",  required  =  false)  String  address
    ) {
            // 校验用户信息是否为空
            if (userId == 0) {
                return ResponseEntity.badRequest().body("校验用户信息是否为空");
            }

            // 从数据库中获取待修改的用户信息
            User oldUser = userService.getById(userId);
            if (oldUser == null) {
                // 待修改用户不存在
                return ResponseEntity.unprocessableEntity().body("待修改用户不存在");
            }

        //  判断name不为空且与oldUser的名字不相同
        if  (name  !=  null  &&  !name.equals(oldUser.getName()))  {
            //  创建名为columnMap的HashMap
            Map<String,  Object>  columnMap  =    new  HashMap<>();
            //  向columnMap中添加name和它的值
            columnMap.put("name",name);
            //  通过列名等于name的条件查询User对象，并储存在名为users的集合中
            Collection<User>  users  =  userService.listByMap(columnMap);
            //  如果users集合不为空，返回一个状态码为400的响应以及一个字符串："用户名已经存在"
            if  (!users.isEmpty())  {
                return  ResponseEntity.badRequest().body("用户名已经存在");
            }
        }

        //  判断电话号码不为空且不等于旧用户的电话号码
        if  (phone  !=  null  &&  !phone.equals(oldUser.getPhone()))  {
            //  创建键值对集合
            Map<String,  Object>  columnMap  =  new  HashMap<>();
            //  将电话号码放入键值对集合中
            columnMap.put("phone",phone);
            //  通过键值对查询用户列表
            Collection<User>  users  =  userService.listByMap(columnMap);
            //  如果用户列表不为空，则手机号已经存在，返回请求错误信息
            if  (!users.isEmpty())  {
                return  ResponseEntity.badRequest().body("手机号已经存在");
            }
        }
            // 更新用户信息到数据库
        //  如果name不为null，则将name设置为oldUser的name
        if  (name  !=  null)  {
            oldUser.setName(name);
        }
//  如果phone不为null，则将phone设置为oldUser的phone
        if  (phone  !=  null)  {
            oldUser.setPhone(phone);
        }
//  如果description不为null，则将description设置为oldUser的description
        if  (description  !=  null)  {
            oldUser.setDescription(description);
        }
//  如果address不为null，则将address设置为oldUser的address
        if  (address  !=  null)  {
            oldUser.setAddress(address);
        }
//  通过userService更新oldUser
        userService.updateById(oldUser);

//  返回一个响应实体，表明修改成功
        return  ResponseEntity.ok("修改成功");
    }



    @PostMapping(value = "/password",produces = "text/plain;charset=UTF-8")
        @Operation(summary  =  "用户修改密码")
        @Parameters({
                @Parameter(name = "userId", description = "用户id",required = true),
                @Parameter(name = "oldPassword", description = "旧密码",required = true),
                @Parameter(name = "newPassword", description = "新密码",required = true),
                @Parameter(name = "confirmPassword", description = "确认密码",required = true),
        })
        public ResponseEntity<String> updatePassword(@RequestParam("userId") Integer userId,
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

        @PutMapping("{userid}/updateAvatar")
        @Operation(summary = "用户修改头像")
        public ResponseEntity<Map<String, String>> updateAvatar(
                @RequestParam(name = "image") MultipartFile file,
                @PathVariable(name = "userid") Integer userid) throws IOException {
        return imageUpload.upload(file,userid);
        }




}

