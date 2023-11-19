package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.CartitemDao;
import com.buy_customers.entity.CartItem;
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
    @Resource
    private CartitemDao cartitemDao;
    @Override
    public Map<String, Object> addToCart(Integer userId, Integer productId, Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        if (quantity <= 0) {
            response.put("status", 400);
            response.put("message", "添加的商品数量必须大于0");
            response.put("data", null);
            return response;
        }

        Integer currentNumber = getCurrentCartNumber(userId);
        CartItem existingCartItem = getCartItem(userId, productId);
        int newQuantity = (existingCartItem != null) ? existingCartItem.getNumber() + quantity : quantity;

        if (currentNumber - (existingCartItem != null ? existingCartItem.getNumber() : 0) + newQuantity > 50) {
            response.put("status", 200);
            response.put("message", "您的购物车商品总数已满，请先清理后继续加入购物车");
            response.put("data", null);
            return response;
        }

        if (existingCartItem != null) {
            existingCartItem.setNumber(newQuantity);
            return updateOrAddItem(existingCartItem, "购物车中商品数量已更新", "购物车商品数量更新失败");
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUserId(userId);
            newCartItem.setProductId(productId);
            newCartItem.setNumber(quantity);
            newCartItem.setTime(new Date());
            return updateOrAddItem(newCartItem, "添加成功", "添加失败");
        }
    }

    private Integer getCurrentCartNumber(Integer userId) {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("user_id", userId);
        List<CartItem> CartItems = cartitemDao.selectByMap(columnMap);
        return CartItems.stream().mapToInt(CartItem::getNumber).sum();
    }

    private CartItem getCartItem(Integer userId, Integer productId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("user_id", userId);
        queryMap.put("product_id", productId);
        return cartitemDao.selectByMap(queryMap).stream().findFirst().orElse(null);
    }

    private Map<String, Object> updateOrAddItem(CartItem item, String successMessage, String failureMessage) {
        Map<String, Object> response = new HashMap<>();
        boolean isSuccess = (item.getId() == null) ? this.save(item) : this.updateById(item);
        response.put("status", isSuccess ? 200 : 400);
        response.put("message", isSuccess ? successMessage : failureMessage);
        response.put("data", isSuccess ? item : null);
        return response;
    }



    @Override
    public List<CartItem> getCartItems(Integer userId) {
        return this.list(new QueryWrapper<CartItem>().eq("user_id", userId));
    }

    @Override
    public Map<String, Object> updateCartItem(Integer id, Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        if (quantity <= 0) {
            response.put("status", 400);
            response.put("message", "商品数量必须大于0");
            response.put("data", null);
            return response;
        }

        CartItem cartitem = this.getOne(new QueryWrapper<CartItem>()
                .eq("id", id)
        );
        if (cartitem == null) {
            response.put("status", 400);
            response.put("message", "购物车商品不存在");
            response.put("data", null);
            return response;
        }

        // 检查加入新商品后购物车的总数量是否会超过50
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("user_id", cartitem.getUserId());
        List<CartItem> CartItems = cartitemDao.selectByMap(columnMap);
        Integer totalNumber = 0;
        for (CartItem item : CartItems) {
            if (!item.getId().equals(id)) { // 不计算当前要更新的商品
                totalNumber += item.getNumber();
            }
        }

        if (totalNumber + quantity > 50) {
            response.put("status", 200);
            response.put("message", "超出购物车最大限制，请将所有商品数量控制在50以内");
            response.put("data", null);
            return response;
        }

        // 更新商品数量
        cartitem.setNumber(quantity);
        boolean updateSuccess = this.updateById(cartitem);
        if (updateSuccess) {
            response.put("status", 200);
            response.put("message", "购物车中商品数量已更新");
            response.put("data", cartitem);
        } else {
            response.put("status", 400);
            response.put("message", "购物车商品数量更新失败");
            response.put("data", null);
        }

        return response;
    }

    @Override
    public Map<String, Object> deleteCartItem(Integer id) {
        Map<String, Object> response = new HashMap<>();
        CartItem cartitem = this.getOne(new QueryWrapper<CartItem>()
                .eq("id", id)
        );
        if (cartitem == null) {
            response.put("status", 400);
            response.put("message", "购物车商品不存在");
            response.put("data", null);
            return response;
        }

        boolean deleteSuccess = this.remove(new QueryWrapper<CartItem>()
                .eq("id", id)
        );

        if (deleteSuccess) {
            response.put("status", 200);
            response.put("message", "购物车中的商品已成功删除");
            response.put("data", null);
        } else {
            response.put("status", 400);
            response.put("message", "购物车商品删除失败");
            response.put("data", null);
        }

        return response;
    }

    @Override
    public Map<String, Object> deleteCartItemByIds(List<Integer> id) {
        Map<String, Object> response = new HashMap<>();
        if (id.isEmpty()) {
            response.put("status", 400);
            response.put("message", "购物车商品ID列表不能为空");
            response.put("data", null);
            return response;
        }
        try {
            int success = cartitemDao.deleteBatchIds(id);
            if (success<=id.size() && success>0) {
                response.put("status", 200);
                response.put("message", "购物车商品删除成功");
                response.put("data", success);
            } else if(success==0){
                response.put("status", 400);
                response.put("message", "购物车不存在此商品");
                response.put("data", success);
            }
            return response;
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "服务器出现异常");
            response.put("data", null);
            return response;
        }
    }
}


