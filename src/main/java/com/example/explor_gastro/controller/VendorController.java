package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.Vendor;
import com.example.explor_gastro.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "商家功能")
public class VendorController extends ApiController {
    //
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
    @GetMapping("vendorInquire")
    @Operation(summary = "所有商家")
    @Parameters({
            @Parameter(name = "page", description = "分页对象"),
            @Parameter(name = "vendor", description = "查询实体"),
    })
    public R selectAll(
            @RequestParam(name = "page",defaultValue = "1") int page1,
            @RequestParam(name = "vendor",defaultValue = "1") int vendor1,
            Page<Vendor> page, Vendor vendor) {

        return success(this.vendorService.page(page, new QueryWrapper<>(vendor)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}vendorid")
    @Operation(summary = "商家详情")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    public R selectOne(
            @RequestParam(name = "id",defaultValue = "1") int id1,
            @PathVariable Serializable id) {
        return success(this.vendorService.getById(id));
    }
}