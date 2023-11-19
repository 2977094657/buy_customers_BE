package com.buy_customers.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.buy_customers.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * 商品表(Product)表服务接口
 *
 * @author makejava
 * @since 2023-05-06 20:16:11
 */
public interface ProductService extends IService<Product> {
    IPage<Product> testSelectPage(int current, int size, Optional<Boolean> isAsc, Optional<String> sortField, Long randomSeed);
    boolean updateProduct(Integer productId, String productName, Integer price, String category);
    /**
     * 模糊搜索商品
     *
     * @param keyword   关键字
     * @param current   当前所在页面
     * @param size      每页显示数量
     * @param isAsc     是否升序排列，不传或传入空值则不排序
     * @param sortField 根据传入的此字段来排序，不传或传入空值则不排序
     * @return 返回搜索结果
     */
    IPage<Product> searchProduct(String keyword, int current, int size, Boolean isAsc, String sortField);

    List<Product> selectByCategory(int current, int size, String category);
    List<Object> getCommentsByProductId(long productId, int pageNum, int pageSize, boolean sortByTime);
}

