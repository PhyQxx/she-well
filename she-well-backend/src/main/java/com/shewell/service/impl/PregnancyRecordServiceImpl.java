package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.PregnancyRecordMapper;
import com.shewell.entity.PregnancyRecord;
import com.shewell.service.PregnancyRecordService;
import org.springframework.stereotype.Service;

@Service
public class PregnancyRecordServiceImpl extends ServiceImpl<PregnancyRecordMapper, PregnancyRecord> implements PregnancyRecordService {
}
