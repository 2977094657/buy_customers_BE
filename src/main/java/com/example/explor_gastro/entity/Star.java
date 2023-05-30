package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 收藏表(Star)表实体类
 *
 * @author makejava
 * @since 2023-05-08 11:38:10
 */
@Data
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL) // 如果查处为null的字段则不显示，可以单独给某字段加
public class Star extends Model<Star> {
    @TableId(type = IdType.AUTO)
    //收藏id，主键自增
    private Integer id;
    //用户id
    private Integer userId;
    //商品id
    private Integer productId;

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

