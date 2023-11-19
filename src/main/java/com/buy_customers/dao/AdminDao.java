package com.buy_customers.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buy_customers.entity.Admin;
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

