package com.buy_customers.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buy_customers.entity.GlobalSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网站全局设置(GlobalSettings)表数据库访问层
 *
 * @author makejava
 * @since 2024-01-05 19:33:42
 */
@Mapper
public interface GlobalSettingsDao extends BaseMapper<GlobalSettings> {

}

