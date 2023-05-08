package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.Star;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏表(Star)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 11:38:10
 */
@Mapper
public interface StarDao extends BaseMapper<Star> {

}

