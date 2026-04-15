package com.shewell.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shewell.entity.SystemConfig;

import java.util.Map;

public interface SystemConfigService extends IService<SystemConfig> {

    String getConfig(String key);

    void setConfig(String key, String value);

    Map<String, String> getAllConfigs();

    void batchUpdate(Map<String, String> configs);
}
