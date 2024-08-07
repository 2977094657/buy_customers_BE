package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.common.utils.Md5;
import com.buy_customers.dao.UserDao;
import com.buy_customers.entity.User;
import com.buy_customers.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Resource
    UserDao userDao;

    //登录
    @Override
    public User LoginIn(String phone, String pwd) {
        User user = userDao.selectByUsername(phone);
//        if (user != null && user.getPwd().equals(pwd)){
//            return true;
//        } else
            if (user==null) {
            throw new RuntimeException("用户不存在");
        }
            if (!user.getPwd().equals(pwd)){
                throw new RuntimeException("密码错误！");
            }
        return user;
    }

    @Override
    public boolean register(User user) throws NoSuchAlgorithmException {
        User existedUser = userDao.selectOne(new QueryWrapper<User>().eq("name", user.getName()));
        if (existedUser != null) {
            // 用户名已存在，注册失败
            return false;
        }

        // 校验手机号
        User mobileUser = userDao.selectUserByPhone(user.getPhone());
        if (mobileUser != null) {
            // 手机号已注册，注册失败
            return false;
        }
        //MD5加密 密码
        String pwd1= Md5.MD5Encryption(user.getPwd());
        // 注册成功，保存用户信息
        User newUser = new User();
        newUser.setName(user.getName());
        //将md5加密后的存入
        newUser.setPwd(pwd1);
        newUser.setPhone(user.getPhone());
        newUser.setAddress(user.getAddress());
        newUser.setDescription(user.getDescription());
        userDao.insert(newUser);
        return true;
    }

    @Override
    public boolean update(User user) {
        return false;
    }


}





