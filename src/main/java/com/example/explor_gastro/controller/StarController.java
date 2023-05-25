package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.Star;
import com.example.explor_gastro.service.StarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 收藏表(Star)表控制层
 *
 * @author makejava
 * @since 2023-05-08 11:38:10
 */
@RestController
@RequestMapping("star")
@Tag(name = "用户的收藏")
public class StarController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private StarService starService;



//    @GetMapping("{userid}")
//    @Operation(summary  =  "查询收藏")
//    public R selectOne(@PathVariable Serializable userid) {
//        return success(this.starService.getById(userid));
//    }

//    /**
//     *
//     * @param userid
//     * @return
//     */
//    @GetMapping("/stars/{userid}")
//    @Operation(summary  =  "查询用户收藏")
//    public  R  selectByUserId(@PathVariable  Long  userid)  {
//        List<Star>  starList  =  this.starService.list(new  QueryWrapper<Star>().eq("user_id",  userid));
//        return  success(starList);
//    }


    @GetMapping("/stars/{userid}")  //  GET请求，请求路径为/stars/{userid}，其中{userid}是用户id的占位符，表示动态获取用户id
    @Operation(summary  =  "用户查看收藏")  //  接口的简要描述为“用户查看收藏”
    public  R  selectByUserId(@PathVariable  Long  userid,  //  接收url路径中的{userid}，并将其作为方法参数
                              @RequestParam(defaultValue  =  "1")  Integer  pageNum,  //  分页查询参数，获取页码，默认为1
                              @RequestParam(defaultValue  =  "10")  Integer  pageSize)  {  //  分页查询参数，获取每页数据量，默认为10
        Page<Star>  page  =  new  Page<>(pageNum,  pageSize);  //  创建分页对象，指定页码和每页数据量
        IPage<Star>  stars  =  this.starService.page(page,  new  QueryWrapper<Star>().eq("user_id",  userid));  //  查询该用户的收藏列表
        return  success(stars);  //  将收藏列表封装成响应结果，返回给前端
    }

    /**
     * 新增数据
     *
     * @param star 实体对象
     * @return 新增结果
     */
    @PostMapping("stars/star")
    @Operation(summary    =    "新增用户的收藏")
    public R insert(@RequestBody Star star) {
        return success(this.starService.save(star));
    }

//    /**
//     * 修改数据
//     *
//     * @param star 实体对象
//     * @return 修改结果
//     */
//    @PutMapping
//    public R update(@RequestBody Star star) {
//        return success(this.starService.updateById(star));
//    }


    @DeleteMapping("/{id}")
    @Operation(summary    =    "取消用户收藏")
    public  R  delete(@PathVariable("id")  Long  id)  {
        return  success(this.starService.removeById(id));
    }
}

