package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.VendorDao;
import com.buy_customers.entity.Vendor;
import com.buy_customers.service.VendorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 商家表(Vendor)表服务实现类
 *
 * @author makejava
 * @since 2023-05-08 11:55:50
 */
@Service("vendorService")
public class VendorServiceImpl extends ServiceImpl<VendorDao, Vendor> implements VendorService {

    @Resource
    private VendorDao vendorDao;
    @Override
    public Vendor LoginIn(String phone, String pwd) {
        QueryWrapper<Vendor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        queryWrapper.eq("pwd", pwd);
        return getOne(queryWrapper);
    }
//
    @Override
    public Vendor register(String name, String phone,String password) {
        Vendor vendor = vendorDao.findByPhone(phone);
        if (vendor != null) {
            return null; // 商家已经注册过了
        }
        vendor = new Vendor();
        vendor.setName(name);
        vendor.setPhone(phone);
        vendor.setPwd(password);
        vendorDao.insert(vendor);
        return vendor; // 注册成功，返回Vendor实例
    }
    @Override
    public IPage<Vendor> testSelectPage(int current, int size, Optional<Boolean> isAsc, Optional<String> sortField) {
        // 创建一个 Page 对象，指定当前页码和每页记录数
        Page<Vendor> page = new Page<>(current, size);
        // 创建一个 QueryWrapper 对象
        QueryWrapper<Vendor> wrapper = new QueryWrapper<>();
        // 判断是否传入了 isAsc 参数
        if (isAsc.isPresent()) {
            if (isAsc.get()) {
                wrapper.orderByAsc(sortField.orElse("")); // 如果传入了 sortField 参数，则按照 sortField 升序排序，否则不排序
            } else {
                wrapper.orderByDesc(sortField.orElse("")); // 如果传入了 sortField 参数，则按照 sortField 降序排序，否则不排序
            }
        }
        // 调用 ProductDao 的 selectPage 方法进行分页查询
        IPage<Vendor> iPage = vendorDao.selectPage(page, wrapper);
        // 打印总页数
        System.out.println("总页数: " + iPage.getPages());
        // 打印总记录数
        System.out.println("总记录数: " + iPage.getTotal());
        // 打印当前页上的记录
        System.out.println("记录: " + iPage.getRecords());
        return iPage;
    }
    }



