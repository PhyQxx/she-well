package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.CheckinRecordMapper;
import com.shewell.entity.CheckinRecord;
import com.shewell.service.CheckinRecordService;
import org.springframework.stereotype.Service;

@Service
public class CheckinRecordServiceImpl extends ServiceImpl<CheckinRecordMapper, CheckinRecord> implements CheckinRecordService {
}
