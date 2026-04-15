package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.OvulationRecordMapper;
import com.shewell.entity.OvulationRecord;
import com.shewell.service.OvulationRecordService;
import org.springframework.stereotype.Service;

@Service
public class OvulationRecordServiceImpl extends ServiceImpl<OvulationRecordMapper, OvulationRecord> implements OvulationRecordService {
}
