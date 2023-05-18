package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.explor_gastro.entity.ProductComments;
import com.example.explor_gastro.service.ProductCommentsService;
import com.example.explor_gastro.utils.ImageUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品评价表(ProductComments)表控制层
 *
 * @author makejava
 * @since 2023-05-09 09:43:45
 */
@RestController
@RequestMapping("productComments")
@Tag(name = "商品评价")
public class ProductCommentsController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ProductCommentsService productCommentsService;
    @Resource
    private ImageUpload imageUpload;

    @PostMapping("add/{userId}")
    @Operation(summary = "增加评价")
    public R insert(@PathVariable int userId, @RequestParam String comments, @RequestParam(name = "imgId",required = false) MultipartFile[] files, @RequestParam int productId) {
        ProductComments productComments = new ProductComments();
        productComments.setUserId(userId);
        productComments.setComments(comments);
        if (files!=null){
             ResponseEntity<List<Map<String, String>>> comments1 = imageUpload.comments(userId, comments, files, productId);
             return success(comments1);
        }
        productComments.setProductId(productId);
        return success(this.productCommentsService.save(productComments));
    }

    @PutMapping("update/{id}")
    @Operation(summary = "修改评价")
    public R update(@PathVariable int id, @RequestParam(required = false) String comments, @RequestParam(name = "imgId",required = false) MultipartFile[] files) {
        ProductComments productComments = this.productCommentsService.getById(id);
        if (comments != null && !comments.isEmpty()) {
            productComments.setComments(comments);
        }
        if (files != null && files.length>0) {
            try {
                imageUpload.updateComments(id,files);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if ((comments!=null&&!comments.isEmpty())||files!=null){
            productComments.setTime(new Date());
            productCommentsService.updateById(productComments);
            return success("修改成功");
        }
        return success("尚未修改");
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除评价")
    public R delete(@PathVariable int id) {
        if (productCommentsService.getById(id)==null){
            return success("暂无此评论");
        }
        productCommentsService.removeByIds(Collections.singleton(id));
        return success("删除成功");
    }
    @GetMapping("{userId}")
    @Operation(summary = "显示用户所有评价")
    public List<ProductComments> userComments(@PathVariable int userId) {
        QueryWrapper<ProductComments> queryWrapper = new QueryWrapper<>();
        //查询user_id等于userId的数据
        queryWrapper.eq("user_id", userId);

        return productCommentsService.list(queryWrapper);
    }
}

