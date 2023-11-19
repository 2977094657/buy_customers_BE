package com.buy_customers.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.buy_customers.entity.Admin;
import com.buy_customers.entity.User;

import java.util.Optional;

/**
 * 管理员表(Admin)表服务接口
 *
 * @author makejava
 * @since 2023-05-06 19:46:25
 */
public interface AdminService extends IService<Admin> {

    IPage<User> testSelectPage(int current, int size, Optional<Boolean> isAsc, Optional<String> sortField);

}

