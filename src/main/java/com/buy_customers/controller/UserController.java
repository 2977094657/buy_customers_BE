package com.buy_customers.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.buy_customers.common.config.DataEncryptionAndDecryption.RSAKeyPairGenerator;
import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.common.utils.*;
import com.buy_customers.dao.UserDao;
import com.buy_customers.dao.VendorDao;
import com.buy_customers.entity.User;
import com.buy_customers.entity.Vendor;
import com.buy_customers.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户表(User)表控制层
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
@Slf4j
@RestController
@RequestMapping("user")
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
    private RSAKeyPairGenerator rsaKeyPairGenerator;

    /**
     * 登录功能
     *
     * @param params 包含登录所需信息的Map，预期包含"usernameOrPhone"（用户名或手机号）、"pwd"（密码）和可选的"expirationTimeOption"（登录有效期选项）
     * @return 返回一个响应实体，包含登录结果信息，如成功则包含token，失败则包含错误信息
     * @throws NoSuchAlgorithmException 如果密码加密算法不可用
     */
    @PostMapping(value = "/login")
    public ResultData<String> login(@RequestBody Map<String, String> params) throws NoSuchAlgorithmException {
        // 处理登录有效期选项
        long timeout = 0;
        if (Objects.equals(params.get("expirationTimeOption"), "0")) {
            timeout = 86400; // 1天
        } else if (Objects.equals(params.get("expirationTimeOption"), "1")) {
            timeout = 604800; // 1周
        }

        // 获取用户名或手机号和密码
        String usernameOrPhone = params.get("usernameOrPhone");
        String pwd = params.get("pwd");

        // 查询用户，支持用户名或手机号登录
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", usernameOrPhone).or().eq("name", usernameOrPhone);
        User user = userService.getOne(queryWrapper);

        // 验证用户存在性和密码正确性
        if (user == null || !user.getPwd().equals(Md5.MD5Encryption(pwd))) {
            return ResultData.fail(400, "用户名或手机号不存在或密码错误");
        }

        // 登录成功，设置登录状态和返回信息
        Integer userId = user.getUserId();
        StpUtil.login(userId, timeout);
        return ResultData.success(String.valueOf(StpUtil.getTokenValue()));
    }



    /**
     * 忘记密码功能的处理方法。
     *
     * @param params 包含手机号、新密码和验证码的键值对参数。
     * @return 根据操作结果返回不同的ResponseEntity对象，包含操作成功或失败的信息。
     * @throws NoSuchAlgorithmException 如果密码加密过程中算法找不到，则抛出此异常。
     */
    @PostMapping("forgotPassword")
    public ResultData<String> forgotPassword(@RequestBody Map<String, String> params) throws NoSuchAlgorithmException {
        try {
            // 从请求体中获取手机号、新密码和验证码
            String phoneNumber = params.get("phoneNumber");
            String newPassword = params.get("newPassword");
            String code = params.get("code");

            // 从 Redis 中获取存储的验证码
            String redisCode = stringRedisTemplate.opsForValue().get(phoneNumber);

            // 验证码比对，不一致则返回错误信息
            if (!code.equals(redisCode)) {
                return ResultData.fail(400, "验证码错误");
            }

            // 根据手机号查询用户信息
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phoneNumber);
            User user = userService.getOne(queryWrapper);

            // 更新用户密码
            user.setPwd(Md5.MD5Encryption(newPassword));
            userService.updateById(user);

            return ResultData.success("密码修改成功");
        } catch (RuntimeException e) {
            // 捕获运行时异常，返回错误信息
            return ResultData.fail(500,e.getMessage());
        }
    }


    /**
     * 通过请求头中的Authorization字段获取用户信息。
     *
     * @param authHeader 请求头中的Authorization字段，包含Bearer令牌。
     * @return 如果成功解析令牌，返回包含用户信息的响应实体；如果解析失败，返回包含错误信息的响应实体。
     */
    @GetMapping(value = "/token")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // 从Authorization头部提取token，移除"Bearer "前缀
            String token = authHeader.substring(7);
            User user = jwtService.parseToken(token); // 解析token，获取用户信息
            return ResponseEntity.ok(user); // 返回成功响应，包含用户信息
        } catch (RuntimeException e) {
            // 捕获运行时异常，返回错误信息
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 用户注册功能。
     * 接收用户提交的注册信息，包括手机号、用户名、密码和验证码，进行一系列校验后，
     * 若信息符合要求则完成用户注册。
     *
     * @param phone 用户的手机号，作为注册账号的唯一标识。
     * @param name 用户的姓名。
     * @param pwd 用户设置的密码。
     * @param code 用户输入的验证码。
     * @return 根据注册结果返回相应的提示信息。
     * @throws NoSuchAlgorithmException 如果在注册过程中遇到未知的算法异常。
     */
    @PostMapping("register")
    public ResultData<String> register(@RequestParam(value = "phone") String phone,
                           @RequestParam(value = "name") String name,
                           @RequestParam(value = "pwd") String pwd,
                           @RequestParam(value = "code") int code) throws NoSuchAlgorithmException {

        // 校验参数完整性
        if (phone == null) {
            return ResultData.fail(400,"手机号不能为空");
        }

        User user = new User();
        user.setPhone(phone);
        user.setName(name);
        user.setPwd(pwd);

        // 检查验证码的有效性
        boolean hasKey = Boolean.TRUE.equals(stringRedisTemplate.hasKey(phone));
        if (!hasKey) {
            return ResultData.fail(400,"验证码已过期，请重新发送");
        }
        // 对输入的验证码进行校验
        int redisCode = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(phone)));
        if (code != redisCode) {
            return ResultData.fail(400,"验证码错误");
        }

        // 尝试注册用户
        boolean success = userService.register(user);
        if (success) {
            return ResultData.success("注册成功");
        } else {
            return ResultData.fail(400,"注册失败，用户名或手机号已存在");
        }
    }



    /**
     * 短信获取
     */
    @PostMapping("message")
    public ResultData<String> sms(String phoneNumber) throws TencentCloudSDKException {
        return ResultData.success(txSendSms.sms(phoneNumber).getSendStatusSet()[0].getMessage());
    }

    @ResponseBody
    @PostMapping("mail")
    public ResultData<String> mail(String mail) {
        return mailUtil.sendEmailWithVerificationCode(mail);
    }


    /**
     * 搜索商家功能
     */
    @GetMapping("/search")
    public List<Vendor> searchVendors(@RequestParam(required = false, defaultValue = "江湖菜") String keyword) {
        return vendorDao.searchVendors(keyword);
    }


    @GetMapping("all")
    public User selectUserById(@RequestParam Integer userId) {
        return userService.getById(userId);
    }


    @PutMapping("updateUser")
    public ResultData<String> updateUser(
            @RequestParam Integer userId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String gender
    ) {

        // 验证非空
        if (userId == null || name == null || description == null || gender == null) {
            return ResultData.fail(400,"所有字段不能为空");
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
            return ResultData.fail(404,"用户不存在");
        }

        //        检查用户名
        if (!currentUser.getName().equals(name)) {
            QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
            objectQueryWrapper.eq("name", name);
            int count1 = userService.count(objectQueryWrapper);
            if (count1 > 0){
                return ResultData.fail(400,"用户名太受欢迎了，试试其他名字吧");
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
        return ResultData.success("修改成功");
    }


    @PostMapping(value = "/password")
    public ResultData<String> updatePassword(@RequestParam("userId") Integer userId,
                                                 @RequestParam("oldPassword") String oldPassword,
                                                 @RequestParam("newPassword") String newPassword,
                                                 @RequestParam("confirmPassword") String confirmPassword) throws NoSuchAlgorithmException {
        User user = userService.getById(userId);
        if (user == null) {
            return ResultData.fail(404,"未找到用户");
        }

        if (!user.getPwd().equals(Md5.MD5Encryption(oldPassword))) {
            return ResultData.fail(400,"旧密码不匹配");
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResultData.fail(400,"新密码和确认密码不匹配");
        }

        user.setPwd(Md5.MD5Encryption(newPassword));
        userDao.updateById(user);

        return ResultData.success("密码更新成功");
    }


    @PutMapping("updateAvatar")
    public ResultData<String> updateAvatar(
            @RequestParam(name = "image") MultipartFile file,
            @RequestParam(name = "userid") Integer userid) throws IOException {
        ResultData<String> uploadResponse = ImageUpload.upload(file);
        String url = uploadResponse.getData();
        User user = new User();
        user.setUserAvatar(url);
        user.setUserId(userid);
        userService.updateById(user);
        return uploadResponse;
    }


    @PostMapping("addHistory")
    public ResultData<String> addHistory(@RequestParam Integer userid, @RequestParam Integer productId) {
        String date = LocalDate.now().toString();
        String key = "user:" + userid + ":product:" + productId + ":date:" + date;
        String value = String.valueOf(System.currentTimeMillis());

        // 将查看的时间戳存储为key的值
        stringRedisTemplate.opsForValue().set(key, value, 30, TimeUnit.DAYS);
        return ResultData.success("浏览记录添加成功");
    }

    @GetMapping("getHistoryByUserId")
    public List<Map<String, Object>> getHistoryByUserId(@RequestParam Integer userId){
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

        return histories;
    }

    /**
     * 删除用户的特定产品浏览历史记录。
     *
     * @param userid 用户ID，用于标识删除记录的用户。
     * @param productId 产品ID，用于标识要删除的产品浏览记录。
     * @param date 浏览日期，用于精确指定要删除的浏览记录日期。
     * @return ResponseEntity<Response<?>> 包含操作结果的响应体，成功则返回200和删除成功的信息，失败则返回400和删除失败的信息。
     */
    @DeleteMapping("deleteHistory")
    public ResultData<String> deleteHistory(
            @RequestParam Integer userid,
            @RequestParam Integer productId,
            @RequestParam String date
    ) {
        // 构造Redis中存储的键名
        String key = "user:" + userid + ":product:" + productId + ":date:" + date;
        System.out.println(key);

        try {
            // 尝试删除指定的键
            stringRedisTemplate.delete(key);
            return ResultData.success("浏览记录删除成功");
        } catch (Exception e) {
            // 捕获异常，设置删除失败的响应
            return ResultData.fail(400,"浏览记录删除失败");
        }
    }

    /**
     * 删除指定用户的全部浏览记录
     *
     * @param userid 用户ID，用于指定要删除浏览记录的用户
     * @return ResponseEntity<Response<?>> 包含操作结果的响应体，其中Response<?>是自定义的响应数据结构，
     *         包含操作状态码和操作消息
     */
    @DeleteMapping("deleteAllHistory")
    public ResultData<String> deleteAllHistory(@RequestParam Integer userid) {
        // 构造Redis中浏览记录的键名模式
        String pattern = "user:" + userid + ":product:*:date:*";
        try {
            // 根据模式查找所有匹配的键
            Set<String> keys = stringRedisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                // 如果找到键，则删除这些键
                stringRedisTemplate.delete(keys);
            }
            // 设置操作成功响应
            return ResultData.success("用户的全部浏览记录删除成功");
        } catch (Exception e) {
            // 设置操作失败响应
            return ResultData.fail(400,"用户的全部浏览记录删除失败");
        }
    }

    @PutMapping(value = "changePhone")
    public ResultData<String> changePhone(@RequestBody Map<String, Object> request) {
        String userid =(String) request.get("userid");
        String phone = (String) request.get("phone");
        String oldPhone = (String) request.get("oldPhone");
        String code = (String) request.get("code");
        // 校验参数
        if (phone == null) {
            return ResultData.fail(400,"手机号不能为空");
        }

        //        检验手机号是否唯一
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("phone", phone);
        int count = userService.count(userQueryWrapper);
        if (count>0){
            return ResultData.fail(400,"手机号已存在");
        }

        // 校验验证码
        boolean hasKey = Boolean.TRUE.equals(stringRedisTemplate.hasKey(oldPhone));
        if (!hasKey) {
            return ResultData.fail(400,"验证码已过期，请重新发送");
        }
        String redisCode = String.valueOf(Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(oldPhone))));
        if (!Objects.equals(code, redisCode)) {
            return ResultData.fail(400,"验证码错误");
        }

        User user = new User();
        user.setPhone(phone);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userid);
        boolean update = userService.update(user, queryWrapper);
        if (update) {
            return ResultData.success("修改成功");
        } else {
            return ResultData.fail(400,"修改失败");
        }
    }


    /**
     * 获取当前登录用户的ID。
     * 该接口使用GET请求访问路径"getLoginId"，不接受任何参数，响应为当前会话登录用户的ID字符串。
     * 如果用户未登录，将返回默认的null值。
     *
     * @return 返回当前会话登录用户的ID字符串。如果用户未登录，则返回null。
     */
    @GetMapping("getLoginId")
    public String getLoginId() {
        // 获取并返回当前会话登录用户的ID
        return "当前会话登录id："+ StpUtil.getLoginIdDefaultNull();
    }

    @GetMapping("getLoginIdAsLong")
    public ResultData<Long> getLoginIdAsLong(){
        return ResultData.success(StpUtil.getLoginIdAsLong());
    }

    /**
     * 用户登出处理
     *
     * @param userid 用户ID，用于标识登出的用户
     * @return 返回一个字符串提示信息，表明用户已成功登出
     */
    @GetMapping("logout")
    public String logout(Integer userid) {
        // 执行用户登出操作
        StpUtil.logout(userid);
        return "已退出登录";
    }

    /**
     * 通过令牌值注销用户。
     *
     * @param token 用户的令牌值，用于标识和验证用户。
     * @return 返回一个字符串提示用户已成功退出登录。
     */
    @GetMapping("logoutByTokenValue")
    public String logoutByTokenValue(String token) {
        // 使用提供的令牌值注销用户
        StpUtil.logoutByTokenValue(token);
        return "已退出登录";
    }

    /**
     * 通过token获取登录用户ID。
     *
     * @param params 包含token信息的请求体参数，其中需包含一个键为"tokenValue"的元素，其值为token字符串。
     * @return 返回登录用户的ID，如果无法获取或token无效，则可能返回null或其他特定错误信息。
     */
    @PostMapping("getLoginIdByToken")
    public ResultData<Object> getLoginIdByToken(@RequestBody Map<String, String> params) {
        // 通过token获取登录用户ID
        return ResultData.success(StpUtil.getLoginIdByToken(params.get("tokenValue")));
    }

    /**
     * 获取指定token的超时时间。
     *
     * @param token 用户的token，用于查询其超时时间。
     * @return 返回该token的超时时间，单位为毫秒。
     */
    @GetMapping("getTokenTimeout")
    public long getTokenTimeout(String token) {
        // 调用STP工具类，获取token的超时时间
        return StpUtil.getTokenTimeout(token);
    }

    @GetMapping("allToken")
    public ResultData<Map<String,Object>> allToken() throws JsonProcessingException {
        Set<String> keys = stringRedisTemplate.keys("satoken:login:token:*");
        Map<String, Object> tokens = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        if (keys != null) {
            for (String key : keys) {
                String value = stringRedisTemplate.opsForValue().get(key);
                Object obj = mapper.readValue(value, Object.class);
                tokens.put(key, obj);
            }
        }
        return ResultData.success(tokens);
    }
}

