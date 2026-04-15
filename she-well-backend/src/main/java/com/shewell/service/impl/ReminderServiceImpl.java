package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.entity.Reminder;
import com.shewell.mapper.ReminderMapper;
import com.shewell.service.ReminderService;
import org.springframework.stereotype.Service;

@Service
public class ReminderServiceImpl extends ServiceImpl<ReminderMapper, Reminder> implements ReminderService {
}
