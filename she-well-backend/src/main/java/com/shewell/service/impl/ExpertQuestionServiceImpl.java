package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.ExpertQuestionMapper;
import com.shewell.entity.ExpertQuestion;
import com.shewell.service.ExpertQuestionService;
import org.springframework.stereotype.Service;

@Service
public class ExpertQuestionServiceImpl extends ServiceImpl<ExpertQuestionMapper, ExpertQuestion> implements ExpertQuestionService {
}
