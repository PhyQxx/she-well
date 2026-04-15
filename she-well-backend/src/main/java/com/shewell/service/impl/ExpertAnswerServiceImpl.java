package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.ExpertAnswerMapper;
import com.shewell.entity.ExpertAnswer;
import com.shewell.service.ExpertAnswerService;
import org.springframework.stereotype.Service;

@Service
public class ExpertAnswerServiceImpl extends ServiceImpl<ExpertAnswerMapper, ExpertAnswer> implements ExpertAnswerService {
}
