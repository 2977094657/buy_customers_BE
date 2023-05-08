package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.Vendor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商家表(Vendor)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 11:55:49
 */
@Mapper
public interface VendorDao extends BaseMapper<Vendor> {

}

