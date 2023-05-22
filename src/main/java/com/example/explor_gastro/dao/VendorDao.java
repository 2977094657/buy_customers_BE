package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.Vendor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商家表(Vendor)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 11:55:49
 */
@Mapper
public interface VendorDao extends BaseMapper<Vendor> {
//
    @Select("SELECT * FROM vendor WHERE phone=#{phone}")
    Vendor findByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM vendor WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<Vendor> searchVendors(@Param("keyword") String keyword);

}

