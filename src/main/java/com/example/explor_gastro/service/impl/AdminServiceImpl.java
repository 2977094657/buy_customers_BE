package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.AdminDao;
import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.entity.Admin;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.AdminService;
import com.example.explor_gastro.utils.Md5;
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

    @Resource
    private UserDao userDao;


    @Override
    public IPage<User> testSelectPage(int current, int size, Optional<Boolean> isAsc, Optional<String> sortField) {
        // 创建一个 Page 对象，指定当前页码和每页记录数
        Page<User> page = new Page<>(current, size);
        // 创建一个 QueryWrapper 对象
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 判断是否传入了 isAsc 参数
        if (isAsc.isPresent()) {
            if (isAsc.get()) {
                wrapper.orderByAsc(sortField.orElse("")); // 如果传入了 sortField 参数，则按照 sortField 升序排序，否则不排序
            } else {
                wrapper.orderByDesc(sortField.orElse("")); // 如果传入了 sortField 参数，则按照 sortField 降序排序，否则不排序
            }
        }
        // 调用 ProductDao 的 selectPage 方法进行分页查询
        IPage<User> iPage = userDao.selectPage(page, wrapper);
        // 打印总页数
        System.out.println("总页数: " + iPage.getPages());
        // 打印总记录数
        System.out.println("总记录数: " + iPage.getTotal());
        // 打印当前页上的记录
        System.out.println("记录: " + iPage.getRecords());
        return iPage;
    }
}

