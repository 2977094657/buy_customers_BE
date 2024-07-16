package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buy_customers.common.annotation.EncryptResponse;
import com.buy_customers.common.httpstatus.CustomStatusCode;
import com.buy_customers.common.utils.ImageUpload;
import com.buy_customers.common.utils.Response;
import com.buy_customers.entity.Product;
import com.buy_customers.service.ProductService;
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
public class ProductController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ProductService productService;

    /**
     * @param current   当前所在页面
     * @param size      每页显示数量
     * @param isAsc     是否升序排列，不传或传入空值则不排序
     * @param sortField 根据传入的此字段来排序，不传或传入空值则不排序
     * @return 返回所有数据
     */
    @GetMapping("all")
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
    public ResponseEntity<Map<String, Object>> addProduct(
            @RequestParam(value = "images",required = false) MultipartFile[] files,
            @RequestParam(defaultValue = "水煮肉片",name = "productName") String productName,
            @RequestParam String name,
            @RequestParam(defaultValue = "32",name = "price") String price,
            @RequestParam(defaultValue = "主食",name = "category") String category
    ) {
        Map<String, Object> responseBody = new HashMap<>();
        if (files==null){
            responseBody.put("message", "请上传商品图片！");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }
        String productImgList = (String) ImageUpload.add(files);
        Product product = new Product();
        product.setProductName(productName);
        product.setCategory(category);
        product.setPrice(price);
        product.setImg(productImgList);
        product.setName(name);
        productService.save(product);
        responseBody.put("message", "商品添加成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
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
    @PutMapping("update")
    public R update(
            @RequestParam Integer productId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String category
    ) {
        // 检查每个参数是否为null值或空字符串
        if ((productName == null || productName.isEmpty())
                && (description == null || description.isEmpty())
                && (price == null || price.isEmpty())
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
    @DeleteMapping("delete")
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
    @GetMapping("search")
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
    public List<Object> getProductComments(@RequestParam Integer productId,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(defaultValue = "true") Boolean sortByTime,
                                           @RequestParam(defaultValue = "true") Boolean sortByLikes
    ) {
        return productService.getCommentsByProductId(productId, pageNum, pageSize, sortByTime, sortByLikes);
    }

    @PutMapping("/updateImages")
    public ResponseEntity<List<Map<String, String>>> updateImages(@RequestParam Integer productId, @RequestParam(name = "images") MultipartFile[] files) {
        // 检查商品是否存在
        Product product = productService.getById(productId);
        if (product == null) {
            Map<String, String> error = Map.of("message", "商品不存在");
            return new ResponseEntity<>(List.of(error), HttpStatus.NOT_FOUND);
        }

        // 调用 ImageUploadService 处理文件上传
        ResponseEntity<List<Map<String, String>>> uploadResponse;
        try {
            uploadResponse = ImageUpload.update(files);
        } catch (IOException e) {
            Map<String, String> error = Map.of("message", "文件上传过程中发生错误: " + e.getMessage());
            return new ResponseEntity<>(List.of(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (uploadResponse.getStatusCode() == HttpStatus.OK) {
            List<Map<String, String>> responseList = uploadResponse.getBody();
            if (responseList != null) {
                // 提取上传成功的图片 URL
                List<String> imgUrls = responseList.stream()
                        .map(map -> map.get("url"))
                        .filter(Objects::nonNull)
                        .toList();

                // 更新商品图片信息
                product.setImg(String.join(",", imgUrls));
                productService.updateById(product);
            }
        }

        return uploadResponse;
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
    public ResponseEntity<Map<String, Object>> example() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", CustomStatusCode.SUCCESS.getCode());
        response.put("message", CustomStatusCode.SUCCESS.getMessage());
        return ResponseEntity.status(CustomStatusCode.SUCCESS.getCode()).body(response);
    }

    @GetMapping("selectById")
    public ResponseEntity<Response<Product>> selectById(@RequestParam Integer productId) {
        Product product = productService.getById(productId);
        Response<Product> response = new Response<>();

        if (product == null) {
            response.setCode(400);
            response.setMsg("商品不存在");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setCode(200);
        response.setData(product);
        response.setMsg("请求成功");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("selectByIds")
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