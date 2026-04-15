package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.PeriodStatsMapper;
import com.shewell.entity.PeriodStats;
import com.shewell.service.PeriodStatsService;
import org.springframework.stereotype.Service;

@Service
public class PeriodStatsServiceImpl extends ServiceImpl<PeriodStatsMapper, PeriodStats> implements PeriodStatsService {
}
