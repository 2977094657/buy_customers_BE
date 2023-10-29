package com.example.explor_gastro.controller;


import com.example.explor_gastro.entity.CartItem;
import com.example.explor_gastro.service.CartitemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "购物车")
public class CartitemController {
    private final CartitemService cartitemService;

    @Autowired
    public CartitemController(CartitemService cartitemService) {
        this.cartitemService = cartitemService;
    }

    @PostMapping("/add")
    @Operation(summary = "往购物车增加商品")
    public Map<String, Object> addToCart(@RequestParam Integer userId, @RequestParam Integer productId, @RequestParam Integer quantity) {
        return cartitemService.addToCart(userId, productId, quantity);
    }

    @GetMapping("/list")
    @Operation(summary = "获取购物车所有商品")
    public List<CartItem> getCartItems(@RequestParam Integer userId) {
        return cartitemService.getCartItems(userId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改购物车某个商品数量")
    public Map<String, Object> updateCartItem(@RequestParam Integer id, @RequestParam Integer quantity) {
        return cartitemService.updateCartItem(id, quantity);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除购物车某个商品")
    public Map<String, Object> deleteCartItem(@RequestParam Integer id) {
        return cartitemService.deleteCartItem(id);
    }

    @DeleteMapping("/deleteAll")
    @Operation(summary = "批量删除")
    public Map<String,Object> deleteAllCartItem(@RequestParam List<Integer> id){
        return cartitemService.deleteCartItemByIds(id);
    }
}

