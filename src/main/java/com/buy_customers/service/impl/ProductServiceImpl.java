package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.ProductCommentsDao;
import com.buy_customers.dao.ProductDao;
import com.buy_customers.dao.UserDao;
import com.buy_customers.dto.CommentDTO;
import com.buy_customers.entity.Product;
import com.buy_customers.entity.ProductComments;
import com.buy_customers.entity.User;
import com.buy_customers.service.ProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 商品表(Product)表服务实现类
 *
 * @author makejava
 * @since 2023-05-06 20:16:11
 */
@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product> implements ProductService {
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductCommentsDao productCommentsDao;
    @Resource
    private UserDao userDao;

    @Override
    public IPage<Product> testSelectPage(int current, int size, Optional<Boolean> isAsc, Optional<String> sortField, Long randomSeed) {
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
    public boolean updateProduct(Integer productId, String productName, String price, String category) {
        Product product = new Product();
        product.setProductId(productId);
        if (productName!=null&& !productName.isEmpty()){
            product.setProductName(productName);
        }
        if (price!=null&& !price.equals("0")){
            product.setPrice(price);
        }
        if (category!=null&& !category.isEmpty()){
            product.setCategory(category);
        }
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
     * @param productId  商品ID
     * @param pageNum    当前页数
     * @param pageSize   每页显示数量
     * @param sortByTime 是否按时间排序
     * @return 商品评价列表
     */
    @Override
    public List<Object> getCommentsByProductId(long productId, int pageNum, int pageSize, boolean sortByTime, boolean sortByLikes) {
        // 计算起始索引
        int startIndex = (pageNum - 1) * pageSize;

        // 使用QueryWrapper构建查询条件
        QueryWrapper<ProductComments> productCommentsQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ProductComments> orderBy = productCommentsQueryWrapper.eq("product_id", productId);

        // 根据sortByTime参数指定排序方式
        if (sortByTime) {
            orderBy = orderBy.orderBy(true, false, "time"); // 修改这里，使用降序排序
        }

        // 如果 sortByLikes 为 true，则按照正赞数减去倒赞数进行排序
        if (sortByLikes) {
            orderBy = orderBy.orderByDesc("(positive_likes - dis_likes)");
        }

        orderBy.last("LIMIT " + startIndex + ", " + pageSize);

        // 调用DAO层获取商品评价列表
        List<ProductComments> comments = productCommentsDao.selectList(orderBy);

        // 调用 DAO 层获取总的评论数量
        QueryWrapper<ProductComments> countWrapper = new QueryWrapper<>();
        countWrapper.eq("product_id", productId);
        int total = productCommentsDao.selectCount(countWrapper);

        // 计算总的页数
        int totalPageNum = (total + pageSize - 1) / pageSize;

        // 将ProductComments转换为CommentDto
        List<CommentDTO> commentDTOS = new ArrayList<>();
        for (ProductComments comment : comments) {
            CommentDTO commentDto = new CommentDTO();
            commentDto.setId(comment.getCommentsId());
            commentDto.setComments(comment.getComments());
            commentDto.setScore(comment.getScore());
            commentDto.setImgId(comment.getImgId());
            commentDto.setTime(comment.getTime());
            commentDto.setIp(comment.getIp());
            commentDto.setPositiveLikes(comment.getPositiveLikes());
            commentDto.setDisLikes(comment.getDisLikes());
            User user = userDao.selectById(comment.getUserId());
            if (user==null){
                commentDto.setUserName("账号已注销");
                commentDto.setUserAvatar("http://124.221.7.201:5000/OIP.jpg");
            }
            if (user != null) {
                commentDto.setUserName(user.getName());
                commentDto.setUserAvatar(user.getUserAvatar());
            }
            commentDTOS.add(commentDto);
        }
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("pageNum", pageNum);
        pageInfo.put("pageSize", pageSize);
        pageInfo.put("sortByTime", sortByTime);
        pageInfo.put("sortByLikes", sortByLikes); // 添加 sortByLikes 参数
        pageInfo.put("pages", totalPageNum);  // 添加总的页数
        pageInfo.put("total", total);  // 添加总的评论数量

        // 创建一个 List 用于返回结果
        List<Object> result = new ArrayList<>();
        result.add(commentDTOS); // 第一个元素是评论列表
        result.add(pageInfo);    // 第二个元素是包含 pageNum，pageSize 和 sortByTime 参数的 Map

        return result;
    }
}

