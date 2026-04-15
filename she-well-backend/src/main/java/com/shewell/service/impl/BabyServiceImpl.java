package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.BabyMapper;
import com.shewell.entity.Baby;
import com.shewell.service.BabyService;
import org.springframework.stereotype.Service;

@Service
public class BabyServiceImpl extends ServiceImpl<BabyMapper, Baby> implements BabyService {
}
