package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.ProductComments;
import com.example.explor_gastro.service.ProductCommentsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 商品评价表(ProductComments)表控制层
 *
 * @author makejava
 * @since 2023-05-09 09:43:45
 */
@RestController
@RequestMapping("productComments")
public class ProductCommentsController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ProductCommentsService productCommentsService;

    /**
     * 分页查询所有数据
     *
     * @param page            分页对象
     * @param productComments 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<ProductComments> page, ProductComments productComments) {
        return success(this.productCommentsService.page(page, new QueryWrapper<>(productComments)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.productCommentsService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param productComments 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody ProductComments productComments) {
        return success(this.productCommentsService.save(productComments));
    }

    /**
     * 修改数据
     *
     * @param productComments 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody ProductComments productComments) {
        return success(this.productCommentsService.updateById(productComments));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.productCommentsService.removeByIds(idList));
    }
}

