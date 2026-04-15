package com.shewell.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shewell.entity.Reminder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReminderMapper extends BaseMapper<Reminder> {
}
