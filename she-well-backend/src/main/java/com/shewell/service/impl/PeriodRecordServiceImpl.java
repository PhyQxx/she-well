package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.PeriodRecordMapper;
import com.shewell.entity.PeriodRecord;
import com.shewell.service.PeriodRecordService;
import org.springframework.stereotype.Service;

@Service
public class PeriodRecordServiceImpl extends ServiceImpl<PeriodRecordMapper, PeriodRecord> implements PeriodRecordService {
}
