package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.buy_customers.common.utils.Response;
import com.buy_customers.dto.ProductStarDTO;
import com.buy_customers.entity.Address;
import com.buy_customers.entity.Product;
import com.buy_customers.entity.Star;
import com.buy_customers.service.ProductService;
import com.buy_customers.service.StarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 收藏表(Star)表控制层
 *
 * @author makejava
 * @since 2023-05-08 11:38:10
 */
@RestController
@RequestMapping("star")
public class StarController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private StarService starService;

    @Resource
    private ProductService productService;

    /**
     * 新增数据
     *
     * @return 新增结果
     */
    @PostMapping("staradd")
    public R insert(@RequestBody Map<String, Integer> params) {
        try {
            // 获取用户 ID 和商品 ID
            Integer userId = params.get("userId");
            Integer productId = params.get("productId");

            // 创建查询条件
            QueryWrapper<Star> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("product_id", productId);

            // 查询数据库中是否存在对应的收藏记录
            Star star = this.starService.getOne(queryWrapper);
            if (star != null) {
                // 如果存在收藏记录，那么就删除这个记录
                boolean success = this.starService.removeById(star.getId());
                if (success) {
                    Integer star1 = productService.getById(productId).getStar();
                    star1--;
                    Product product = new Product();
                    product.setStar(star1);
                    UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("product_id", productId).set("star", star1);
                    productService.update(updateWrapper);
                    return success("用户取消收藏成功");
                } else {
                    return failed("用户取消收藏失败");
                }
            } else {
                // 如果不存在收藏记录，那么就添加一个新的记录
                star = new Star();
                star.setUserId(userId);
                star.setProductId(productId);
                boolean success = this.starService.save(star);
                if (success) {
                    Integer star1 = productService.getById(productId).getStar();
                    star1++;
                    UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("product_id", productId).set("star", star1);
                    productService.update(updateWrapper);
                    return success("用户收藏成功");
                } else {
                    return failed("用户收藏失败");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return failed("操作失败");
        }
    }


    @GetMapping("all")
    public List<ProductStarDTO> getProductStarDTOsByUserId(@RequestParam Integer userId) {
        return starService.getProductStarDTOsByUserId(userId);
    }

    @GetMapping("select")
    public List<Star> select(@RequestParam Integer userId) {
        return starService.list(new QueryWrapper<Star>().eq("user_id", userId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<?>> deleteCartItem(@RequestParam Integer id) {
        Response<List<Address>> response = new Response<>();
        try {
            Integer productId = starService.getById(id).getProductId();
            Integer star1 = productService.getById(productId).getStar();
            System.out.println(star1);
            star1--;
            Product product = new Product();
            product.setStar(star1);
            QueryWrapper<Product> wrapper = new QueryWrapper<>();
            wrapper.eq("product_id",productId);
            productService.update(product,wrapper);
            starService.deleteCartItem(id);

            response.setCode(200);
            response.setMsg("删除成功");
        } catch (Exception e) {
            response.setCode(400);
            response.setMsg("收藏不存在");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/deleteAll")
    public Map<String,Object> deleteAllCartItem(@RequestParam List<Integer> id){
        for (Integer ids : id) {
            Integer productId = starService.getById(ids).getProductId();
            Integer star1 = productService.getById(productId).getStar();
            star1--;
            Product product = new Product();
            product.setStar(star1);
            QueryWrapper<Product> wrapper = new QueryWrapper<>();
            wrapper.eq("product_id",productId);
            productService.update(product,wrapper);
        }
        return starService.deleteCartItemByIds(id);
    }
}

