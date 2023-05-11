package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户表(User)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
@Mapper
public interface UserDao extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User selectByUsername(String phone);

}

