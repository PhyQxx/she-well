package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.UserCollectMapper;
import com.shewell.entity.UserCollect;
import com.shewell.service.UserCollectService;
import org.springframework.stereotype.Service;

@Service
public class UserCollectServiceImpl extends ServiceImpl<UserCollectMapper, UserCollect> implements UserCollectService {
}
