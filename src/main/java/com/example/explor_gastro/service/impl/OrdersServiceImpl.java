package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.OrdersDao;
import com.example.explor_gastro.entity.Orders;
import com.example.explor_gastro.service.OrdersService;
import org.springframework.stereotype.Service;

/**
 * 订单表(Orders)表服务实现类
 *
 * @author makejava
 * @since 2023-10-29 15:31:07
 */
@Service("orderService")
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, Orders> implements OrdersService {

}

