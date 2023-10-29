package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收货地址(Address)表实体类
 *
 * @author makejava
 * @since 2023-08-25 19:15:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Address extends Model<Address> {
    //地址id,主键自增
    @TableId(type = IdType.AUTO)
    private Integer id;
    //用户id
    private Integer userId;
    //收货人
    private String consignee;
    //所在地区
    private String area;
    //详细地址
    private String fullAddress;
    //手机号
    private String phone;
    //是否设置当前地址为默认地址，初始为0，1是0否
    private Integer defaultOperate;
}

