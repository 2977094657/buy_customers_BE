package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.ProductDao;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 商品表(Product)表服务实现类
 *
 * @author makejava
 * @since 2023-05-06 20:16:11
 */
@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product> implements ProductService {
    @Autowired
    private ProductDao productDao;
    //    current – 当前页 size – 每页显示条数


    /**
     *
     * @param current 当前所在页面
     * @param size 每页显示数量
     * @param isAsc 是否升序排列,false为降序，不填则原序
     * @param sortField 根据传入的此字段来排序，不填则原序
     * @return 返回所有数据
     */

    @Override
    public IPage<Product> testSelectPage(int current, int size, Optional<Boolean> isAsc, Optional<String> sortField) {
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
        // 调用 ProductDao 的 selectPage 方法进行分页查询
        IPage<Product> iPage = productDao.selectPage(page, wrapper);
        // 打印总页数
        System.out.println("总页数: " + iPage.getPages());
        // 打印总记录数
        System.out.println("总记录数: " + iPage.getTotal());
        // 打印当前页上的记录
        System.out.println("记录: " + iPage.getRecords());
        return iPage;
    }
    @Override
    public boolean updateProduct(Integer productId, String productName, String description, Integer price, String category) {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(productName);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);

        return this.updateById(product);
    }
    public IPage<Product> searchProduct(String keyword, int current, int size, Optional<Boolean> isAsc, Optional<String> sortField) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("product_name", keyword)
                .orderBy(isAsc.orElse(false), sortField.orElse("id").isEmpty());
        return productDao.selectPage(new Page<>(current, size), queryWrapper);
    }
    @Override
    public boolean updateImgByProductId(Integer productId, String img) {
        int result = productDao.updateImgByProductId(productId, img);
        return result > 0;
    }
    @Override
    public List<Product> selectByCategory(int current, int size, String category) {
        Map<String, Object> params = new HashMap<>();
        params.put("category", category);
        params.put("offset", (current - 1) * size);
        params.put("limit", size);
        return productDao.selectByCategory(params);
    }

}

