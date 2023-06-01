package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.dto.ProductStarDTO;
import com.example.explor_gastro.entity.Star;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收藏表(Star)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 11:38:10
 */
@Mapper
public interface StarDao extends BaseMapper<Star> {
    @Select("SELECT p.product_name, p.img, u.name FROM product p INNER JOIN star s ON p.product_id=s.product_id INNER JOIN user u ON s.user_id=u.user_id WHERE s.user_id=#{userId}")
    List<ProductStarDTO> selectProductStarDTOsByUserId(Integer userId);
}

