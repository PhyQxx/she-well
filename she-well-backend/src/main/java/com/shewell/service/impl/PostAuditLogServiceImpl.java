package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.PostAuditLogMapper;
import com.shewell.entity.PostAuditLog;
import com.shewell.service.PostAuditLogService;
import org.springframework.stereotype.Service;

@Service
public class PostAuditLogServiceImpl extends ServiceImpl<PostAuditLogMapper, PostAuditLog> implements PostAuditLogService {
}
