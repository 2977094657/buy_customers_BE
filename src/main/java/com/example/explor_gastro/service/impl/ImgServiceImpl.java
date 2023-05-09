package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.ImgDao;
import com.example.explor_gastro.entity.Img;
import com.example.explor_gastro.service.ImgService;
import org.springframework.stereotype.Service;

/**
 * 评论图片表(Img)表服务实现类
 *
 * @author makejava
 * @since 2023-05-09 09:43:46
 */
@Service("imgService")
public class ImgServiceImpl extends ServiceImpl<ImgDao, Img> implements ImgService {

}

