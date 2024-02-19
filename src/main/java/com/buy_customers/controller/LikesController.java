package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buy_customers.common.utils.Response;
import com.buy_customers.entity.Likes;
import com.buy_customers.entity.ProductComments;
import com.buy_customers.service.LikesService;
import com.buy_customers.service.ProductCommentsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * 分页查询所有数据
     *
     * @param page  分页对象
     * @param likes 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<Likes> page, Likes likes) {
        return success(this.likesService.page(page, new QueryWrapper<>(likes)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.likesService.getById(id));
    }

    /**
     * 点赞处理
     *
     */
    @PostMapping("likes")
    public ResponseEntity<Response<?>> likes(@RequestBody Likes newLikes){
        Response<String> response = new Response<>();
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
            response.setCode(200);
            response.setMsg("新增成功");
        } else {
            if (newLikes.getPositiveLikes() != null && likes.getPositiveLikes() != newLikes.getPositiveLikes()) {
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
            if (newLikes.getDisLikes() != null && likes.getDisLikes() != newLikes.getDisLikes()) {
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
            response.setCode(200);
            response.setMsg("修改成功");
        }
        productCommentsService.updateById(productComments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }






    @GetMapping("userLikes")
    public ResponseEntity<Response<?>> getUserLikes(@RequestParam Integer userId){
        Response<List<Map<String, Object>>> response = new Response<>();
        QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Likes> userLikes = likesService.list(queryWrapper);
        if (userLikes.isEmpty()) {
            response.setCode(404);
            response.setMsg("未找到点赞");
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
            }).collect(Collectors.toList());
            response.setData(likesInfo);
            response.setCode(200);
            response.setMsg("查询成功");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * 修改数据
     *
     * @param likes 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody Likes likes) {
        return success(this.likesService.updateById(likes));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.likesService.removeByIds(idList));
    }
}

