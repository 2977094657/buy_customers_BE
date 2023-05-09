package com.example.explor_gastro.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 商家评价表(VendorComments)表实体类
 *
 * @author makejava
 * @since 2023-05-08 11:55:50
 */
@SuppressWarnings("serial")
public class VendorComments extends Model<VendorComments> {
    @TableId(type = IdType.AUTO)
    //评论id，主键自增
    private Integer id;
    //用户id
    private Integer userId;
    //评论内容
    private String comments;
    //评论图片
    private String img;
    //评论时间
    private Date time;
    //商家id
    private Integer vendorId;


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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
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

