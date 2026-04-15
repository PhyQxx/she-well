package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.HealthReportMapper;
import com.shewell.entity.HealthReport;
import com.shewell.service.HealthReportService;
import org.springframework.stereotype.Service;

@Service
public class HealthReportServiceImpl extends ServiceImpl<HealthReportMapper, HealthReport> implements HealthReportService {
}
