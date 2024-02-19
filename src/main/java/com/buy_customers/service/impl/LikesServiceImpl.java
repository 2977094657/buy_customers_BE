package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.LikesDao;
import com.buy_customers.entity.Likes;
import com.buy_customers.service.LikesService;
import org.springframework.stereotype.Service;

/**
 * 评论点赞表(Likes)表服务实现类
 *
 * @author makejava
 * @since 2024-02-05 22:34:35
 */
@Service("likesService")
public class LikesServiceImpl extends ServiceImpl<LikesDao, Likes> implements LikesService {

}

