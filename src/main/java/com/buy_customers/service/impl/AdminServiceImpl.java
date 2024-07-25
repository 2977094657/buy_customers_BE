package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.UserDao;
import com.buy_customers.entity.Admin;
import com.buy_customers.entity.User;
import com.buy_customers.service.AdminService;
import com.buy_customers.dao.AdminDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 管理员表(Admin)表服务实现类
 *
 * @author makejava
 * @since 2023-05-06 19:46:25
 */
@Service("adminService")
public class AdminServiceImpl extends ServiceImpl<AdminDao, Admin> implements AdminService {
}

