package com.example.explor_gastro.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.entity.ProductImg;

import java.util.List;
import java.util.Optional;

/**
 * 商品表(Product)表服务接口
 *
 * @author makejava
 * @since 2023-05-06 20:16:11
 */
public interface ProductService extends IService<Product> {
    IPage<Product> testSelectPage(int current, int size, Optional<Boolean> isAsc, Optional<String> sortField);
    public boolean updateProduct(Integer productId, String productName, String description, Integer price, String category);
}

