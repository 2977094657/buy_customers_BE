package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.ProductImg;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品图片表(ProductImg)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-09 09:32:26
 */
@Mapper
public interface ProductImgDao extends BaseMapper<ProductImg> {
}

