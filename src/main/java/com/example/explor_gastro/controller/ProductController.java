package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 商品表(Product)表控制层
 *
 * @author makejava
 * @since 2023-05-06 20:16:10
 */
@RestController
@RequestMapping("product")
@Tag(name = "商品功能")
public class ProductController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ProductService productService;

    /**
     * @param current   当前所在页面
     * @param size      每页显示数量
     * @param isAsc     是否升序排列，不传或传入空值则不排序
     * @param sortField 根据传入的此字段来排序，不传或传入空值则不排序
     * @return 返回所有数据
     */
    @GetMapping("all")
    @Operation(summary = "分页查询所有商品")
    @Parameters({
            @Parameter(name = "current", description = "所在页面"),
            @Parameter(name = "size", description = "每页显示数据"),
            @Parameter(name = "isAsc", description = "是否升序排列"),
            @Parameter(name = "sortField", description = "根据此参数传入的字段排序")
    })
    public IPage<Product> page(
            @RequestParam(name = "current", required = true) int current,
            @RequestParam(name = "size", required = true) int size,
            @RequestParam(name = "isAsc", required = false) Optional<Boolean> isAsc,
            @RequestParam(name = "sortField", required = false) Optional<String> sortField) {
        return productService.testSelectPage(current, size, isAsc, sortField);
    }


    @Operation(summary = "增加商品")
    @PostMapping("add")
    @Parameters({
            @Parameter(name = "productName", description = "商品名字"),
            @Parameter(name = "name", description = "商家名字，根据商家登陆的账号来传入此参数，不允许商家填入"),
            @Parameter(name = "description", description = "商品介绍"),
            @Parameter(name = "price", description = "价格"),
            @Parameter(name = "category", description = "商品分类，此处应为下拉栏，不允许商家填入，四个分类:主食、小吃、甜品、饮料")
    })
    public R insert(@RequestParam("productName") String productName,
                    @RequestParam("description") String description,
                    @RequestParam("price") Integer price,
                    @RequestParam("category") String category,
                    @RequestParam("name") String name
    ) {
        Product product = new Product(productName, description, price, category,name);
        return success(this.productService.save(product));
    }


    @Operation(summary = "修改商品")
    @PutMapping("update")
    @Parameters({
            @Parameter(name = "productId", description = "商品id,根据此字段修改"),
            @Parameter(name = "productName", description = "商品名字"),
            @Parameter(name = "description", description = "商品介绍"),
            @Parameter(name = "price", description = "价格"),
            @Parameter(name = "category", description = "商品分类，此处应为下拉栏，不允许商家填入，四个分类:主食、小吃、甜品、饮料")
    })
    public R update(
            @RequestParam("productId") Integer productId,
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Integer price,
            @RequestParam("category") String category
    ) {
        boolean result = this.productService.updateProduct(productId, productName, description, price, category);
        if (result) {
            return success("商品修改成功");
        } else {
            return failed("商品修改失败");
        }
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @Operation(summary = "删除商品")
    @PostMapping("delete")
    @Parameters({
            @Parameter(name = "idList", description = "商品id，根据商家登陆的账号来传入此参数，不允许商家填入，根据此参数来确定要删除的商品"),
            })
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.productService.removeByIds(idList));
    }
}

