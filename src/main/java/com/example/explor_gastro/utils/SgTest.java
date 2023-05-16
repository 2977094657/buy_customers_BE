package com.example.explor_gastro.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.explor_gastro.dao.OrderDao;
import com.example.explor_gastro.entity.Orders;

import javax.annotation.Resource;

public class SgTest {
    @Resource
    OrderDao orderDao;
    public long test(){
        // 创建SnowflakeIdGenerator对象,设置工作机器ID为1,数据中心ID为1
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        long id = generator.nextId();
        Orders orderLong = orderDao.selectOne(new QueryWrapper<Orders>().eq("orderLong", id));
        if (orderLong == null){
            return id;
        }
        test();
        return 0;
    }
}