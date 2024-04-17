package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.buy_customers.common.utils.Md5;
import com.buy_customers.common.utils.RSAKeyPairGenerator;
import com.buy_customers.common.utils.Response;
import com.buy_customers.dao.UserDao;
import com.buy_customers.entity.Admin;
import com.buy_customers.entity.Product;
import com.buy_customers.entity.User;
import com.buy_customers.service.AdminService;
import com.buy_customers.service.ProductService;
import com.buy_customers.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 管理员表(Admin)表控制层
 *
 * @author makejava
 * @since 2023-05-06 19:46:24
 */
@RestController
@CrossOrigin
@RequestMapping("admin")
public class AdminController extends ApiController {
    /**
     * 服务对象
     */
//    哎·
    @Resource
    private AdminService adminService;

    @Resource
    private UserDao userDao;

    @Resource
    private UserService userService;

    @Resource
    private ProductService productService;
    @Resource
    private RSAKeyPairGenerator keyPairGenerator;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String keyword) {
        return userDao.searchUsers(keyword);
    }

    /**
     * @param current   当前所在页面
     * @param size      每页显示数量
     * @param isAsc     是否升序排列，不传或传入空值则不排序
     * @param sortField 根据传入的此字段来排序，不传或传入空值则不排序
     * @return 返回所有数据
     * cy
     */
    @GetMapping("all")
    public IPage<User> page(
            @RequestParam(name = "current",defaultValue = "1") int current,
            @RequestParam(name = "size",defaultValue = "10") int size,
            @RequestParam(name = "isAsc", required = false,defaultValue = "true") Optional<Boolean> isAsc,
            @RequestParam(name = "sortField", required = false,defaultValue = "user_id") Optional<String> sortField) {
        return adminService.testSelectPage(current, size, isAsc, sortField);
    }
    /**
     * 修改用户密码
     * @return
     * cy
     */
    @PutMapping(value = "/pwd",produces  =  "text/plain;charset=UTF-8")
    public String updateUserPwd(@RequestParam Integer userId,
                                @RequestParam(required = false) String pwd,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) String address) throws NoSuchAlgorithmException {
        User user = userDao.selectById(userId); // 查询数据库中的记录
        if (user == null) {
            return "用户不存在";
        }
        // 如果 pwd 不为空，则更新密码
        if (pwd != null && !pwd.isEmpty()) {
            String encryptedPwd = Md5.MD5Encryption(pwd); // MD5Encryption方法
            user.setPwd(encryptedPwd);
        }
        // 如果 description 不为空，则更新描述
        if (description != null && !description.isEmpty()) {
            user.setDescription(description);
        }
        if (phone != null && !phone.isEmpty()) {
            user.setPhone(phone);
        }
        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }
        if (address != null && !address.isEmpty()) {
            user.setAddress(address);
        }
        userDao.updateById(user);
        return "修改成功";
    }

    /**
     * 删除用户
     * cy
     */
    @DeleteMapping(value = "/deleteuser",produces  =  "text/plain;charset=UTF-8")
    @CrossOrigin(origins = "*", maxAge = 3600)
    public String deleteUserPwd(@RequestParam Integer userId) {
        if (userDao.selectById(userId) == null) {
            return "删除失败，该用户不存在";
        } else {
            userDao.deleteById(userId);
            return "删除成功";
        }
    }

    /**
     * 管理员登录
     */
    @PostMapping(value = "login",produces  =  "text/plain;charset=UTF-8")
    public String login(@RequestParam(value = "admin",defaultValue = "admin") String admin,
                        @RequestParam(value = "pwd",defaultValue = "admin") String pwd) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("admin", admin)
                .eq("pwd", pwd);

        Admin result = adminService.getOne(queryWrapper);
        if (result != null) {
            return "登录成功";
        } else {
            return "登录失败";
        }
    }


    @PostMapping(value = "/register", produces = "text/plain;charset=UTF-8")
    public String register(@RequestParam(value = "phone") String phone,
                           @RequestParam(value = "name") String name,
                           @RequestParam(value = "pwd") String pwd,
                           @RequestParam(value = "address") String address,
                           @RequestParam(value = "description") String description
                           ) throws NoSuchAlgorithmException {
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
        user.setAddress(address);
        user.setDescription(description);
        // 注册用户
        boolean success = userService.register(user);
        if (success) {
            return "注册成功";
        } else {
            return "注册失败，用户名或手机号已存在";
        }
    }

    @GetMapping("updateIp")
    public ResponseEntity<Response<?>> updateIp(@RequestParam String old, @RequestParam String newIp) {
        Response <String> response = new Response<>();
        try {
            List<Product> products = productService.list();

            for (Product product : products) {
                // 获取原始的img字段值
                String originalImg = product.getImg();

                // 进行替换操作
                String newImg = originalImg.replace(old, newIp);

                // 创建新的产品对象，将其他字段设置为原始对象的值，只修改img字段
                Product updatedProduct = new Product();
                updatedProduct.setProductId(product.getProductId());  // 设置原始对象的ID或其他标识符
                updatedProduct.setImg(newImg);  // 设置修改后的img字段的值

                // 保存更新后的对象到数据库
                productService.updateById(updatedProduct);
            }
            response.setCode(200);
            response.setMsg("修改成功");
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            response.setCode(400);
            response.setMsg(e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @GetMapping("updateAvatarIp")
    public ResponseEntity<Response<?>> updateAvatarIp(@RequestParam String old,@RequestParam String newIp) {
        Response <String> response = new Response<>();
        try {
            List<User> avatar = userService.list();

            for (User user : avatar) {
                // 获取原始的img字段值
                String originalImg = user.getUserAvatar();

                // 进行替换操作
                String newImg = originalImg.replace(old, newIp);

                // 创建新的产品对象，将其他字段设置为原始对象的值，只修改img字段
                User userAvatar = new User();
                userAvatar.setUserId(user.getUserId());  // 设置原始对象的ID或其他标识符
                userAvatar.setUserAvatar(newImg);  // 设置修改后的img字段的值

                // 保存更新后的对象到数据库
                userService.updateById(userAvatar);
            }
            response.setCode(200);
            response.setMsg("修改成功");
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            response.setCode(400);
            response.setMsg(e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @GetMapping("publicKey")
    public R<Map<String, Object>> publicKey() {
        String publicKey = keyPairGenerator.getPublicKey();
        // 创建一个 Map 来存储用户信息和公钥
        Map<String, Object> response = new HashMap<>();
        response.put("publicKey", publicKey);
        // 将查询结果封装到通用返回结果类型中，并返回
        return R.ok(response);
    }

    @PostMapping("privateKey")
    public R<Map<String, Object>> privateKey(@RequestBody Map<String, String> body) {
        String publicKey = body.get("publicKey");
        // 使用公钥从Redis中获取私钥
        String privateKey = stringRedisTemplate.opsForValue().get(publicKey);
        if (privateKey == null) {
            throw new IllegalStateException("Private key not found for the provided public key.");
        }
        // 创建一个 Map 来存储私钥
        Map<String, Object> response = new HashMap<>();
        response.put("privateKey", privateKey);
        // 将查询结果封装到通用返回结果类型中，并返回
        return R.ok(response);
    }



}

