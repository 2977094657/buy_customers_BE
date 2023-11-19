package com.buy_customers.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.buy_customers.entity.Vendor;

import java.util.Optional;

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

    IPage<Vendor> testSelectPage(int current, int size, Optional<Boolean> isAsc, Optional<String> sortField);
}


