package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.CheckupRecordMapper;
import com.shewell.entity.CheckupRecord;
import com.shewell.service.CheckupRecordService;
import org.springframework.stereotype.Service;

@Service
public class CheckupRecordServiceImpl extends ServiceImpl<CheckupRecordMapper, CheckupRecord> implements CheckupRecordService {
}
