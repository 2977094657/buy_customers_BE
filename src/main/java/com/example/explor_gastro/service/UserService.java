package com.example.explor_gastro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.explor_gastro.entity.User;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
public interface UserService extends IService<User> {

    User LoginIn(String phone, String pwd);

    boolean register(User user) throws NoSuchAlgorithmException;


    boolean updateUser(Integer userId, String name, String description, String address, String phone);


    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User selectUserById(Integer userId);

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


}

