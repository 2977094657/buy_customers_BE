package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表(Order)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-09 08:53:40
 */
@Mapper
public interface OrderDao extends BaseMapper<Order> {

}

