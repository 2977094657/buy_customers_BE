package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.CartitemDao;
import com.buy_customers.dao.ProductDao;
import com.buy_customers.entity.CartItem;
import com.buy_customers.entity.Product;
import com.buy_customers.service.CartitemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 购物车(Cart_item)表服务实现类
 *
 * @author makejava
 * @since 2023-07-26 22:29:26
 */
@Service("cart_itemService")
public class CartitemServiceImpl extends ServiceImpl<CartitemDao, CartItem> implements CartitemService {
}


