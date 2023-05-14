package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.ProductComments;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价表(ProductComments)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-09 09:43:45
 */
@Mapper
public interface ProductCommentsDao extends BaseMapper<ProductComments> {

}

