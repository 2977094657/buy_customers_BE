package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.explor_gastro.dto.ProductStarDTO;
import com.example.explor_gastro.entity.Star;
import com.example.explor_gastro.service.StarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 新增数据
     *
     * @param productId 实体对象
     * @return 新增结果
     */
    @PostMapping("staradd")
    @Operation(summary  =  "新增用户的收藏")
    public  R  insert(
            @RequestParam("userId")  Integer  userId,
            @RequestParam("productId")  Integer  productId
    )  {
        try {
            //  创建Star对象并设置其属性
            Star  star  =  new  Star();
            star.setUserId(userId);
            star.setProductId(productId);
            //  将Star对象保存到数据库中
            boolean  success  =  this.starService.save(star);
            if  (success)  {
                return  success("用户收藏成功");
            }  else  {
                return  failed("用户收藏失败");
            }
        } catch (Exception e) {
            return failed("用户已收藏此商品");
        }
    }


    //  删除映射，请求方式为DELETE，路径为"del"
    @DeleteMapping("del")
//  操作摘要，用于描述此接口功能
    @Operation(summary  =  "取消用户收藏")
    public  R  delete(@RequestParam("id")  Long  id)  {
        //  调用starService的removeById方法，删除id对应的记录，并返回删除结果
        return  success(this.starService.removeById(id));
    }

    @GetMapping("all")
    public List<ProductStarDTO> getProductStarDTOsByUserId(@RequestParam Integer userId) {
        return starService.getProductStarDTOsByUserId(userId);
    }
}

