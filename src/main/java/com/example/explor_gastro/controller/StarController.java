package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.Star;
import com.example.explor_gastro.service.StarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "收藏管理")
public class StarController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private StarService starService;

    /**
     *
     * @param page
     * @param starQuery
     * @return
     */
    @GetMapping("/select")
    @Operation(summary  =  "收藏的分页查")
    public  R  selectAll(@RequestParam("page")  Page<Star>  page,  @RequestBody  Star  starQuery)  {
        return  success(this.starService.page(page,  new  QueryWrapper<>(starQuery)));
    }


//    @GetMapping("{userid}")
//    @Operation(summary  =  "查询收藏")
//    public R selectOne(@PathVariable Serializable userid) {
//        return success(this.starService.getById(userid));
//    }

    /**
     *
     * @param userid
     * @return
     */
    @GetMapping("/stars/{userid}")
    @Operation(summary  =  "查询用户收藏")
    public  R  selectByUserId(@PathVariable  Long  userid)  {
        List<Star>  starList  =  this.starService.list(new  QueryWrapper<Star>().eq("user_id",  userid));
        return  success(starList);
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

