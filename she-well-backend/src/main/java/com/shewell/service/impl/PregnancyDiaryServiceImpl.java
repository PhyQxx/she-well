package com.shewell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shewell.mapper.PregnancyDiaryMapper;
import com.shewell.entity.PregnancyDiary;
import com.shewell.service.PregnancyDiaryService;
import org.springframework.stereotype.Service;

@Service
public class PregnancyDiaryServiceImpl extends ServiceImpl<PregnancyDiaryMapper, PregnancyDiary> implements PregnancyDiaryService {
}
