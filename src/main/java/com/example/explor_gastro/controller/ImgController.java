package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.Img;
import com.example.explor_gastro.service.ImgService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 评论图片表(Img)表控制层
 *
 * @author makejava
 * @since 2023-05-09 09:43:45
 */
@RestController
@RequestMapping("img")
public class ImgController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ImgService imgService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param img  查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<Img> page, Img img) {
        return success(this.imgService.page(page, new QueryWrapper<>(img)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.imgService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param img 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody Img img) {
        return success(this.imgService.save(img));
    }

    /**
     * 修改数据
     *
     * @param img 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody Img img) {
        return success(this.imgService.updateById(img));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.imgService.removeByIds(idList));
    }
}

