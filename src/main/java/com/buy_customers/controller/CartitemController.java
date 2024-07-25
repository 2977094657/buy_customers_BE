package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.entity.CartItem;
import com.buy_customers.entity.Product;
import com.buy_customers.service.CartitemService;
import com.buy_customers.service.ProductService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

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
    @Resource
    CartitemService cartitemService;
    @Resource
    ProductService productService;

    @PostMapping("/add")
    public ResultData<String> addToCart(@RequestParam Integer userId, @RequestParam Integer productId, @RequestParam Integer quantity) {
        // 检查商品数量是否大于0
        if (quantity <= 0) {
            return ResultData.fail(400, "添加的商品数量必须大于0");
        }

        // 获取当前购物车商品总数量
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("user_id", userId);
        Collection<CartItem> cartItems = cartitemService.listByMap(columnMap);
        int currentNumber = cartItems.stream().mapToInt(CartItem::getNumber).sum();

        // 检查购物车中是否已有该商品
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("user_id", userId);
        queryMap.put("product_id", productId);
        CartItem existingCartItem = cartitemService.listByMap(queryMap).stream().findFirst().orElse(null);

        int existingCartItemNumber = (existingCartItem != null) ? existingCartItem.getNumber() : 0;
        int newQuantity = existingCartItemNumber + quantity;

        // 检查购物车商品总数是否超过上限
        if (currentNumber - existingCartItemNumber + newQuantity > 50) {
            return ResultData.fail(400, "您的购物车商品总数已满，请先清理后继续加入购物车");
        }

        boolean isSuccess;
        CartItem newCartItem;
        if (existingCartItem != null) {
            existingCartItem.setNumber(newQuantity);
            isSuccess = cartitemService.updateById(existingCartItem);

            // 更新产品星级
            if (isSuccess) {
                Product product = productService.getById(productId);
                if (product != null) {
                    product.setStar(product.getStar() + 1);
                    productService.updateById(product);
                }
            }
            return ResultData.success("购物车中商品数量已更新");
        } else {
            newCartItem = new CartItem();
            newCartItem.setUserId(userId);
            newCartItem.setProductId(productId);
            newCartItem.setNumber(quantity);
            newCartItem.setTime(new Date());
            isSuccess = cartitemService.save(newCartItem);
            if (isSuccess){
                return ResultData.success("添加成功");
            }
            return ResultData.fail(400,"添加失败");
        }
    }

    @GetMapping("/list")
    public ResultData<List<CartItem>> getCartItems(@RequestParam Integer userId) {
        return ResultData.success(cartitemService.list(new QueryWrapper<CartItem>().eq("user_id", userId)));
    }

    @PutMapping("/update")
    public ResultData<String> updateCartItem(@RequestParam Integer id, @RequestParam Integer quantity) {
        if (quantity <= 0) {
            return ResultData.fail(400,"商品数量必须大于0");
        }

        CartItem cartitem = cartitemService.getOne(new QueryWrapper<CartItem>()
                .eq("id", id)
        );
        if (cartitem == null) {
            return ResultData.fail(400,"购物车商品不存在");
        }

        // 检查加入新商品后购物车的总数量是否会超过50
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("user_id", cartitem.getUserId());
        Collection<CartItem> CartItems = cartitemService.listByMap(columnMap);
        Integer totalNumber = 0;
        for (CartItem item : CartItems) {
            if (!item.getId().equals(id)) { // 不计算当前要更新的商品
                totalNumber += item.getNumber();
            }
        }

        if (totalNumber + quantity > 50) {
            return ResultData.fail(400,"超出购物车最大限制，请将所有商品数量控制在50以内");
        }

        // 更新商品数量
        cartitem.setNumber(quantity);
        boolean updateSuccess = cartitemService.updateById(cartitem);
        if (updateSuccess) {
            return ResultData.success("购物车中商品数量已更新");
        } else {
            return ResultData.fail(400,"购物车商品数量更新失败");
        }
    }

    @DeleteMapping("/deleteAll")
    public ResultData<String> deleteAllCartItem(@RequestParam List<Integer> id) {
        if (id.isEmpty()) {
            return ResultData.fail(400, "购物车商品ID列表不能为空");
        }
        boolean success = cartitemService.removeByIds(id);
        if (success) {
            return ResultData.success("购物车商品删除成功");
        } else {
            return ResultData.fail(400, "购物车不存在此商品");
        }
    }
}

