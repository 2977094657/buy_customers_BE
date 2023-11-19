package com.buy_customers.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buy_customers.entity.CartItem;
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

