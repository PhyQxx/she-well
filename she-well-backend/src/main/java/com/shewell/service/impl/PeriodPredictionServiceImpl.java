package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.PeriodPredictionMapper;
import com.shewell.entity.PeriodPrediction;
import com.shewell.service.PeriodPredictionService;
import org.springframework.stereotype.Service;

@Service
public class PeriodPredictionServiceImpl extends ServiceImpl<PeriodPredictionMapper, PeriodPrediction> implements PeriodPredictionService {
}
