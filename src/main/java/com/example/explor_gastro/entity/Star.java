package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 收藏表(Star)表实体类
 *
 * @author makejava
 * @since 2023-05-08 11:38:10
 */
@SuppressWarnings("serial")
public class Star extends Model<Star> {
    //收藏id，主键自增
    private Integer id;
    //用户id
    private Integer userId;
    //商品id
    private Integer productId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}

