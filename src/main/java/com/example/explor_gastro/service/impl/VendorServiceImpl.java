package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.VendorDao;
import com.example.explor_gastro.entity.Vendor;
import com.example.explor_gastro.service.VendorService;
import org.springframework.stereotype.Service;

/**
 * 商家表(Vendor)表服务实现类
 *
 * @author makejava
 * @since 2023-05-08 11:55:50
 */
@Service("vendorService")
public class VendorServiceImpl extends ServiceImpl<VendorDao, Vendor> implements VendorService {

}

