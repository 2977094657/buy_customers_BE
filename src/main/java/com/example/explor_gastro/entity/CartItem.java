package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
 * 购物车(Cartitem)表实体类
 *
 * @author makejava
 * @since 2023-07-26 22:29:26
 */
@Data
public class CartItem extends Model<CartItem> {
    @TableId(type = IdType.AUTO)
    //购物车id，主键自增
    private Integer id;
    //用户id
    private Integer userId;
    //商品id
    private Integer productId;
    //商品数量
    private Integer number;
    //加入时间
    private Date time;


    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}

