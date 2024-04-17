package com.buy_customers.controller;


import com.buy_customers.entity.CartItem;
import com.buy_customers.service.CartitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车(Cartitem)表控制层
 *
 * @author makejava
 * @since 2023-07-26 22:29:25
 */
@RestController
@RequestMapping("/cart")
@ResponseBody
public class CartitemController {
    private final CartitemService cartitemService;

    @Autowired
    public CartitemController(CartitemService cartitemService) {
        this.cartitemService = cartitemService;
    }

    @PostMapping("/add")
    public Map<String, Object> addToCart(@RequestParam Integer userId, @RequestParam Integer productId, @RequestParam Integer quantity) {
        return cartitemService.addToCart(userId, productId, quantity);
    }

    @GetMapping("/list")
    public List<CartItem> getCartItems(@RequestParam Integer userId) {
        return cartitemService.getCartItems(userId);
    }

    @PutMapping("/update")
    public Map<String, Object> updateCartItem(@RequestParam Integer id, @RequestParam Integer quantity) {
        return cartitemService.updateCartItem(id, quantity);
    }

    @DeleteMapping("/delete")
    public Map<String, Object> deleteCartItem(@RequestParam Integer id) {
        return cartitemService.deleteCartItem(id);
    }

    @DeleteMapping("/deleteAll")
    public Map<String,Object> deleteAllCartItem(@RequestParam List<Integer> id){
        return cartitemService.deleteCartItemByIds(id);
    }
}

