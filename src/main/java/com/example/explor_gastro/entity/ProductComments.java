package com.example.explor_gastro.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 商品评价表(ProductComments)表实体类
 *
 * @author makejava
 * @since 2023-05-09 09:43:45
 */
@SuppressWarnings("serial")
public class ProductComments extends Model<ProductComments> {
    @TableId(type = IdType.AUTO)
    //评论id，主键自增
    private Integer id;
    //用户id
    private Integer userId;
    //评论内容
    private String comments;
    //图片id
    private String imgId;
    //评论时间
    private Date time;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

