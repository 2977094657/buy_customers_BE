package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.ProductCommentsDao;
import com.buy_customers.entity.ProductComments;
import com.buy_customers.service.ProductCommentsService;
import org.springframework.stereotype.Service;

/**
 * 商品评价表(ProductComments)表服务实现类
 *
 * @author makejava
 * @since 2023-05-09 09:43:45
 */
@Service("productCommentsService")
public class ProductCommentsServiceImpl extends ServiceImpl<ProductCommentsDao, ProductComments> implements ProductCommentsService {

}

