package com.example.explor_gastro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.explor_gastro.entity.User;

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


//    boolean updateUser(Integer userId, String name, String description, String address, Date signupTime, String phone);
//    public boolean updateUer2(User user);



    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User selectUserById(Integer userId);

    boolean updateById(Integer userId, String name, String pwd, String phone, String description, String address);

    boolean updateUser(Integer userId, String name, String description, String address, String phone);

//    boolean updateById(Integer userId, String name, String pwd, String phone, String description, String address);

    boolean update(User user);

    boolean isUserNameExists(String name);

    boolean isUserPhoneExists(String phone);
}

