package com.buy_customers.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 网站全局设置(GlobalSettings)表实体类
 *
 * @author makejava
 * @since 2024-01-05 19:33:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalSettings extends Model<GlobalSettings> {
    @TableId(type = IdType.AUTO)
    // 主键自增
    private Integer id;
    // 侧边栏显示,0假1真
    private String sidebar;


    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}

