package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 商品图片表(UserImg)表实体类
 *
 * @author makejava
 * @since 2023-05-16 16:45:46
 */
@SuppressWarnings("serial")
public class UserImg extends Model<UserImg> {
    //商品图片id，主键自增
    private Integer imgId;

    private String img;
    //用户ID
    private Integer userId;


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

