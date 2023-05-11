package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.transform.Result;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    UserDao userDao;

    public boolean LoginIn(String phone, String pwd) {
        User user = userDao.selectByUsername(phone);
        if (user != null && user.getPwd().equals(pwd)){
            return true;
        }
        return false;
    }

}

