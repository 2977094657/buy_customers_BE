package com.example.explor_gastro.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.explor_gastro.entity.Admin;
import com.example.explor_gastro.entity.User;

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

