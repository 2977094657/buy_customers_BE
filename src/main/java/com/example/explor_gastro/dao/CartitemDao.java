package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车(Cartitem)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-26 22:29:26
 */
@Mapper
public interface CartitemDao extends BaseMapper<CartItem> {

}

