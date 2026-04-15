package com.shewell.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shewell.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {
}
