package  com.example.explor_gastro.service;

import  com.baomidou.mybatisplus.extension.service.IService;
import  com.example.explor_gastro.entity.Orders;

/**
 *  订单表(Order)表服务接口
 */
public  interface  OrderService  extends  IService<Orders>  {

    /**
     *  创建订单
     *
     *  @param  orders  订单对象
     *  @return  是否创建成功
     */
    public  boolean  createOrder(Orders  orders);

    /***
     *  根据订单号获取订单对象
     *
     *  @param  orderLong  订单号
     *  @return  订单对象
     */
    public  Orders  getOrderByOrderLong(String  orderLong);
}
