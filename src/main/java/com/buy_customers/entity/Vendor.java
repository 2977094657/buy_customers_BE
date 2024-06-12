package com.buy_customers.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商家表(Vendor)表实体类
 *
 * @author makejava
 * @since 2023-05-08 11:55:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Vendor extends Model<Vendor> {
    @TableId(type = IdType.AUTO)
    //商家id，主键自增
    private Integer vendorId;
    //手机号
    private String phone;
    //商家名
    private String name;
    //商家密码
    private String pwd;
    //注册时间
    private Date signupTime;
    //商家简介
    private String description;
    //营业时间
    private String openingTime;
    //评分范围0-5默认0
    private Double score;
    //总销量
    private Integer totalSales;
}

