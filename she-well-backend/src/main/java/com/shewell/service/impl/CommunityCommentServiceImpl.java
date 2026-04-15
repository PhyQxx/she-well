package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.CommunityCommentMapper;
import com.shewell.entity.CommunityComment;
import com.shewell.service.CommunityCommentService;
import org.springframework.stereotype.Service;

@Service
public class CommunityCommentServiceImpl extends ServiceImpl<CommunityCommentMapper, CommunityComment> implements CommunityCommentService {
}
