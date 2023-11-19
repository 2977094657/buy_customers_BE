package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.OrdersDao;
import com.buy_customers.entity.Orders;
import com.buy_customers.service.OrdersService;
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

