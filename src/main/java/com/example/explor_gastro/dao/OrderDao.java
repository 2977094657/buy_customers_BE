package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderDao extends BaseMapper<Orders> {

    /**
     * 根据订单号查询订单
     *
     * @param orderLong 订单号
     * @return 查询到的订单，若不存在该订单则返回null
     */
    @Select("SELECT * FROM orders WHERE order_long = #{orderLong}")
    Orders findByOrderLong(String orderLong);

    /**
     * 根据商家id、用户id和订单类型查询订单列表
     *
     * @param vendorId 商家id
     * @param userId 用户id
     * @param type 订单类型
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE vendor_id = #{vendorId} AND user_id = #{userId} AND type = #{type}")
    List<Orders> findByVendorIdAndUserIdAndType(Integer vendorId, Integer userId, String type);

    /**
     * 更新订单总计价格和备注
     *
     * @param orderId 订单id
     * @param price 订单总计价格
     * @param notes 订单备注
     * @return 影响的行数，若为1则表示更新成功
     */
    @Update("UPDATE orders SET price = #{price}, notes = #{notes} WHERE order_id = #{orderId}")
    int updatePriceAndNotesById(Integer orderId, Integer price, String notes);

}