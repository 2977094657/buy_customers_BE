package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表(Orders)表实体类
 *
 * @author makejava
 * @since 2023-10-29 15:31:07
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 如果查出为null的字段则不显示，可以单独给某字段加
public class Orders extends Model<Orders> {
    @TableId(type = IdType.AUTO)
    // 订单id，主键自增
    private Integer orderId;
    // 商家名
    private String vendorName;
    // 订单号
    private String orderLong;
    // 用户id
    private Integer userId;
    // 收货地址
    private String address;
    // 创建时间
    private Date createDate;
    // 付款时间
    private Date payDate;
    // 发货时间
    private Date sendDate;
    // 收货时间
    private Date receiveDate;
    // 订单总计价格
    private BigDecimal price;
    // 商品id
    private String productId;
    // 商品数量
    private String productNumber;
    // 订单备注
    private String notes;
    // 付款方式,默认未支付
    private String payMethod;
    // 订单状态,默认待付款
    private String state;
    // 收货人
    private String consignee;
    // 收货人手机号
    private String phone;
}

