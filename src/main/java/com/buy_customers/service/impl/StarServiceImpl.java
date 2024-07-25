package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.StarDao;
import com.buy_customers.dto.ProductStarDTO;
import com.buy_customers.entity.Star;
import com.buy_customers.service.StarService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏表(Star)表服务实现类
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
@Service("starService")
public class StarServiceImpl extends ServiceImpl<StarDao, Star> implements StarService {
}

