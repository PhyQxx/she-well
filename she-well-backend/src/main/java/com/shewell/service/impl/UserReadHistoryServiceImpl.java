package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.UserReadHistoryMapper;
import com.shewell.entity.UserReadHistory;
import com.shewell.service.UserReadHistoryService;
import org.springframework.stereotype.Service;

@Service
public class UserReadHistoryServiceImpl extends ServiceImpl<UserReadHistoryMapper, UserReadHistory> implements UserReadHistoryService {
}
