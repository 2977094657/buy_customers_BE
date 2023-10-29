package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.explor_gastro.common.utils.*;
import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.dao.VendorDao;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.entity.Vendor;
import com.example.explor_gastro.service.UserService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private MailUtil mailUtil;

    @Resource
    private JwtService jwtService;
    @Resource
    private ImageUpload imageUpload;

    /**
     * 登录功能
     *
     * @param phone 手机号1
     * @param pwd   密码
     * @return 响应内容
     */
    @PostMapping(value = "/login", produces = "application/json")
    @Operation(summary = "用户登录")
    @Parameters({
            @Parameter(name = "phone", description = "手机号"),
            @Parameter(name = "pwd", description = "用户密码"),
    })
    public ResponseEntity<?> login(@RequestParam String phone,
                                   @RequestParam String pwd) throws NoSuchAlgorithmException {
        try {
            User user = userService.LoginIn(phone, Md5.MD5Encryption(pwd));
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


    @GetMapping(value = "/token", produces = "application/json")
    @Operation(summary = "解析token")
    @Parameters({
            @Parameter(name = "Authorization", description = "用户token"),
    })
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // 提取 token，移除 "Bearer " 前缀
            User user = jwtService.parseToken(authHeader);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 用户注册功能
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
     */
    @PostMapping(value = "/message", produces = "text/plain;charset=UTF-8")
    @Operation(summary = "获取短信")
    @Parameters({
            @Parameter(name = "phoneNumber", description = "手机号", required = true),
    })
    public String sms(String phoneNumber) throws TencentCloudSDKException {
        return txSendSms.sms(phoneNumber);
    }

    @ResponseBody
    @PostMapping("mail")
    @Operation(summary = "获取邮件")
    @Parameters({
            @Parameter(name = "mail", description = "用户邮箱", required = true),
    })
    public ResponseEntity<Response<?>> mail(String mail) {
        return mailUtil.sendEmailWithVerificationCode(mail);
    }


    /**
     * 搜索商家功能
     */
    @GetMapping("/search")
    @Operation(summary = "搜索商家")
    @Parameters({
            @Parameter(name = "keyword", description = "关键词，示例值：江湖菜;瓦香鸡"),
    })
    public List<Vendor> searchVendors(@RequestParam(required = false, defaultValue = "江湖菜") String keyword) {
        return vendorDao.searchVendors(keyword);
    }


    /**
     *
     */
    @GetMapping("all")
    @Operation(summary = "个人中心的获取用户")
    public R<User> selectUserById(@RequestParam Integer userId) {
        //  根据传入的  userId  查询对应的用户信息
        User user = userService.selectUserById(userId);
        //  将查询结果封装到通用返回结果类型中，并返回
        return R.ok(user);
    }

    @PutMapping("updateUser")
    @Operation(summary = "个人中心的用户修改")
    @Parameters({
            @Parameter(name = "userId", description = "根据用户id修改"),
            @Parameter(name = "name", description = "用户名字"),
            @Parameter(name = "description", description = "用户简介"),
            @Parameter(name = "gender", description = "用户性别"),
    })
    public ResponseEntity<Response<?>> updateUser(
            @RequestParam Integer userId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String gender
    ) {
        Response<String> response = new Response<>();

        // 验证非空
        if (userId == null || name == null || description == null || gender == null) {
            response.setCode(400);
            response.setMsg("所有字段不能为空");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 去除前后空格
        name = name.trim();
        description = description.trim();
        gender = gender.trim();

        // 查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        User currentUser = userService.getOne(queryWrapper);
        if (currentUser == null) {
            response.setCode(404);
            response.setMsg("用户不存在");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

//        检查用户名
        if (!currentUser.getName().equals(name)) {
            QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
            objectQueryWrapper.eq("name", name);
            int count1 = userService.count(objectQueryWrapper);
            if (count1 > 0){
                response.setCode(400);
                response.setMsg("用户名太受欢迎了，试试其他名字吧");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setDescription(description);
        user.setGender(gender);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", userId);
        userService.update(user, userQueryWrapper);
        response.setCode(200);
        response.setMsg("修改成功");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/password")
    @Operation(summary = "用户修改密码")
    @Parameters({
            @Parameter(name = "userId", description = "用户id", required = true),
            @Parameter(name = "oldPassword", description = "旧密码", required = true),
            @Parameter(name = "newPassword", description = "新密码", required = true),
            @Parameter(name = "confirmPassword", description = "确认密码", required = true),
    })
    public ResponseEntity<Response<?>> updatePassword(@RequestParam("userId") Integer userId,
                                                 @RequestParam("oldPassword") String oldPassword,
                                                 @RequestParam("newPassword") String newPassword,
                                                 @RequestParam("confirmPassword") String confirmPassword) throws NoSuchAlgorithmException {
        User user = userDao.selectByUserId1(userId);
        Response<String> response = new Response<>();
        if (user == null) {
            response.setCode(400);
            response.setMsg("未找到用户");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        if (!user.getPwd().equals(Md5.MD5Encryption(oldPassword))) {
            response.setCode(400);
            response.setMsg("旧密码不匹配");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        if (!newPassword.equals(confirmPassword)) {
            response.setCode(400);
            response.setMsg("新密码和确认密码不匹配");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        user.setPwd(Md5.MD5Encryption(newPassword));
        userDao.updateById(user);

        response.setCode(200);
        response.setMsg("密码更新成功");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("updateAvatar")
    @Operation(summary = "用户修改头像")
    public ResponseEntity<Map<String, String>> updateAvatar(
            @RequestParam(name = "image") MultipartFile file,
            @RequestParam(name = "userid") Integer userid) throws IOException {
        return imageUpload.upload(file, userid);
    }

    @PostMapping("addHistory")
    @Operation(summary = "添加浏览记录")
    public ResponseEntity<Response<?>> addHistory(
            @RequestParam Integer userid,
            @RequestParam Integer productId
    ) {
        String date = LocalDate.now().toString();
        String key = "user:" + userid + ":product:" + productId + ":date:" + date;
        String value = String.valueOf(System.currentTimeMillis());
        Response<String> response = new Response<>();

        try {
            // 将查看的时间戳存储为key的值
            stringRedisTemplate.opsForValue().set(key, value, 30, TimeUnit.DAYS);
            response.setCode(200);
            response.setMsg("浏览记录添加成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setCode(400);
            response.setMsg("浏览记录添加失败");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("getHistoryByUserId")
    @Operation(summary = "显示浏览记录")
    public ResponseEntity<Map<String, Object>> getHistoryByUserId(@RequestParam Integer userId){
        String keyPattern = "user:" + userId + ":*";
        Set<String> keys = stringRedisTemplate.keys(keyPattern);

        List<Map<String, Object>> histories = new ArrayList<>();
        if (keys != null) {
            for(String key : keys) {
                String value = stringRedisTemplate.opsForValue().get(key);
                String[] parts = key.split(":");
                Map<String, Object> history = new HashMap<>();
                history.put("userId", Integer.parseInt(parts[1]));
                history.put("productId", Integer.parseInt(parts[3]));
                if (value != null) {
                    history.put("timestamp", Long.parseLong(value));
                }
                histories.add(history);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "浏览记录获取成功");
        response.put("data", histories);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("deleteHistory")
    @Operation(summary = "删除浏览记录")
    public ResponseEntity<Response<?>> deleteHistory(
            @RequestParam Integer userid,
            @RequestParam Integer productId,
            @RequestParam String date
    ) {
        String key = "user:" + userid + ":product:" + productId + ":date:" + date;
        System.out.println(key);
        Response<String> response = new Response<>();

        try {
            // 删除指定的key
            stringRedisTemplate.delete(key);
            response.setCode(200);
            response.setMsg("浏览记录删除成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setCode(400);
            response.setMsg("浏览记录删除失败");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @DeleteMapping("deleteAllHistory")
    @Operation(summary = "删除用户的全部浏览记录")
    public ResponseEntity<Response<?>> deleteAllHistory(@RequestParam Integer userid) {
        String pattern = "user:" + userid + ":product:*:date:*";
        Response<String> response = new Response<>();
        try {
            Set<String> keys = stringRedisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);
            }
            response.setCode(200);
            response.setMsg("用户的全部浏览记录删除成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setCode(400);
            response.setMsg("用户的全部浏览记录删除失败");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PutMapping(value = "/changePhone")
    @Operation(summary = "修改手机号")
    @Parameters({
            @Parameter(name = "userid", description = "用户id"),
            @Parameter(name = "oldPhone", description = "旧手机号"),
            @Parameter(name = "code", description = "验证码"),
            @Parameter(name = "phone", description = "新手机号"),
    })
    public ResponseEntity<Response<?>> changePhone(
            @RequestParam Integer userid,
            @RequestParam String phone,
            @RequestParam String oldPhone,
            @RequestParam int code) {
        Response<String> response = new Response<>();
        // 校验参数
        if (phone == null) {
            response.setCode(400);
            response.setMsg("手机号不能为空");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        // 手机号正则校验，可自行定义
        if (!Pattern.matches("^1[3-9]\\d{9}$", phone)) {
            response.setCode(400);
            response.setMsg("手机号格式不正确");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
//        检验手机号是否唯一
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("phone", phone);
        int count = userService.count(userQueryWrapper);
        if (count>0){
            response.setCode(400);
            response.setMsg("手机号已存在");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 校验验证码
        boolean hasKey = Boolean.TRUE.equals(stringRedisTemplate.hasKey(oldPhone));
        if (!hasKey) {
            response.setCode(400);
            response.setMsg("验证码已过期，请重新发送");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        int redisCode = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(oldPhone)));
        if (code != redisCode) {
            response.setCode(400);
            response.setMsg("验证码错误");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        User user = new User();
        user.setPhone(phone);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userid);
        boolean update = userService.update(user, queryWrapper);
        if (update) {
            response.setCode(200);
            response.setMsg("修改成功");
        } else {
            response.setCode(400);
            response.setMsg("修改失败");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

