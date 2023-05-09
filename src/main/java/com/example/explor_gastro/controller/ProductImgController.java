package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.ProductImg;
import com.example.explor_gastro.service.ProductImgService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 商品图片表(ProductImg)表控制层
 *
 * @author makejava
 * @since 2023-05-09 08:47:22
 */
@RestController
@RequestMapping("productImg")
public class ProductImgController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ProductImgService productImgService;

    /**
     * 分页查询所有数据
     *
     * @param page       分页对象
     * @param productImg 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<ProductImg> page, ProductImg productImg) {
        return success(this.productImgService.page(page, new QueryWrapper<>(productImg)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.productImgService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param productImg 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody ProductImg productImg) {
        return success(this.productImgService.save(productImg));
    }

    /**
     * 修改数据
     *
     * @param productImg 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody ProductImg productImg) {
        return success(this.productImgService.updateById(productImg));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.productImgService.removeByIds(idList));
    }
}

