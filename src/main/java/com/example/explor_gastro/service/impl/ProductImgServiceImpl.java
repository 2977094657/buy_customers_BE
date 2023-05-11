package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.ProductImgDao;
import com.example.explor_gastro.entity.ProductImg;
import com.example.explor_gastro.service.ProductImgService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品图片表(ProductImg)表服务实现类
 *
 * @author makejava
 * @since 2023-05-09 09:32:26
 */
@Service("productImgService")
public class ProductImgServiceImpl extends ServiceImpl<ProductImgDao, ProductImg> implements ProductImgService {

}

