package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.entity.CommunityQuestion;
import com.shewell.mapper.CommunityQuestionMapper;
import com.shewell.service.CommunityQuestionService;
import org.springframework.stereotype.Service;

@Service
public class CommunityQuestionServiceImpl extends ServiceImpl<CommunityQuestionMapper, CommunityQuestion> implements CommunityQuestionService {
}
