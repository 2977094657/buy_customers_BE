package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.ProductDao;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.service.ProductService;
import org.springframework.stereotype.Service;

/**
 * 商品表(Product)表服务实现类
 *
 * @author makejava
 * @since 2023-05-06 20:16:11
 */
@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product> implements ProductService {

}

