package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buy_customers.entity.GlobalSettings;
import com.buy_customers.service.GlobalSettingsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 网站全局设置(GlobalSettings)表控制层
 *
 * @author makejava
 * @since 2024-01-05 19:33:42
 */
@RestController
@RequestMapping("globalSettings")
public class GlobalSettingsController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private GlobalSettingsService globalSettingsService;

    /**
     * 分页查询所有数据
     *
     * @param page           分页对象
     * @param globalSettings 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<GlobalSettings> page, GlobalSettings globalSettings) {
        return success(this.globalSettingsService.page(page, new QueryWrapper<>(globalSettings)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public R selectOne(@RequestParam String id) {
        return success(this.globalSettingsService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param globalSettings 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody GlobalSettings globalSettings) {
        return success(this.globalSettingsService.save(globalSettings));
    }

    /**
     * 修改数据
     *
     * @param globalSettings 实体对象
     * @return 修改结果
     */
    @PutMapping("sidebar")
    public R update(@RequestBody GlobalSettings globalSettings) {
        return success(this.globalSettingsService.updateById(globalSettings));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.globalSettingsService.removeByIds(idList));
    }
}

