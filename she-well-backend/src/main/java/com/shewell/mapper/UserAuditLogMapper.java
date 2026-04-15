package com.shewell.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shewell.entity.UserAuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAuditLogMapper extends BaseMapper<UserAuditLog> {
}
