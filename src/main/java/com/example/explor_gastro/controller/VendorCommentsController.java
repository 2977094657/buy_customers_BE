package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.VendorComments;
import com.example.explor_gastro.service.VendorCommentsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 商家评价表(VendorComments)表控制层
 *
 * @author makejava
 * @since 2023-05-08 11:55:50
 */
@RestController
@RequestMapping("vendorComments")
public class VendorCommentsController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private VendorCommentsService vendorCommentsService;

    /**
     * 分页查询所有数据
     *
     * @param page           分页对象
     * @param vendorComments 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<VendorComments> page, VendorComments vendorComments) {
        return success(this.vendorCommentsService.page(page, new QueryWrapper<>(vendorComments)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.vendorCommentsService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param vendorComments 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody VendorComments vendorComments) {
        return success(this.vendorCommentsService.save(vendorComments));
    }

    /**
     * 修改数据
     *
     * @param vendorComments 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody VendorComments vendorComments) {
        return success(this.vendorCommentsService.updateById(vendorComments));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.vendorCommentsService.removeByIds(idList));
    }
}

