package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品评分表(Score)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-18 15:01:47
 */
@Mapper
public interface ScoreDao extends BaseMapper<Score> {
    /**
     * 根据用户ID和商品ID获取评分实体。
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 评分实体
     */
    @Select("SELECT * FROM score WHERE user_id = #{userId} AND product_id = #{productId}")
    Score getScoreByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    /**
     * 根据商品ID获取所有评分的列表。
     *
     * @param productId 商品ID
     * @return 评分列表
     */
    @Select("SELECT * FROM score WHERE product_id = #{productId}")
    List<Score> getScoresByProductId(Integer productId);
}

