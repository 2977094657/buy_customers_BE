package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 商品表(Product)表实体类
 *
 * @author makejava
 * @since 2023-05-06 20:16:11
 */
@SuppressWarnings("serial")
public class Product extends Model<Product> {
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
    //用户id
    private Integer userId;


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.productId;
    }
}

