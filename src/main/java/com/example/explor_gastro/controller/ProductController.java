package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

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
     * 分页查询所有数据
     *
     * @param current 所在页面
     * @param size 每页显示数据
     * @return 所有数据
     */
    @GetMapping
    @Operation(summary = "分页查询所有商品")
    public IPage<Product> page(int current, int size,boolean isAsc,String sortField){return productService.testSelectPage(current,size,isAsc,sortField);}


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.productService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param product 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody Product product) {
        return success(this.productService.save(product));
    }

    /**
     * 修改数据
     *
     * @param product 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody Product product) {
        return success(this.productService.updateById(product));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.productService.removeByIds(idList));
    }
}

