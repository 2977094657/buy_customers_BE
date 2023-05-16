package com.example.explor_gastro.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.explor_gastro.entity.UserImg;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品图片表(UserImg)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-16 16:45:45
 */
@Mapper
public interface UserImgDao extends BaseMapper<UserImg> {

}

