package com.buy_customers.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buy_customers.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址(Address)表数据库访问层
 *
 * @author makejava
 * @since 2023-08-25 19:15:04
 */
@Mapper
public interface AddressDao extends BaseMapper<Address> {

}

