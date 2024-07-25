package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.common.utils.ImageUpload;
import com.buy_customers.entity.Product;
import com.buy_customers.entity.ProductComments;
import com.buy_customers.service.ProductCommentsService;
import com.buy_customers.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 商品评价表(ProductComments)表控制层
 *
 * @author makejava
 * @since 2023-05-09 09:43:45
 */
@RestController
@RequestMapping("productComments")
public class ProductCommentsController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ProductCommentsService productCommentsService;
    @Resource
    private ProductService productService;

    @PostMapping("add")
    public ResultData<Boolean> insert(@RequestParam int userId, @RequestParam String comments, @RequestParam(name = "imgId",required = false) MultipartFile[] files, @RequestParam int productId,@RequestParam int score,@RequestParam String ip) {
        ProductComments productComments = new ProductComments();
        productComments.setUserId(userId);
        productComments.setComments(comments);
        productComments.setScore(score);
        if (files!=null){
            Object comments1 = ImageUpload.comments(files);
            productComments.setImgId((String) comments1);
        }
        productComments.setProductId(productId);
        productComments.setIp(ip);
        productCommentsService.save(productComments);
        QueryWrapper<ProductComments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        // 获取该商品的所有评分
        List<ProductComments> scores = productCommentsService.list(queryWrapper);

        // 计算平均分数
        Double averageScore = calculateAverageScore(scores);

        // 更新商品实体的分数和总评分人数
        Product product = productService.getById(productId);  // 从数据库中获取当前的商品实体
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("product_id", productId);  // 设置要更新的商品ID
        updateWrapper.set("score", averageScore);  // 设置要更新的字段
        updateWrapper.set("total_comments", scores.size());

        // 根据用户评分更新对应的星级数量字段
        switch (score) {
            case 5:
                updateWrapper.set("five", product.getFive() + 1);
                break;
            case 4:
                updateWrapper.set("four", product.getFour() + 1);
                break;
            case 3:
                updateWrapper.set("three", product.getThree() + 1);
                break;
            case 2:
                updateWrapper.set("two", product.getTwo() + 1);
                break;
            case 1:
                updateWrapper.set("one", product.getOne() + 1);
                break;
        }

        return ResultData.success(productService.update(updateWrapper));
    }

    /**
     * 计算平均分数。
     *
     * @param scores 评分实体列表
     * @return 平均分数
     */
    private Double calculateAverageScore(List<ProductComments> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        Double sum = 0.0;
        for (ProductComments score : scores) {
            sum += score.getScore();
        }

        return sum / scores.size();
    }

    @DeleteMapping("delete")
    public ResultData<?> delete(@RequestParam int id) {
        if (productCommentsService.getById(id)==null){
            return ResultData.fail(404,"暂无此评论");
        }
        ProductComments byId = productCommentsService.getById(id);
        QueryWrapper<ProductComments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", byId.getProductId());
        Integer score = byId.getScore();
        productCommentsService.removeByIds(Collections.singleton(id));

        // 获取该商品的所有评分
        List<ProductComments> scores = productCommentsService.list(queryWrapper);

        // 计算平均分数
        Double averageScore = calculateAverageScore(scores);

        // 更新商品实体的分数和总评分人数
        Product product = productService.getById(byId.getProductId());  // 从数据库中获取当前的商品实体
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("product_id", byId.getProductId());  // 设置要更新的商品ID
        updateWrapper.set("score", averageScore);  // 设置要更新的字段
        updateWrapper.set("total_comments", scores.size());

        // 根据用户评分更新对应的星级数量字段
        switch (score) {
            case 5:
                updateWrapper.set("five", product.getFive() - 1);
                break;
            case 4:
                updateWrapper.set("four", product.getFour() - 1);
                break;
            case 3:
                updateWrapper.set("three", product.getThree() - 1);
                break;
            case 2:
                updateWrapper.set("two", product.getTwo() - 1);
                break;
            case 1:
                updateWrapper.set("one", product.getOne() - 1);
                break;
        }
        boolean update = productService.update(updateWrapper);
        if (update){
            return ResultData.success("删除成功");
        }else {
            return ResultData.fail(400,"删除失败");
        }
    }
    @GetMapping("myComments")
    public ResultData<List<ProductComments>> userComments(@RequestParam int userId) {
        QueryWrapper<ProductComments> queryWrapper = new QueryWrapper<>();
        //查询user_id等于userId的数据
        queryWrapper.eq("user_id", userId);
        return ResultData.success(productCommentsService.list(queryWrapper));
    }
}

