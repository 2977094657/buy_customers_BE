package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.AddressDao;
import com.example.explor_gastro.entity.Address;
import com.example.explor_gastro.service.AddressService;
import org.springframework.stereotype.Service;

/**
 * 收货地址(Address)表服务实现类
 *
 * @author makejava
 * @since 2023-08-25 19:15:04
 */
@Service("addressService")
public class AddressServiceImpl extends ServiceImpl<AddressDao, Address> implements AddressService {
}

