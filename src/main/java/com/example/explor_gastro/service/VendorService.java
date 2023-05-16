package com.example.explor_gastro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.entity.Vendor;

import java.security.NoSuchAlgorithmException;

/**
 * 商家表(Vendor)表服务接口
 *
 *
 * @author makejava
 * @since 2023-05-08 11:55:50
 */
public interface VendorService extends IService<Vendor> {

    Vendor LoginIn(String phone, String pwd);

    Vendor register(String name, String password,String phone);

}


