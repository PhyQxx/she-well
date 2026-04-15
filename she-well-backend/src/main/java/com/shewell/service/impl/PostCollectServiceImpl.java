package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.PostCollectMapper;
import com.shewell.entity.PostCollect;
import com.shewell.service.PostCollectService;
import org.springframework.stereotype.Service;

@Service
public class PostCollectServiceImpl extends ServiceImpl<PostCollectMapper, PostCollect> implements PostCollectService {
}
