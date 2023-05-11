package com.example.explor_gastro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.explor_gastro.entity.User;

import javax.xml.transform.Result;

/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
public interface UserService extends IService<User> {

    User LoginIn(String phone, String pwd);

}

