package com.buy_customers.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buy_customers.dao.GlobalSettingsDao;
import com.buy_customers.entity.GlobalSettings;
import com.buy_customers.service.GlobalSettingsService;
import org.springframework.stereotype.Service;

/**
 * 网站全局设置(GlobalSettings)表服务实现类
 *
 * @author makejava
 * @since 2024-01-05 19:33:42
 */
@Service("globalSettingsService")
public class GlobalSettingsServiceImpl extends ServiceImpl<GlobalSettingsDao, GlobalSettings> implements GlobalSettingsService {

}

