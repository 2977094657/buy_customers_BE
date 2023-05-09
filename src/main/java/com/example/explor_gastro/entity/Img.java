package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 评论图片表(Img)表实体类
 *
 * @author makejava
 * @since 2023-05-09 09:43:46
 */
@SuppressWarnings("serial")
public class Img extends Model<Img> {
    //评论图片id，主键自增
    private Integer imgId;
    //图片地址
    private String img;
    //用户id
    private Integer userId;
    //商品id
    private Integer productId;


    public Integer getImgId() {
        return imgId;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
        return this.imgId;
    }
}

