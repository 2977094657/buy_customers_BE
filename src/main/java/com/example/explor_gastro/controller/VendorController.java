package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.Vendor;
import com.example.explor_gastro.service.VendorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 商家表(Vendor)表控制层
 *
 * @author makejava
 * @since 2023-05-08 11:55:49
 */
@RestController
@RequestMapping("vendor")
public class VendorController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private VendorService vendorService;

    /**
     * 分页查询所有数据
     *
     * @param page   分页对象
     * @param vendor 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<Vendor> page, Vendor vendor) {
        return success(this.vendorService.page(page, new QueryWrapper<>(vendor)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.vendorService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param vendor 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody Vendor vendor) {
        return success(this.vendorService.save(vendor));
    }

    /**
     * 修改数据
     *
     * @param vendor 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody Vendor vendor) {
        return success(this.vendorService.updateById(vendor));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.vendorService.removeByIds(idList));
    }
}

