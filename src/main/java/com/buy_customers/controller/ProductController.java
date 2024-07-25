package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.common.utils.ImageUpload;
import com.buy_customers.entity.Product;
import com.buy_customers.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
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
    public IPage<Product> page(
            @RequestParam(name = "current",defaultValue = "1") int current,
            @RequestParam(name = "size",defaultValue = "10") int size,
            @RequestParam(name = "isAsc", required = false) Optional<Boolean> isAsc,
            @RequestParam(name = "sortField", required = false) Optional<String> sortField,
            @RequestParam(name = "randomSeed",required = false) Long randomSeed
    ) {
        // 创建一个 Page 对象，指定当前页码和每页记录数
        Page<Product> page = new Page<>(current, size);
        // 创建一个 QueryWrapper 对象
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        // 判断是否传入了 isAsc 参数
        if (isAsc.isPresent()) {
            if (isAsc.get()) {
                wrapper.orderByAsc(sortField.orElse("")); // 如果传入了 sortField 参数，则按照 sortField 升序排序，否则不排序
            } else {
                wrapper.orderByDesc(sortField.orElse("")); // 如果传入了 sortField 参数，则按照 sortField 降序排序，否则不排序
            }
        }
        // 在排序字段为 null 或空字符串时，添加一个随机排序规则
        if (sortField.isEmpty() || sortField.get().isEmpty()) {
            wrapper.orderByAsc(String.format("RAND(%d)", randomSeed)); // 使用当前时间戳作为随机种子
        }
        // 调用 ProductDao 的 selectPage 方法进行分页查询
        IPage<Product> iPage = productService.page(page, wrapper);
        // 打印总页数
        System.out.println("总页数: " + iPage.getPages());
        // 打印总记录数
        System.out.println("总记录数: " + iPage.getTotal());
        // 打印当前页上的记录
        System.out.println("记录: " + iPage.getRecords());
        return iPage;
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
    public ResultData<String> addProduct(
            @RequestParam(value = "images",required = false) MultipartFile[] files,
            @RequestParam(defaultValue = "水煮肉片",name = "productName") String productName,
            @RequestParam String name,
            @RequestParam(defaultValue = "32",name = "price") String price,
            @RequestParam(defaultValue = "主食",name = "category") String category
    ) throws IOException {
        if (files==null){
            return ResultData.fail(400, "请上传商品图片！");
        }
        String add = ImageUpload.add(files).getData().toString();
        Product product = new Product();
        product.setProductName(productName);
        product.setCategory(category);
        product.setPrice(price);
        product.setImg(add);
        product.setName(name);
        productService.save(product);
        return ResultData.success("商品添加成功");
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
    public ResultData<String> update(
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
            return ResultData.fail(400,"没有提供要更新的值");
        }

        boolean result = this.productService.updateProduct(productId, productName, price, category);
        if (result) {
            return ResultData.success("商品修改成功");
        } else {
            return ResultData.fail(500,"商品修改失败");
        }
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @DeleteMapping("delete")
    public ResultData<Boolean> delete(@RequestParam List<Long> id) {
        return ResultData.success(this.productService.removeByIds(id));
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

    @GetMapping("selectById")
    public ResultData<Product> selectById(@RequestParam Integer productId) {
        Product product = productService.getById(productId);

        if (product == null) {
            return ResultData.fail(404, "商品不存在");
        }

        return ResultData.success(product);
    }

    @GetMapping("selectByIds")
    public ResultData<List<Product>> selectByIds(@RequestParam List<Integer> productIds){
        List<Product> products = new ArrayList<>();

        for (Integer productId : productIds) {
            Product product = productService.getById(productId);
            if (product != null) {
                products.add(product);
            }
        }

        if (products.isEmpty()) {
            return ResultData.fail(404,"商品不存在");
        }

        return ResultData.success(products);
    }

    @GetMapping("vendor")
    public IPage<Product> vendor(@RequestParam String name,
                                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "30") Integer pageSize){
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        Page<Product> page = new Page<>(pageNum, pageSize);
        return productService.page(page, wrapper);
    }
}