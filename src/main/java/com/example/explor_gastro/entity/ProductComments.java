package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * 商品评价表(ProductComments)表实体类
 *
 * @author makejava
 * @since 2023-05-09 09:43:45
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 如果查出为null的字段则不显示，可以单独给某字段加
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
}

