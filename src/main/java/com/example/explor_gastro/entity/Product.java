package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品表(Product)表实体类
 *
 * @author makejava
 * @since 2023-05-06 20:16:11
 */
@Data
@SuppressWarnings("serial")
public class Product extends Model<Product> {
    @TableId(type = IdType.AUTO)
    //商品id，主键自增
    private Integer productId;
    //商品名
    private String productName;
    //销量默认0
    private Integer sales;
    //评分范围0-5默认0
    private Double score;
    //商品收藏数
    private Integer star;
    //商品介绍
    private String description;
    //商家名
    private String name;
    //商品价格
    private Integer price;
    //商品类别,默认主食
    private String category;
    //商品图片
    private String img;
    //评分总人数
    private long totalComments;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.productId;
    }
    public Product(String productName, String description, Integer price, String category, String name) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.name = name;
    }
    public Product() {}
}

