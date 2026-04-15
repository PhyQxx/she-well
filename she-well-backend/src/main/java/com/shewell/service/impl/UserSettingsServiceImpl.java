package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.UserSettingsMapper;
import com.shewell.entity.UserSettings;
import com.shewell.service.UserSettingsService;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsServiceImpl extends ServiceImpl<UserSettingsMapper, UserSettings> implements UserSettingsService {
}
