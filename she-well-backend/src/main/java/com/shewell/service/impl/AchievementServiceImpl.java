package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.AchievementMapper;
import com.shewell.entity.Achievement;
import com.shewell.service.AchievementService;
import org.springframework.stereotype.Service;

@Service
public class AchievementServiceImpl extends ServiceImpl<AchievementMapper, Achievement> implements AchievementService {
}
