package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.VendorComments;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商家评价表(VendorComments)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 11:55:50
 */
@Mapper
public interface VendorCommentsDao extends BaseMapper<VendorComments> {

}

