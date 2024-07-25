package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.entity.Likes;
import com.buy_customers.entity.ProductComments;
import com.buy_customers.service.LikesService;
import com.buy_customers.service.ProductCommentsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 评论点赞表(Likes)表控制层
 *
 * @author makejava
 * @since 2024-02-05 22:34:35
 */
@RestController
@RequestMapping("likes")
public class LikesController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private LikesService likesService;
    @Resource
    private ProductCommentsService productCommentsService;


    /**
     * 点赞处理
     *
     */
    @PostMapping("likes")
    public ResultData<String> likes(@RequestBody Likes newLikes){
        QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", newLikes.getUserId());
        queryWrapper.eq("comments_id", newLikes.getCommentsId());
        Likes likes = likesService.getOne(queryWrapper);
        ProductComments productComments = productCommentsService.getById(newLikes.getCommentsId());
        if (likes == null) {
            if (newLikes.getPositiveLikes() != null && newLikes.getPositiveLikes() == 1) {
                productComments.setPositiveLikes(productComments.getPositiveLikes() + 1);
            }
            if (newLikes.getDisLikes() != null && newLikes.getDisLikes() == 1) {
                productComments.setDisLikes(productComments.getDisLikes() + 1);
            }
            likesService.save(newLikes);
            return ResultData.success("新增成功");
        } else {
            if (newLikes.getPositiveLikes() != null && !Objects.equals(likes.getPositiveLikes(), newLikes.getPositiveLikes())) {
                if (newLikes.getPositiveLikes() == 1) {
                    likes.setDisLikes(0);
                    productComments.setPositiveLikes(productComments.getPositiveLikes() + 1);
                    if (productComments.getDisLikes()!=0){
                        productComments.setDisLikes(productComments.getDisLikes() - 1);
                    }
                    if (likes.getDisLikes() == 1) {
                        productComments.setDisLikes(productComments.getDisLikes() - 1);
                    }
                } else {
                    productComments.setPositiveLikes(productComments.getPositiveLikes() - 1);
                }
                likes.setPositiveLikes(newLikes.getPositiveLikes());
            }
            if (newLikes.getDisLikes() != null && !Objects.equals(likes.getDisLikes(), newLikes.getDisLikes())) {
                if (newLikes.getDisLikes() == 1) {
                    likes.setPositiveLikes(0);
                    productComments.setDisLikes(productComments.getDisLikes() + 1);
                    if (productComments.getPositiveLikes()!=0){
                        productComments.setPositiveLikes(productComments.getPositiveLikes() - 1);
                    }
                    if (likes.getPositiveLikes() == 1) {
                        productComments.setPositiveLikes(productComments.getPositiveLikes() - 1);
                    }
                } else {
                    productComments.setDisLikes(productComments.getDisLikes() - 1);
                }
                likes.setDisLikes(newLikes.getDisLikes());
            }
            likesService.updateById(likes);
        }
        productCommentsService.updateById(productComments);
        return ResultData.success("修改成功");
    }


    @GetMapping("userLikes")
    public ResultData<?> getUserLikes(@RequestParam Integer userId) {
        QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Likes> userLikes = likesService.list(queryWrapper);
        if (userLikes.isEmpty()) {
            return ResultData.fail(404, "未找到点赞");
        } else {
            List<Map<String, Object>> likesInfo = userLikes.stream().map(like -> {
                Map<String, Object> info = new HashMap<>();
                info.put("commentsId", like.getCommentsId());
                if (like.getPositiveLikes() == 1) {
                    info.put("likeType", "positiveLikes");
                } else if (like.getDisLikes() == 1) {
                    info.put("likeType", "disLikes");
                } else {
                    info.put("likeType", "暂无");
                }
                return info;
            }).toList();
            return ResultData.success(likesInfo);
        }
    }
}

