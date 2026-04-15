package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.UserAuditLogMapper;
import com.shewell.entity.UserAuditLog;
import com.shewell.service.UserAuditLogService;
import org.springframework.stereotype.Service;

@Service
public class UserAuditLogServiceImpl extends ServiceImpl<UserAuditLogMapper, UserAuditLog> implements UserAuditLogService {
}
