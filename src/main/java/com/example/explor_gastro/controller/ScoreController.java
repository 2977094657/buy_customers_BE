package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.example.explor_gastro.dao.ProductDao;
import com.example.explor_gastro.dao.ScoreDao;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.entity.Score;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品评分表(Score)表控制层
 *
 * @author makejava
 * @since 2023-05-18 15:01:47
 */
@RestController
@RequestMapping("score")
@Tag(name = "评分操作")
public class ScoreController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ScoreDao scoreDao;
    @Resource
    ProductDao productDao;

    /**
     * 用户对商品进行评分。
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param score 分数
     */
    @PostMapping(value = "{userId},{productId}/add",produces = "application/json")
    @Operation(summary = "商品评分")
    public ResponseEntity<String> calculateAndUpdateScore(@PathVariable Integer userId,@PathVariable Integer productId, Integer score) {
        try {
            // 检查该用户是否已对该商品评分
            Score existingScore = scoreDao.getScoreByUserIdAndProductId(userId, productId);
            if (existingScore != null) {
                return ResponseEntity.badRequest().body("该用户已对该商品进行过评分");
            }

            // 创建新的评分实体
            Score newScore = new Score();
            newScore.setUserId(userId);
            newScore.setProductId(productId);
            newScore.setScore(score);

            // 插入新的评分实体
            scoreDao.insert(newScore);

            // 获取该商品的所有评分
            List<Score> scores = scoreDao.getScoresByProductId(productId);

            // 计算平均分数
            Double averageScore = calculateAverageScore(scores);

            // 更新商品实体的分数和总评分人数
            Product product = new Product();
            product.setProductId(productId);
            product.setScore(averageScore);
            product.setTotalComments(scores.size());
            productDao.updateById(product);

            return ResponseEntity.ok("评分成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("评分失败，原因：" + e.getMessage());
        }
    }

    /**
     * 计算平均分数。
     *
     * @param scores 评分实体列表
     * @return 平均分数
     */
    private Double calculateAverageScore(List<Score> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        Double sum = 0.0;
        for (Score score : scores) {
            sum += score.getScore();
        }

        return sum / scores.size();
    }
    /**
     * 修改用户对商品的评分。
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param score 分数
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "{userId},{productId}/update", produces = "application/json")
    @Operation(summary = "修改评分")
    public ResponseEntity<String> updateScore(@PathVariable Integer userId,@PathVariable Integer productId, Integer score) {
        try {
            // 检查该用户是否已对该商品评分
            Score existingScore = scoreDao.getScoreByUserIdAndProductId(userId, productId);
            if (existingScore == null) {
                return ResponseEntity.badRequest().body("该用户未对该商品进行评分");
            }

            // 更新评分实体的分数
            existingScore.setScore(score);
            scoreDao.updateById(existingScore);

            // 获取该商品的所有评分
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("product_id", productId);
            List<Score> scores = scoreDao.selectByMap(columnMap);

            // 计算平均分数
            Double averageScore = 0.0;
            for (Score score1 : scores) {
                averageScore += score1.getScore();
            }
            long totalComments=scores.size();
            if (totalComments > 0) {
                averageScore /= totalComments;
            }

            // 更新商品实体的分数和评分总人数
            Product product = new Product();
            product.setProductId(productId);
            product.setTotalComments(totalComments);
            product.setScore(averageScore);
            productDao.updateById(product);

            return ResponseEntity.ok("评分修改成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("评分修改失败，原因：" + e.getMessage());
        }
    }

    /**
     * 删除用户对商品的评分。
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "{userId},{productId}/delete", produces = "application/json")
    @Operation(summary = "删除评分")
    public ResponseEntity<String> deleteScore(@PathVariable Integer userId,@PathVariable Integer productId) {
        try {
            // 检查该用户是否已对该商品评分
            Score existingScore = scoreDao.getScoreByUserIdAndProductId(userId, productId);
            if (existingScore == null) {
                return ResponseEntity.badRequest().body("该用户未对该商品进行评分");
            }

            // 删除评分实体
            scoreDao.deleteById(existingScore.getId());

            // 获取该商品的所有评分
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("product_id", productId);
            List<Score> scores = scoreDao.selectByMap(columnMap);

            // 计算平均分数
            Double averageScore = 0.0;
            for (Score score : scores) {
                averageScore += score.getScore();
            }
            long totalComments=scores.size();
            if (totalComments > 0) {
                averageScore /= totalComments;
            }

            // 更新商品实体的分数和评分总人数
            Product product = new Product();
            product.setProductId(productId);
            product.setScore(averageScore);
            product.setTotalComments(totalComments);
            productDao.updateById(product);

            return ResponseEntity.ok("评分删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("评分删除失败，原因：" + e.getMessage());
        }
    }

    /**
     * 根据用户ID查看自己对商品的评价。
     *
     * @param userId 用户ID
     * @return ResponseEntity<List<Score>>
     */
    @GetMapping(value = "{userId}/myScore", produces = "application/json")
    @Operation(summary = "查看自己的所有评分")
    public ResponseEntity<List<Score>> getMyScore(@PathVariable Integer userId) {
        try {
            // 获取该用户对所有商品的评分
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("user_id", userId);
            List<Score> scores = scoreDao.selectByMap(columnMap);
            return ResponseEntity.ok(scores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

