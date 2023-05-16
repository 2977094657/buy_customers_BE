package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.dao.VendorDao;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.entity.Vendor;
import com.example.explor_gastro.service.VendorService;
import com.example.explor_gastro.utils.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;

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
    }



