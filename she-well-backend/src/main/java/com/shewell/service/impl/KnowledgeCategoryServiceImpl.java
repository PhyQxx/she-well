package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.KnowledgeCategoryMapper;
import com.shewell.entity.KnowledgeCategory;
import com.shewell.service.KnowledgeCategoryService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeCategoryServiceImpl extends ServiceImpl<KnowledgeCategoryMapper, KnowledgeCategory> implements KnowledgeCategoryService {
}
