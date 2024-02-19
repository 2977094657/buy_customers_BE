package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buy_customers.common.config.EncryptResponse;
import com.buy_customers.common.httpstatus.CustomStatusCode;
import com.buy_customers.common.utils.ImageUpload;
import com.buy_customers.common.utils.Response;
import com.buy_customers.entity.Product;
import com.buy_customers.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 商品表(Product)表控制层
 *
 * @author makejava
 * @since 2023-05-06 20:16:10
 */
@RestController
@RequestMapping("product")
@Tag(name = "商品功能")
public class ProductController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ProductService productService;
    @Resource
    private ImageUpload imageUpload;

    /**
     * @param current   当前所在页面
     * @param size      每页显示数量
     * @param isAsc     是否升序排列，不传或传入空值则不排序
     * @param sortField 根据传入的此字段来排序，不传或传入空值则不排序
     * @return 返回所有数据
     */
    @GetMapping("all")
    @Operation(summary = "分页查询所有商品")
    @Parameters({
            @Parameter(name = "current", description = "所在页面"),
            @Parameter(name = "size", description = "每页显示数据"),
            @Parameter(name = "isAsc", description = "是否升序排列"),
            @Parameter(name = "sortField", description = "根据此参数传入的字段排序")
    })
    @EncryptResponse
    public IPage<Product> page(
            @RequestParam(name = "current",defaultValue = "1") int current,
            @RequestParam(name = "size",defaultValue = "10") int size,
            @RequestParam(name = "isAsc", required = false) Optional<Boolean> isAsc,
            @RequestParam(name = "sortField", required = false) Optional<String> sortField,
            @RequestParam(name = "randomSeed",required = false) Long randomSeed
    ) {
        return productService.testSelectPage(current, size, isAsc, sortField,randomSeed);
    }

    /**
     * 按照category字段的内容查询所有商品
     *
     * @param current  当前所在页面
     * @param size     每页显示数量
     * @param category 商品类别
     * @return 返回查询结果
     */
    @GetMapping("productsByCategory")
    @Operation(summary = "按照category字段的内容查询所有商品")
    @Parameters({
            @Parameter(name = "current", description = "所在页面"),
            @Parameter(name = "size", description = "每页数量"),
            @Parameter(name = "category", description = "商品类别")
    })
    public List<Product> selectByCategory(@RequestParam(defaultValue = "1",name = "current") int current,
                                          @RequestParam(defaultValue = "10",name = "size") int size,
                                          @RequestParam(defaultValue = "主食",name = "category") String category) {
        return productService.selectByCategory(current, size, category);
    }

    /**
     * 新增商品
     *
     * @param files 多个图片，以数组存入
     * @param productName 商品名字
     * @param name 商家名字，根据商家登陆的账号来传入此参数，不允许商家填入
     * @param price 商品价格
     * @param category 商品分类，此处应为下拉栏，不允许商家填写，四个分类: 主食、小吃、甜品、饮料
     * @return 返回增加结果
     */
    @PostMapping("add")
    @Operation(summary = "新增商品")
    @Parameters({
            @Parameter(name = "images", description = "多个图片，以数组存入"),
            @Parameter(name = "productName", description = "商品名字"),
            @Parameter(name = "name", description = "商家名字，根据商家登陆的账号来传入此参数，不允许商家填入"),
            @Parameter(name = "price", description = "价格"),
            @Parameter(name = "category", description = "商品分类，此处应为下拉栏，不允许商家填入，四个分类:主食、小吃、甜品、饮料")
    })
    public ResponseEntity<Map<String, Object>> addProduct(
            @RequestParam(value = "images",required = false) MultipartFile[] files,
            @RequestParam(defaultValue = "水煮肉片",name = "productName") String productName,
            @RequestParam String name,
            @RequestParam(defaultValue = "32",name = "price") Double price,
            @RequestParam(defaultValue = "主食",name = "category") String category
    ) {
        Map<String, Object> responseBody = new HashMap<>();
        if (files==null){
            responseBody.put("message", "请上传商品图片！");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }
        List<Map<String, String>> responseList = imageUpload.add(files, productName, name, price, category).getBody();
        boolean isSuccess = true;
        if (responseList != null) {
            for (Map<String, String> response : responseList) {
                if (response.containsKey("message")) {
                    isSuccess = false;
                    break;
                }
            }
        }
        if (isSuccess) {
            responseBody.put("message", "商品添加成功");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } else {
            responseBody.put("message", "商品添加失败");
            responseBody.put("errors", responseList);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    /**
     * 根据id修改商品
     *
     * @param productId 商品id，根据此字段修改商品
     * @param productName 商品名称
     * @param description 商品介绍
     * @param price 商品价格
     * @param category 商品分类，此处应为下拉栏，不允许商家填写，四个分类: 主食、小吃、甜品、饮料。类型为字符串
     * @return 返回修改结果
     */
    @Operation(summary = "根据商品id修改商品")
    @PutMapping("update")
    public R update(
            @RequestParam Integer productId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String category
    ) {
        // 检查每个参数是否为null值或空字符串
        if ((productName == null || productName.isEmpty())
                && (description == null || description.isEmpty())
                && (price == null || price.toString().isEmpty())
                && (category == null || category.isEmpty())) {
            return failed("没有提供要更新的值");
        }

        boolean result = this.productService.updateProduct(productId, productName, price, category);
        if (result) {
            return success("商品修改成功");
        } else {
            return failed("商品修改失败");
        }
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @Operation(summary = "根据商品id删除商品")
    @DeleteMapping("delete")
    @Parameters({
            @Parameter(name = "id", description = "商品id，根据商家点击删除的商品id来传入此参数，不允许商家填入，根据此参数来确定要删除的商品"),
            })
    public R delete(@RequestParam List<Long> id) {
        return success(this.productService.removeByIds(id));
    }

    /**
     * 根据商品名字模糊搜索商品
     *
     * @param keyword 搜索关键字
     * @param current 当前所在页面
     * @param size 每页显示数据
     * @param isAsc 是否升序排列，不传或传入空值则不排序
     * @param sortField 根据此参数传入的字段排序
     * @return 返回搜索结果
     */
    @Operation(summary = "根据商品名字模糊搜索商品")
    @GetMapping("search")
    @Parameters({
            @Parameter(name = "keyword", description = "搜索关键字"),
            @Parameter(name = "current", description = "所在页面"),
            @Parameter(name = "size", description = "每页显示数据"),
            @Parameter(name = "isAsc", description = "是否升序排列，不传或传入空值则不排序"),
            @Parameter(name = "sortField", description = "根据此参数传入的字段排序")
    })
    public IPage<Product> search(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "current",defaultValue = "1") int current,
            @RequestParam(name = "size",defaultValue = "60") int size,
            @RequestParam(name = "isAsc", required = false) Boolean isAsc,
            @RequestParam(name = "sortField", required = false) String sortField) {
        return productService.searchProduct(keyword, current, size, isAsc, sortField);
    }

    /**
     * 获取指定商品的评价列表
     *
     * @param productId  商品id
     * @param pageNum    所在页面，默认为1
     * @param pageSize   每页显示数量，默认为10
     * @param sortByTime 是否按照时间排序，默认为true
     * @return 评论列表
     */
    @GetMapping("comments")
    @Operation(summary = "商品评价")
    @Parameters({
            @Parameter(name = "productId", description = "商品id"),
            @Parameter(name = "pageNum", description = "所在页面", example = "1"),
            @Parameter(name = "pageSize", description = "每页显示数量", example = "10"),
            @Parameter(name = "sortByTime", description = "是否按照时间排序", example = "true")
    })
    public List<Object> getProductComments(@RequestParam Integer productId,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(defaultValue = "true") Boolean sortByTime,
                                           @RequestParam(defaultValue = "true") Boolean sortByLikes
    ) {
        return productService.getCommentsByProductId(productId, pageNum, pageSize, sortByTime, sortByLikes);
    }

    @PutMapping("updateImages")
    @Operation(summary = "根据商品id修改商品图片")
    public ResponseEntity<List<Map<String, String>>> updateImages(@RequestParam Integer productId, @RequestParam(name = "images") MultipartFile[] files) throws IOException {
        return imageUpload.update(productId, files);
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamSource> download(@RequestParam String url) throws IOException {
        InputStream in = new URL(url).openStream();
        InputStreamSource resource = new InputStreamResource(in);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=image.webp");
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
    @GetMapping("/example")
    @Operation(summary = "自定义状态码测试")
    public ResponseEntity<Map<String, Object>> example() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", CustomStatusCode.SUCCESS.getCode());
        response.put("message", CustomStatusCode.SUCCESS.getMessage());
        return ResponseEntity.status(CustomStatusCode.SUCCESS.getCode()).body(response);
    }

    @GetMapping(value = "selectById",produces = "application/json")
    @Operation(summary = "根据id查询商品")
    @Parameters({
            @Parameter(name = "productId", description = "商品id"),
    })
    public ResponseEntity<Object> selectById(@RequestParam Integer productId){
        Product product = productService.getById(productId);

        if (product == null) {
            return new ResponseEntity<>("商品不存在", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("selectByIds")
    @Operation(summary = "根据id查询多个商品")
    @Parameters({
            @Parameter(name = "productIds", description = "商品id列表"),
    })
    public ResponseEntity<Response<?>> selectByIds(@RequestParam List<Integer> productIds){
        // 构建响应
        Response<List<Product>> response = new Response<>();
        List<Product> products = new ArrayList<>();

        for (Integer productId : productIds) {
            Product product = productService.getById(productId);
            if (product != null) {
                products.add(product);
            }
        }

        if (products.isEmpty()) {
            response.setCode(404);
            response.setMsg("商品不存在");
            response.setData(null);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setCode(200);
        response.setMsg("查询成功");
        response.setData(products);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("vendor")
    @Operation(summary = "查询商家所有商品")
    @Parameters({
            @Parameter(name = "name", description = "商品名称"),
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页显示数量")
    })
    public ResponseEntity<IPage<Product>> vendor(@RequestParam String name,
                                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "30") Integer pageSize){
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        Page<Product> page = new Page<>(pageNum, pageSize);
        IPage<Product> pageResult = productService.page(page, wrapper);
        return ResponseEntity.ok(pageResult);
    }

}