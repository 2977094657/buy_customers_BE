package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 管理员表(Admin)表实体类
 *
 * @author makejava
 * @since 2023-05-06 19:46:25
 */
@Data
public class Admin extends Model<Admin> {
    @TableId(type = IdType.AUTO)
    //管理员id，主键自增
    private Integer adminId;
    //管理员账号
    private String admin;
    //管理员密码
    private String pwd;
}

