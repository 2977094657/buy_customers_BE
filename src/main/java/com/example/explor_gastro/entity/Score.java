package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 商品评分表(Score)表实体类
 *
 * @author makejava
 * @since 2023-05-18 15:01:47
 */
@Data
public class Score extends Model<Score> {
    @TableId(type = IdType.AUTO)
    //评分id主键自增
    private Integer id;
    //用户id
    private Integer userId;
    //用户评分
    private Integer score;
    //商品id
    private Integer productId;
}

