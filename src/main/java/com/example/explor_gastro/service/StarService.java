package com.example.explor_gastro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.explor_gastro.dto.ProductStarDTO;
import com.example.explor_gastro.entity.Star;

import java.util.List;
import java.util.Map;

/**
 * 收藏表(Star)表服务接口
 *
 * @author makejava
 * @since 2023-05-08 11:38:11
 */
public interface StarService extends IService<Star> {
    List<ProductStarDTO> getProductStarDTOsByUserId(Integer userId);
    Map<String, Object> deleteCartItem(Integer id);
    Map<String, Object> deleteCartItemByIds(List<Integer> id);
}

