package com.example.explor_gastro.entity;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 订单表(Order)表实体类
 *
 * @author makejava
 * @since 2023-05-09 08:53:40
 */
@SuppressWarnings("serial")
public class Order extends Model<Order> {
    //订单id，主键自增
    private Integer orderId;
    //商家id，主键自增
    private Integer vendorId;
    //订单号
    private String orderLong;
    //用户id
    private Integer userId;
    //下单时间
    private Date data;
    //下单地址
    private String address;
    //订单总计价格
    private Integer price;
    //商品id
    private Integer productId;
    //订单备注
    private String notes;
    //订单类型
    private String type;


    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getOrderLong() {
        return orderLong;
    }

    public void setOrderLong(String orderLong) {
        this.orderLong = orderLong;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.orderId;
    }
}

