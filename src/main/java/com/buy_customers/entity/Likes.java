package com.buy_customers.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 评论点赞表(Likes)表实体类
 *
 * @author makejava
 * @since 2024-02-05 22:34:35
 */
@SuppressWarnings("serial")
@Data
public class Likes extends Model<Likes> {
    // 主键自增
    @TableId(type = IdType.AUTO)
    private Integer id;
    // 用户id
    private Integer userId;
    // 评论id
    private Integer commentsId;
    // 正赞0表示没有,1表示有
    private Integer positiveLikes;
    // 倒赞0表示没有,1表示有
    private Integer disLikes;
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

