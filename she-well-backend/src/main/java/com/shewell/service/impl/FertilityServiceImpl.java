package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.entity.Fertility;
import com.shewell.mapper.FertilityMapper;
import com.shewell.service.FertilityService;
import org.springframework.stereotype.Service;

@Service
public class FertilityServiceImpl extends ServiceImpl<FertilityMapper, Fertility> implements FertilityService {
}
