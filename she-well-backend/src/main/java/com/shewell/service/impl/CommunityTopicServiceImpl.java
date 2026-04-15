package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.CommunityTopicMapper;
import com.shewell.entity.CommunityTopic;
import com.shewell.service.CommunityTopicService;
import org.springframework.stereotype.Service;

@Service
public class CommunityTopicServiceImpl extends ServiceImpl<CommunityTopicMapper, CommunityTopic> implements CommunityTopicService {
}
