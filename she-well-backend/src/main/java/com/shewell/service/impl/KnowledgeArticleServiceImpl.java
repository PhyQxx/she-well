package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.KnowledgeArticleMapper;
import com.shewell.entity.KnowledgeArticle;
import com.shewell.service.KnowledgeArticleService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeArticleServiceImpl extends ServiceImpl<KnowledgeArticleMapper, KnowledgeArticle> implements KnowledgeArticleService {
}
