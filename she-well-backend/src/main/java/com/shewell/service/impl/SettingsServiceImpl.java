package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.entity.Settings;
import com.shewell.mapper.SettingsMapper;
import com.shewell.service.SettingsService;
import org.springframework.stereotype.Service;

@Service
public class SettingsServiceImpl extends ServiceImpl<SettingsMapper, Settings> implements SettingsService {
}
