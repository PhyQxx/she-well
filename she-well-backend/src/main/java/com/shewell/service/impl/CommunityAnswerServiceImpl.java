package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.entity.CommunityAnswer;
import com.shewell.mapper.CommunityAnswerMapper;
import com.shewell.service.CommunityAnswerService;
import org.springframework.stereotype.Service;

@Service
public class CommunityAnswerServiceImpl extends ServiceImpl<CommunityAnswerMapper, CommunityAnswer> implements CommunityAnswerService {
}
