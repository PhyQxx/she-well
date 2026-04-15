package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.HabitMapper;
import com.shewell.entity.Habit;
import com.shewell.service.HabitService;
import org.springframework.stereotype.Service;

@Service
public class HabitServiceImpl extends ServiceImpl<HabitMapper, Habit> implements HabitService {
}
