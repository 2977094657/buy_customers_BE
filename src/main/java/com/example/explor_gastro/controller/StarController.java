package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.explor_gastro.common.utils.Response;
import com.example.explor_gastro.dto.ProductStarDTO;
import com.example.explor_gastro.entity.Address;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.entity.Star;
import com.example.explor_gastro.service.ProductService;
import com.example.explor_gastro.service.StarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "用户的收藏")
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
    @Operation(summary  =  "新增用户的收藏")
    public R insert(@RequestBody Map<String, Integer> params) {
        try {
            //  创建Star对象并设置其属性
            Star star = new Star();
            star.setUserId(params.get("userId"));
            star.setProductId(params.get("productId"));
            //  将Star对象保存到数据库中
            boolean success = this.starService.save(star);
            if (success) {
                Integer star1 = productService.getById(params.get("productId")).getStar();
                star1++;
                Product product = new Product();
                product.setStar(star1);
                QueryWrapper<Product> wrapper = new QueryWrapper<>();
                wrapper.eq("product_id",params.get("productId"));
                productService.update(product,wrapper);
                return success("用户收藏成功");
            } else {
                return failed("用户收藏失败");
            }
        } catch (Exception e) {
            return failed("用户已收藏此商品");
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
    @Operation(summary = "删除单个收藏")
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
    @Operation(summary = "批量删除")
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

