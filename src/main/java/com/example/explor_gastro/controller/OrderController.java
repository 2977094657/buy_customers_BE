package com.example.explor_gastro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.explor_gastro.dao.OrderDao;
import com.example.explor_gastro.entity.Orders;
import com.example.explor_gastro.service.OrderService;
import com.example.explor_gastro.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Resource
    private OrderDao orderDao;

    @GetMapping("/{orderId}")
    public Orders getOrderById(@PathVariable Integer orderId) {
        return orderService.getOrderById(orderId);
    }

    @PostMapping("/{userId},{vendorId},{productId}/orders")
    public Orders createOrder(
            //商家ID(vendorId)
            @PathVariable Integer vendorId,
            //用户ID(userId)
            @PathVariable Integer userId,
            //下单地址(address)
            @RequestParam String address,
            //订单总计价格(price)
            @RequestParam Integer price,
            //商品ID(productId)
            @PathVariable Integer productId,
            //订单备注(notes)
            @RequestParam String notes,
            //订单类型(type)
            @RequestParam String type) {

        // 判断订单类型是否正确
        if (!"外卖".equals(type) && !"堂食".equals(type)) {
            throw new IllegalArgumentException("订单类型不正确");
        }

        String orderLong = String.valueOf(test());
        if ("0".equals(orderLong)){
            test();
        }

        // 创建新订单
        Orders orders = new Orders();
        orders.setVendorId(vendorId);
        orders.setOrderLong(orderLong);
        orders.setUserId(userId);
        orders.setAddress(address);
        orders.setPrice(price);
        orders.setProductId(productId);
        orders.setNotes(notes);
        orders.setType(type);

        // 将订单保存到数据库
        orderService.save(orders);

        return orders;
    }
    public long test(){
        // 创建SnowflakeIdGenerator对象,设置工作机器ID为1,数据中心ID为1
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        long id = generator.nextId();
        Orders orderLong = orderDao.selectOne(new QueryWrapper<Orders>().eq("order_long", id));
        if (orderLong == null){
            return id;
        }
        test();
        return 0;
    }
}