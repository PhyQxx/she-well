package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.UserAchievementMapper;
import com.shewell.entity.UserAchievement;
import com.shewell.service.UserAchievementService;
import org.springframework.stereotype.Service;

@Service
public class UserAchievementServiceImpl extends ServiceImpl<UserAchievementMapper, UserAchievement> implements UserAchievementService {
}
