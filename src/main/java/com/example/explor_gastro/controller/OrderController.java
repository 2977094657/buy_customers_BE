package com.example.explor_gastro.controller;//package com.example.explor_gastro.controller;
//
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.api.ApiController;
//import com.baomidou.mybatisplus.extension.api.R;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.example.explor_gastro.entity.Order;
//import com.example.explor_gastro.service.OrderService;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import java.io.Serializable;
//import java.util.List;
//
///**
// * 订单表(Order)表控制层
// *
// * @author makejava
// * @since 2023-05-09 08:53:40
// */
//@RestController
//@RequestMapping("order")
//public class OrderController extends ApiController {
//    /**
//     * 服务对象
//     */
//    @Resource
//    private OrderService orderService;
////“getOrderById” 的意思是根据订单ID获取订单信息。
//    @GetMapping("/{orderId}")
//    public Order getOrderById(@PathVariable Integer orderId) {
//        return orderService.getById(orderId);
//    }
////    createOrder” 的意思是创建订单
//    @PostMapping("/")
//    public boolean createOrder(@RequestBody Order order) {
//        return orderService.createOrder(order);
//    }
////“updateOrder” 的意思是更新订单
//    @PutMapping("/")
//    public boolean updateOrder(@RequestBody Order order){
//        return orderService.updateOrder(order);
//    }
//
////“removeByIds” 的意思是根据ID批量删除数据
//@DeleteMapping("/{orderId}")
//public boolean deleteOrder(@PathVariable Integer orderId) {
//    return orderService.removeById(orderId);
//}
//}
//
import com.example.explor_gastro.entity.Orders;
import com.example.explor_gastro.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    public Orders getOrderById(@PathVariable Integer orderId) {
        return orderService.getOrderById(orderId);
    }

    @PostMapping("/orders")
    public Orders createOrder(
            //商家ID(vendorId)
            @RequestParam Integer vendorId,
            //订单号(orderLong)
            @RequestParam String orderLong,
            //用户ID(userId)
            @RequestParam Integer userId,
            //下单时间(data)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date data,
            //下单地址(address)
            @RequestParam String address,
            //订单总计价格(price)
            @RequestParam Integer price,
            //商品ID(productId)
            @RequestParam Integer productId,
            //订单备注(notes)
            @RequestParam String notes,
            //订单类型(type)
            @RequestParam String type) {

        // 判断订单类型是否正确
        if (!"外卖".equals(type) && !"堂食".equals(type)) {
            throw new IllegalArgumentException("订单类型不正确");
        }

        // 创建新订单
        Orders orders = new Orders();
        orders.setVendorId(vendorId);
        orders.setOrderLong(orderLong);
        orders.setUserId(userId);
        orders.setData(data);
        orders.setAddress(address);
        orders.setPrice(price);
        orders.setProductId(productId);
        orders.setNotes(notes);
        orders.setType(type);

        // 将订单保存到数据库
        orderService.save(orders);

        return orders;
    }



    @PutMapping("/{orderId}")
    public boolean updateOrder(
            //订单ID(orderId)
            @PathVariable Integer orderId,
            //商家ID(vendorId)
            @RequestParam Integer vendorId,
            //订单号(orderLong)
            @RequestParam String orderLong,
            //用户ID(userId)
            @RequestParam Integer userId,
            //下单时间(data)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date data,
            //下单地址(address)
            @RequestParam String address,
            //订单总计价格(price)
            @RequestParam Integer price,
            //商品ID(productId)
            @RequestParam Integer productId,
            //订单备注(notes)
            @RequestParam String notes,
            //订单类型(type)
            @RequestParam String type) {
        Orders orders = orderService.getById(orderId);
        orders.setVendorId(vendorId);
        orders.setOrderLong(orderLong);
        orders.setUserId(userId);
        orders.setData(data);
        orders.setAddress(address);
        orders.setPrice(price);
        orders.setProductId(productId);
        orders.setNotes(notes);

        if ("外卖".equals(type) || "堂食".equals(type)) {
            orders.setType(type);
        } else {
            throw new IllegalArgumentException("订单类型不正确");
        }

        return orderService.updateById(orders);
    }

    @DeleteMapping("/{orderId}")
    public boolean deleteOrder(@PathVariable Integer orderId) {
        return orderService.deleteOrder(orderId);
    }
}