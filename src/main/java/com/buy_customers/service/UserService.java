package com.buy_customers.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buy_customers.entity.User;

import java.security.NoSuchAlgorithmException;

/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
public interface UserService extends IService<User> {

    User LoginIn(String phone, String pwd);

    boolean register(User user) throws NoSuchAlgorithmException;

    boolean update(User user);

}

