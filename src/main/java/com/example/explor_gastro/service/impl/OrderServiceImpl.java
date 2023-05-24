
package  com.example.explor_gastro.service.impl;

import  com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import  com.example.explor_gastro.dao.OrderDao;
import  com.example.explor_gastro.entity.Orders;
import  com.example.explor_gastro.service.OrderService;
import  org.springframework.stereotype.Service;

/***
 *  订单表(Order)表服务实现类
 *
 *  @author  makejava
 *  @since  2023-05-09  08:53:40
 */
@Service("orderService")
public  class  OrderServiceImpl  extends  ServiceImpl<OrderDao,  Orders>  implements  OrderService  {


    @Override
    public  boolean  createOrder(Orders  orders)  {
        return  save(orders);
    }

    @Override
    public  Orders  getOrderByOrderLong(String  orderLong)  {
        return  baseMapper.getOrderByOrderLong(orderLong);
    }
}