package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.UserImgDao;
import com.example.explor_gastro.entity.UserImg;
import com.example.explor_gastro.service.UserImgService;
import org.springframework.stereotype.Service;

/**
 * 商品图片表(UserImg)表服务实现类
 *
 * @author makejava
 * @since 2023-05-16 16:45:46
 */
@Service("userImgService")
public class UserImgServiceImpl extends ServiceImpl<UserImgDao, UserImg> implements UserImgService {

}

