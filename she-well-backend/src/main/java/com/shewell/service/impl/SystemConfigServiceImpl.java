package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.entity.SystemConfig;
import com.shewell.mapper.SystemConfigMapper;
import com.shewell.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "system:config:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    @Override
    public String getConfig(String key) {
        String cacheKey = CACHE_PREFIX + key;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached.toString();

        SystemConfig config = lambdaQuery()
                .eq(SystemConfig::getConfigKey, key)
                .one();
        String value = config != null ? config.getConfigValue() : null;
        if (value != null) {
            redisTemplate.opsForValue().set(cacheKey, value, CACHE_TTL);
        }
        return value;
    }

    @Override
    public void setConfig(String key, String value) {
        SystemConfig config = lambdaQuery()
                .eq(SystemConfig::getConfigKey, key)
                .one();
        if (config != null) {
            config.setConfigValue(value);
            updateById(config);
        } else {
            config = new SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            save(config);
        }
        redisTemplate.opsForValue().set(CACHE_PREFIX + key, value, CACHE_TTL);
    }

    @Override
    public Map<String, String> getAllConfigs() {
        List<SystemConfig> list = list();
        Map<String, String> map = new LinkedHashMap<>();
        for (SystemConfig c : list) {
            map.put(c.getConfigKey(), c.getConfigValue());
        }
        return map;
    }

    @Override
    public void batchUpdate(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            setConfig(entry.getKey(), entry.getValue());
        }
    }
}
