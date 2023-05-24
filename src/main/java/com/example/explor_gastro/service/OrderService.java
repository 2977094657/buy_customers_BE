package com.example.explor_gastro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.explor_gastro.entity.Orders;

/**
 * 订单表(Order)表服务接口
 *
 * @author makejava
 * @since 2023-05-09 08:53:40
 */
public interface OrderService extends IService<Orders> {
    Orders getOrderById(Integer orderId);

    boolean createOrder(Orders orders);

}

