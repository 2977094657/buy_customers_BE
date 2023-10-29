package com.example.explor_gastro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.explor_gastro.dao.StarDao;
import com.example.explor_gastro.dto.ProductStarDTO;
import com.example.explor_gastro.entity.Star;
import com.example.explor_gastro.service.StarService;
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

    @Override
    public Map<String, Object> deleteCartItem(Integer id) {
        Map<String, Object> response = new HashMap<>();
        Star star = this.getOne(new QueryWrapper<Star>()
                .eq("id", id)
        );
        if (star == null) {
            response.put("status", 400);
            response.put("message", "宝贝不存在");
            response.put("data", null);
            return response;
        }

        boolean deleteSuccess = this.remove(new QueryWrapper<Star>()
                .eq("id", id)
        );

        if (deleteSuccess) {
            response.put("status", 200);
            response.put("message", "宝贝已成功删除");
            response.put("data", null);
        } else {
            response.put("status", 400);
            response.put("message", "宝贝删除失败");
            response.put("data", null);
        }

        return response;
    }

    @Override
    public Map<String, Object> deleteCartItemByIds(List<Integer> id) {
        Map<String, Object> response = new HashMap<>();
        if (id.isEmpty()) {
            response.put("status", 400);
            response.put("message", "暂时还没有收藏哦");
            response.put("data", null);
            return response;
        }
        try {
            int success = starDao.deleteBatchIds(id);
            if (success<=id.size() && success>0) {
                response.put("status", 200);
                response.put("message", "宝贝删除成功");
                response.put("data", success);
            } else if(success==0){
                response.put("status", 400);
                response.put("message", "不存在此宝贝");
                response.put("data", success);
            }
            return response;
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "服务器出现异常");
            response.put("data", null);
            return response;
        }
    }
}

