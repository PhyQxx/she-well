package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.UserFollowMapper;
import com.shewell.entity.UserFollow;
import com.shewell.service.UserFollowService;
import org.springframework.stereotype.Service;

@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {
}
