package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.Product;
import org.apache.ibatis.annotations.*;

/**
 * 商品表(Product)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-06 20:16:11
 */
@Mapper
public interface ProductDao extends BaseMapper<Product> {
    @Update("UPDATE product SET img=#{img} WHERE product_id=#{productId}")
    int updateImgByProductId(@Param("productId") Integer productId, @Param("img") String img);
}

