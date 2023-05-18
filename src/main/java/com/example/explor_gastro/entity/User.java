package com.example.explor_gastro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表(User)表实体类
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 如果查处为null的字段则不显示，可以单独给某字段加
@SuppressWarnings("serial")
public class User extends Model<User> {
    @TableId(type = IdType.AUTO)
    //用户id，主键自增
    private Integer userId;
    //用户名
    private String name;
    //用户密码
    private String pwd;
    //手机号
    private String phone;
    //注册时间
    private Date signupTime;
    //用户简介有默认值
    private String description;
    //下单地址
    private String address;
    // JWT 的签发时间
    @TableField(value = "iat", exist = false) // 数据库不会对此字段操作
    private Date iat;
    // JWT 的过期时间
    @TableField(value = "exp", exist = false)
    private Date exp;

    public User(Integer userId, String phone, String pwd, Date iat, Date exp) {
        this.userId = userId;
        this.phone = phone;
        this.pwd = pwd;
        this.iat = iat;
        this.exp = exp;
    }

    public User(){

    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getSignupTime() {
        return signupTime;
    }

    public void setSignupTime(Date signupTime) {
        this.signupTime = signupTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

    public void getUserId(Integer userId) {

    }
}

