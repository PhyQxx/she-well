package com.shewell.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shewell.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
