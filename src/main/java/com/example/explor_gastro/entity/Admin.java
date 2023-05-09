package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 管理员表(Admin)表实体类
 *
 * @author makejava
 * @since 2023-05-06 19:46:25
 */
@SuppressWarnings("serial")
public class Admin extends Model<Admin> {
    @TableId(type = IdType.AUTO)
    //管理员id，主键自增
    private Integer adminId;
    //管理员账号
    private String admin;
    //管理员密码
    private String pwd;


    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.adminId;
    }
}

