package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.UserRoleHistoryMapper;
import com.shewell.entity.UserRoleHistory;
import com.shewell.service.UserRoleHistoryService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleHistoryServiceImpl extends ServiceImpl<UserRoleHistoryMapper, UserRoleHistory> implements UserRoleHistoryService {
}
