package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
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

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User selectUserByPhone(String phone);

    @Select("SELECT * FROM user WHERE user_Id = #{userId}")//用于修改用户密码
    User selectByUserId1(Integer userId);

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Select("SELECT name, description, address, signup_time AS signupTime, phone FROM user WHERE user_id = #{userId}")
    User selectUserById(Integer userId);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 是否修改成功
     */
    @Update("UPDATE user SET name = #{name}, description = #{description}, address = #{address}, phone = #{phone} WHERE user_id = #{userId}")
    boolean updateUser(User user);

}

