package com.buy_customers.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buy_customers.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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
    @Select("SELECT * FROM product WHERE category LIKE CONCAT('%', #{category}, '%') LIMIT #{offset}, #{limit}")
    List<Product> selectByCategory(Map<String, Object> params);
}

