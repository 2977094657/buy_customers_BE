package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.AdminDao;
import com.example.explor_gastro.entity.Admin;
import com.example.explor_gastro.service.AdminService;
import org.springframework.stereotype.Service;

/**
 * 管理员表(Admin)表服务实现类
 *
 * @author makejava
 * @since 2023-05-06 19:46:25
 */
@Service("adminService")
public class AdminServiceImpl extends ServiceImpl<AdminDao, Admin> implements AdminService {

}

