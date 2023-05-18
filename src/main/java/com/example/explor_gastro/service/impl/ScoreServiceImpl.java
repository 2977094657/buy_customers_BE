package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.ScoreDao;
import com.example.explor_gastro.entity.Score;
import com.example.explor_gastro.service.ScoreService;
import org.springframework.stereotype.Service;

/**
 * 商品评分表(Score)表服务实现类
 *
 * @author makejava
 * @since 2023-05-18 15:01:47
 */
@Service("scoreService")
public class ScoreServiceImpl extends ServiceImpl<ScoreDao, Score> implements ScoreService {

}

