package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品图片表(ProductImg)表实体类
 *
 * @author makejava
 * @since 2023-05-09 09:32:26
 */
@Data
@SuppressWarnings("serial")
public class ProductImg extends Model<ProductImg> {
    //商品图片id，主键自增
    @TableId(type = IdType.AUTO)
    private Integer imgId;
    //商品图片
    private String img;
    //商品id
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

