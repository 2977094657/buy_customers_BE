package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.VendorCommentsDao;
import com.example.explor_gastro.entity.VendorComments;
import com.example.explor_gastro.service.VendorCommentsService;
import org.springframework.stereotype.Service;

/**
 * 商家评价表(VendorComments)表服务实现类
 *
 * @author makejava
 * @since 2023-05-08 11:55:51
 */
@Service("vendorCommentsService")
public class VendorCommentsServiceImpl extends ServiceImpl<VendorCommentsDao, VendorComments> implements VendorCommentsService {

}

