package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表(Orders)表数据库访问层
 *
 * @author makejava
 * @since 2023-10-29 15:31:07
 */
@Mapper
public interface OrdersDao extends BaseMapper<Orders> {

}

