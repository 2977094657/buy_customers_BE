package com.example.explor_gastro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.explor_gastro.entity.CartItem;

import java.util.List;
import java.util.Map;

/**
 * 购物车(Cartitem)表服务接口
 *
 * @author makejava
 * @since 2023-07-26 22:29:26
 */
public interface CartitemService extends IService<CartItem> {
    Map<String, Object> addToCart(Integer userId, Integer productId, Integer quantity);
    List<CartItem> getCartItems(Integer userId);
    Map<String, Object> updateCartItem(Integer id, Integer quantity);
    Map<String, Object> deleteCartItem(Integer id);
    Map<String, Object> deleteCartItemByIds(List<Integer> id);
}

