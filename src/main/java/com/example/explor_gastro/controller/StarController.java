package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.Star;
import com.example.explor_gastro.service.StarService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

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

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param star 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<Star> page, Star star) {
        return success(this.starService.page(page, new QueryWrapper<>(star)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.starService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param star 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody Star star) {
        return success(this.starService.save(star));
    }

    /**
     * 修改数据
     *
     * @param star 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody Star star) {
        return success(this.starService.updateById(star));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.starService.removeByIds(idList));
    }
}

