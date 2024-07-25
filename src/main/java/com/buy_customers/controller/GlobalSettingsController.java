package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.entity.GlobalSettings;
import com.buy_customers.service.GlobalSettingsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @GetMapping("selectOne")
    public ResultData<GlobalSettings> selectOne(@RequestParam String name) {
        QueryWrapper<GlobalSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        return ResultData.success(globalSettingsService.getOne(queryWrapper));
    }

    /**
     * 修改数据
     *
     * @param globalSettings 实体对象
     * @return 修改结果
     */
    @PutMapping("sidebar")
    public ResultData<Boolean> update(@RequestBody GlobalSettings globalSettings) {
        return ResultData.success(this.globalSettingsService.updateById(globalSettings));
    }
}

