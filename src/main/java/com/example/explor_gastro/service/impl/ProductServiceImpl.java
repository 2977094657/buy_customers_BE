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
    public IPage<Product> testSelectPage(int current, int size,boolean isAsc,String sortField) {
        // 创建一个 Page 对象，指定当前页码和每页记录数
        Page<Product> page = new Page<>(current, size);
        // 创建一个 QueryWrapper 对象，指定按照 score 字段升序排列
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        if (isAsc) {
            wrapper.orderByAsc(sortField);
        } else {
            wrapper.orderByDesc(sortField);
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
}

