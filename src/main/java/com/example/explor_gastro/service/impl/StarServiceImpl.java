package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.StarDao;
import com.example.explor_gastro.dto.ProductStarDTO;
import com.example.explor_gastro.entity.Star;
import com.example.explor_gastro.service.StarService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收藏表(Star)表服务实现类
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
@Service("starService")
public class StarServiceImpl extends ServiceImpl<StarDao, Star> implements StarService {
    @Resource
    private StarDao starDao;

    public List<ProductStarDTO> getProductStarDTOsByUserId(Integer userId) {
        List<ProductStarDTO> productStarDTOS = starDao.selectProductStarDTOsByUserId(userId);
        if (productStarDTOS.isEmpty()) {
            ProductStarDTO defaultDTO = new ProductStarDTO();
            defaultDTO.setName("用户已注销");
            productStarDTOS.add(defaultDTO);
        }
        return productStarDTOS;
    }
}

