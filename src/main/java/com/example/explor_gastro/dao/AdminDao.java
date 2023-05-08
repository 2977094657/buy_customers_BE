package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员表(Admin)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-06 19:46:25
 */
@Mapper
public interface AdminDao extends BaseMapper<Admin> {

}

