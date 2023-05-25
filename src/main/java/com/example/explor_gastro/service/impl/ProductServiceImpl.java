package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.ProductCommentsDao;
import com.example.explor_gastro.dao.ProductDao;
import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.dto.CommentDto;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.entity.ProductComments;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    private ProductCommentsDao productCommentsDao;
    @Autowired
    private UserDao userDao;

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
        // 在排序字段为 null 或空字符串时，添加一个随机排序规则
        if (!sortField.isPresent() || sortField.get().isEmpty()) {
            wrapper.orderByAsc("rand()");
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
    @Override
    public IPage<Product> searchProduct(String keyword, int current, int size, Boolean isAsc, String sortField) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("product_name", keyword);

        // 根据提供的字段和排序顺序排序
        if (sortField != null && !sortField.isEmpty() && isAsc != null) {
            if (isAsc) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        }

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
    /**
     * 根据商品ID获取商品评价列表
     *
     * @param productId 商品ID
     * @param pageNum   当前页数
     * @param pageSize  每页显示数量
     * @param sortByTime 是否按时间排序
     * @return 商品评价列表
     */
    @Override
    public List<CommentDto> getCommentsByProductId(long productId, int pageNum, int pageSize, boolean sortByTime) {
        // 计算起始索引
        int startIndex = (pageNum - 1) * pageSize;

        // 使用QueryWrapper构建查询条件，并根据sortByTime参数指定排序方式
        QueryWrapper<ProductComments> productCommentsQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ProductComments> orderByTime = productCommentsQueryWrapper
                .eq("product_id", productId)
                .orderBy(true, sortByTime, "time")
                .last("LIMIT " + startIndex + ", " + pageSize);

        // 调用DAO层获取商品评价列表
        List<ProductComments> comments = productCommentsDao.selectList(orderByTime);

        // 将ProductComments转换为CommentDto
        List<CommentDto> commentDtos = new ArrayList<>();
        for (ProductComments comment : comments) {
            CommentDto commentDto = new CommentDto();
            commentDto.setComments(comment.getComments());
            commentDto.setImgId(comment.getImgId());
            commentDto.setTime(comment.getTime());
            User user = userDao.selectById(comment.getUserId());
            if (user==null){
                commentDto.setUserName("账号已注销");
                commentDto.setUserAvatar("http://1.14.126.98:5000/OIP.jpg");
            }
            if (user != null) {
                commentDto.setUserName(user.getName());
                commentDto.setUserAvatar(user.getUserAvatar());
            }
            commentDtos.add(commentDto);
        }

        return commentDtos;
    }
}

