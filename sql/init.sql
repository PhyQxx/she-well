-- =============================================
-- SheWell 建表SQL (40张表)
-- 数据库: MySQL 8.0, 字符集: utf8mb4_unicode_ci
-- 生成时间: 2026-04-16
-- =============================================

DROP DATABASE IF EXISTS she_well;
CREATE DATABASE she_well DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
USE she_well;

-- =============================================
-- 一、用户与账户体系（5张）
-- =============================================

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基本信息';

CREATE TABLE `user_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `height` decimal(5,1) DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5,1) DEFAULT NULL COMMENT '体重(kg)',
  `blood_type` varchar(10) DEFAULT NULL COMMENT '血型',
  `menstrual_cycle` int NOT NULL DEFAULT 28 COMMENT '月经周期(天)',
  `period_duration` int NOT NULL DEFAULT 5 COMMENT '经期时长(天)',
  `last_period_date` date DEFAULT NULL COMMENT '最近经期开始日期',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `intro` varchar(255) DEFAULT NULL COMMENT '个人简介',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `fk_up_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户详细档案';

CREATE TABLE `user_settings` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `reminder_enabled` tinyint NOT NULL DEFAULT 1 COMMENT '经期提醒是否开启',
  `reminder_time` varchar(10) NOT NULL DEFAULT '09:00' COMMENT '每日提醒时间',
  `period_reminder_days` int NOT NULL DEFAULT 3 COMMENT '经期前几天开始提醒',
  `ovul_reminder` tinyint NOT NULL DEFAULT 1 COMMENT '排卵日提醒是否开启',
  `push_enabled` tinyint NOT NULL DEFAULT 1 COMMENT '推送通知是否开启',
  `theme` varchar(20) NOT NULL DEFAULT 'light' COMMENT '主题：light/dark',
  `language` varchar(20) NOT NULL DEFAULT 'zh_CN' COMMENT '语言',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `fk_us_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置';

CREATE TABLE `user_role_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `from_role` varchar(20) DEFAULT NULL COMMENT '原角色',
  `to_role` varchar(20) NOT NULL COMMENT '切换后角色',
  `switch_time` datetime NOT NULL COMMENT '切换时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_urh_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色切换记录';

-- =============================================
-- 二、周期与健康追踪（9张）
-- =============================================

CREATE TABLE `period_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `start_date` date NOT NULL COMMENT '经期开始日期',
  `end_date` date DEFAULT NULL COMMENT '经期结束日期',
  `duration` int DEFAULT NULL COMMENT '持续天数',
  `flow_level` tinyint DEFAULT NULL COMMENT '出血量等级：1=极少，2=少，3=中，4=多',
  `symptoms` json DEFAULT NULL COMMENT '症状JSON：["疲劳","腹痛","头痛"]',
  `notes` text COMMENT '备注',
  `is_physical_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否永久删除（0=否，1=是）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`,`start_date`),
  CONSTRAINT `fk_pr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='经期记录表';

CREATE TABLE `period_prediction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `predicted_start_date` date DEFAULT NULL COMMENT '预测下次经期开始日期',
  `predicted_end_date` date DEFAULT NULL COMMENT '预测经期结束日期',
  `predicted_next_date` date DEFAULT NULL COMMENT '预测下次经期开始日期（别名）',
  `fertile_start_date` date DEFAULT NULL COMMENT '易孕期开始日期',
  `fertile_end_date` date DEFAULT NULL COMMENT '易孕期结束日期',
  `ovulation_date` date DEFAULT NULL COMMENT '预测排卵日',
  `cycle_length` int NOT NULL DEFAULT 28 COMMENT '月经周期长度(天)',
  `period_length` int NOT NULL DEFAULT 5 COMMENT '经期长度(天)',
  `prediction_confidence` decimal(3,2) NOT NULL DEFAULT 0.85 COMMENT '预测置信度',
  `last_calculated` datetime DEFAULT NULL COMMENT '最后计算时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `fk_pp_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='经期预测表';

CREATE TABLE `period_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `stat_month` varchar(7) NOT NULL COMMENT '统计月份：YYYY-MM',
  `actual_days` int DEFAULT NULL COMMENT '实际经期天数',
  `predicted_days` int DEFAULT NULL COMMENT '预测经期天数',
  `cycle_length` int DEFAULT NULL COMMENT '实际周期长度',
  `period_length` int DEFAULT NULL COMMENT '实际经期长度',
  `regularity_score` decimal(3,2) DEFAULT NULL COMMENT '规律度评分(0-1)',
  `flow_distribution` json DEFAULT NULL COMMENT '流量分布JSON',
  `symptoms_summary` json DEFAULT NULL COMMENT '症状汇总JSON',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_month` (`user_id`,`stat_month`),
  CONSTRAINT `fk_ps_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='经期月度统计表';

CREATE TABLE `ovulation_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `record_date` date NOT NULL COMMENT '记录日期',
  `ovulation_status` tinyint NOT NULL DEFAULT 0 COMMENT '排卵状态：0=未确定，1=已确认',
  `ovulation_way` enum('threshold','bbt','lh','follicular') DEFAULT NULL COMMENT '判断方式',
  `confidence` tinyint DEFAULT NULL COMMENT '置信度(0-100)',
  `temperature` decimal(4,1) DEFAULT NULL COMMENT '体温(℃)',
  `lh_surge` tinyint DEFAULT NULL COMMENT 'LH激素峰值：0=无，1=有',
  `follicular_size` decimal(5,1) DEFAULT NULL COMMENT '卵泡大小(mm)',
  `symptoms` json DEFAULT NULL COMMENT '排卵症状JSON',
  `notes` text COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`,`record_date`),
  CONSTRAINT `fk_or_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='排卵记录表';

CREATE TABLE `bbt_temp_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `record_date` date NOT NULL COMMENT '测量日期',
  `temperature` decimal(4,1) NOT NULL COMMENT '体温值(℃)',
  `measurement_time` varchar(10) DEFAULT NULL COMMENT '测量时间：HH:mm',
  `measurement_method` enum('oral','vaginal','axillary') DEFAULT 'oral' COMMENT '测量方式',
  `sleep_quality` tinyint DEFAULT NULL COMMENT '睡眠质量(1-5)',
  `notes` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`,`record_date`),
  CONSTRAINT `fk_bbt_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基础体温记录表';

CREATE TABLE `intercourse_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `record_date` date NOT NULL COMMENT '同房日期',
  `conception_possible` tinyint DEFAULT NULL COMMENT '怀孕可能性：0=不可能，1=可能',
  `conception_source` tinyint DEFAULT NULL COMMENT '受孕推断来源：0=推测，1=lh试纸，2=bbt，3=b超',
  `notes` text COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`,`record_date`),
  CONSTRAINT `fk_ir_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='同房记录表';

CREATE TABLE `pregnancy_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `last_period_start` date DEFAULT NULL COMMENT '末次月经开始日期',
  `due_date` date DEFAULT NULL COMMENT '预产期',
  `conception_date` date DEFAULT NULL COMMENT '受孕日期',
  `current_week` int DEFAULT NULL COMMENT '当前孕周',
  `current_day` int DEFAULT NULL COMMENT '当前孕天',
  `pregnancy_status` enum('preparing','confirmed','ended') NOT NULL DEFAULT 'preparing' COMMENT '怀孕状态',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `end_reason` varchar(50) DEFAULT NULL COMMENT '结束原因',
  `baby_id` bigint DEFAULT NULL COMMENT '关联宝宝ID（怀孕结束后回填）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_pgr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='怀孕记录表';

CREATE TABLE `checkup_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pregnancy_id` bigint DEFAULT NULL COMMENT '怀孕记录ID',
  `checkup_date` date NOT NULL COMMENT '产检日期',
  `gestational_week` int DEFAULT NULL COMMENT '孕周',
  `hospital` varchar(100) DEFAULT NULL COMMENT '产检医院',
  `doctor` varchar(50) DEFAULT NULL COMMENT '产检医生',
  `diagnosis` text COMMENT '诊断结果',
  `attachments` json DEFAULT NULL COMMENT '附件URL列表JSON',
  `next_appointment` date DEFAULT NULL COMMENT '下次产检日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_pregnancy` (`user_id`,`pregnancy_id`),
  CONSTRAINT `fk_cup_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产检记录表';

CREATE TABLE `pregnancy_diary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pregnancy_id` bigint NOT NULL COMMENT '怀孕记录ID',
  `record_date` date NOT NULL COMMENT '记录日期',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '当日体重(kg)',
  `blood_pressure_systolic` smallint DEFAULT NULL COMMENT '收缩压(mmHg)',
  `blood_pressure_diastolic` smallint DEFAULT NULL COMMENT '舒张压(mmHg)',
  `blood_sugar` decimal(4,1) DEFAULT NULL COMMENT '血糖(mmol/L)',
  `fetal_movement_count` int DEFAULT NULL COMMENT '胎动次数',
  `mood` varchar(20) DEFAULT NULL COMMENT '心情',
  `symptoms` json DEFAULT NULL COMMENT '当日症状JSON',
  `notes` text COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_pregnancy_date` (`user_id`,`pregnancy_id`,`record_date`),
  CONSTRAINT `fk_pdg_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='孕期日记表';

-- =============================================
-- 三、健康打卡与体征（3张）
-- =============================================

CREATE TABLE `checkin_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `habit_id` bigint NOT NULL COMMENT '习惯ID',
  `checkin_date` date NOT NULL COMMENT '打卡日期',
  `value` varchar(50) DEFAULT NULL COMMENT '打卡值',
  `symptom` json DEFAULT NULL COMMENT '症状JSON',
  `notes` text COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_habit_date` (`user_id`,`habit_id`,`checkin_date`),
  CONSTRAINT `fk_cr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡记录表';

CREATE TABLE `habit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '习惯名称',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `target_type` enum('count','duration','boolean') NOT NULL COMMENT '目标类型',
  `target_value` varchar(20) DEFAULT NULL COMMENT '目标值',
  `color` varchar(20) DEFAULT NULL COMMENT '颜色',
  `reminder_time` varchar(10) DEFAULT NULL COMMENT '提醒时间',
  `reminder_enabled` tinyint NOT NULL DEFAULT 0 COMMENT '是否开启提醒',
  `category` varchar(20) DEFAULT NULL COMMENT '分类',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_h_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户习惯表';

CREATE TABLE `body_metrics_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `record_date` date NOT NULL COMMENT '记录日期',
  `metric_type` enum('weight','temperature','blood_pressure','blood_sugar') NOT NULL COMMENT '指标类型',
  `metric_value` decimal(6,2) NOT NULL COMMENT '指标值',
  `unit` varchar(20) NOT NULL COMMENT '单位',
  `source` enum('manual','checkin','bbt','pregnancy_diary') NOT NULL DEFAULT 'manual' COMMENT '数据来源',
  `notes` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date_type` (`user_id`,`record_date`,`metric_type`),
  CONSTRAINT `fk_bmr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一体征记录表';

-- =============================================
-- 四、成就体系（2张）
-- =============================================

CREATE TABLE `achievement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(50) NOT NULL COMMENT '成就编码',
  `name` varchar(50) NOT NULL COMMENT '成就名称',
  `description` text COMMENT '成就描述',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标URL',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `condition_type` varchar(50) DEFAULT NULL COMMENT '触发条件类型',
  `condition_value` varchar(100) DEFAULT NULL COMMENT '触发条件值',
  `reward_points` int NOT NULL DEFAULT 0 COMMENT '奖励积分',
  `display_order` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `version` int NOT NULL DEFAULT 1 COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成就定义表';

CREATE TABLE `user_achievement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `achievement_id` bigint NOT NULL COMMENT '成就ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0=进行中，1=已达成',
  `progress_current` int NOT NULL DEFAULT 0 COMMENT '当前进度',
  `progress_target` int DEFAULT NULL COMMENT '目标进度',
  `earned_time` datetime DEFAULT NULL COMMENT '达成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_achievement` (`user_id`,`achievement_id`),
  CONSTRAINT `fk_ua_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户成就表';

-- =============================================
-- 五、知识库（2张）
-- =============================================

CREATE TABLE `knowledge_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父分类ID',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `type` enum('period','pregnancy','mother','common') NOT NULL DEFAULT 'common' COMMENT '类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识分类表';

CREATE TABLE `knowledge_article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `title` varchar(200) NOT NULL COMMENT '文章标题',
  `summary` text COMMENT '文章摘要',
  `content` longtext NOT NULL COMMENT '文章内容(富文本)',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图URL',
  `author` varchar(50) DEFAULT NULL COMMENT '作者',
  `source` varchar(100) DEFAULT NULL COMMENT '来源',
  `tags` json DEFAULT NULL COMMENT '标签JSON数组',
  `read_count` int NOT NULL DEFAULT 0 COMMENT '阅读数',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int NOT NULL DEFAULT 0 COMMENT '收藏数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=草稿，1=已发布',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_publish_time` (`publish_time`),
  CONSTRAINT `fk_ka_category` FOREIGN KEY (`category_id`) REFERENCES `knowledge_category` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识文章表';

-- =============================================
-- 六、社区（7张）
-- =============================================

CREATE TABLE `community_topic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '话题名称',
  `description` text COMMENT '话题描述',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图',
  `post_count` int NOT NULL DEFAULT 0 COMMENT '帖子数',
  `follow_count` int NOT NULL DEFAULT 0 COMMENT '关注数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=正常',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区话题表';

CREATE TABLE `community_post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `topic_id` bigint DEFAULT NULL COMMENT '话题ID',
  `title` varchar(200) DEFAULT NULL COMMENT '帖子标题',
  `content` text NOT NULL COMMENT '帖子内容',
  `images` json DEFAULT NULL COMMENT '图片URL列表JSON',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `topic_ids` json DEFAULT NULL COMMENT '关联话题ID列表JSON',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评论数',
  `collect_count` int NOT NULL DEFAULT 0 COMMENT '收藏数',
  `share_count` int NOT NULL DEFAULT 0 COMMENT '分享数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=待审核，1=已发布，2=已删除',
  `is_top` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶',
  `is_essence` tinyint NOT NULL DEFAULT 0 COMMENT '是否精华',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_status_time` (`status`,`create_time`),
  CONSTRAINT `fk_cp_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子表';

CREATE TABLE `community_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父评论ID（0=顶级评论）',
  `content` text NOT NULL COMMENT '评论内容',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` int NOT NULL DEFAULT 0 COMMENT '回复数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=待审核，1=已发布，2=已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_cc_post` FOREIGN KEY (`post_id`) REFERENCES `community_post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区评论表';

CREATE TABLE `post_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` enum('post','comment','answer') NOT NULL COMMENT '点赞对象类型',
  `target_id` bigint NOT NULL COMMENT '点赞对象ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`),
  KEY `idx_target` (`target_type`,`target_id`),
  CONSTRAINT `fk_pl_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子点赞表';

CREATE TABLE `post_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` enum('post','article','question') NOT NULL COMMENT '收藏对象类型',
  `target_id` bigint NOT NULL COMMENT '收藏对象ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`),
  KEY `idx_target` (`target_type`,`target_id`),
  CONSTRAINT `fk_pco_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子收藏表';

CREATE TABLE `user_follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关注者用户ID',
  `target_user_id` bigint NOT NULL COMMENT '被关注者用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_user_id`),
  CONSTRAINT `fk_uf_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注表';

CREATE TABLE `user_read_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` enum('article','post') NOT NULL COMMENT '内容类型',
  `target_id` bigint NOT NULL COMMENT '内容ID',
  `read_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_read_at` (`read_at`),
  CONSTRAINT `fk_urh_read_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅读历史表';

-- =============================================
-- 七、专家问答（3张）
-- =============================================

CREATE TABLE `expert` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `specialty` varchar(100) NOT NULL COMMENT '专业领域',
  `hospital` varchar(100) DEFAULT NULL COMMENT '医院',
  `department` varchar(100) DEFAULT NULL COMMENT '科室',
  `professional_title` varchar(50) DEFAULT NULL COMMENT '职称',
  `license_image` varchar(255) NOT NULL COMMENT '执照图片URL',
  `certification_status` enum('pending','approved','rejected') NOT NULL DEFAULT 'pending' COMMENT '认证状态',
  `intro` text COMMENT '个人简介',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `answer_count` int NOT NULL DEFAULT 0 COMMENT '回答数',
  `follow_count` int NOT NULL DEFAULT 0 COMMENT '关注数',
  `rating` decimal(2,1) NOT NULL DEFAULT 5.0 COMMENT '评分',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=正常',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `certify_time` datetime DEFAULT NULL COMMENT '认证时间',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '驳回原因',
  `reject_count` tinyint NOT NULL DEFAULT 0 COMMENT '驳回次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_cert_status` (`certification_status`,`status`),
  CONSTRAINT `fk_exp_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专家信息表';

CREATE TABLE `expert_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '提问用户ID',
  `expert_id` bigint DEFAULT NULL COMMENT '指定专家ID',
  `category` varchar(50) DEFAULT NULL COMMENT '问题分类',
  `title` varchar(200) NOT NULL COMMENT '问题标题',
  `content` text NOT NULL COMMENT '问题内容',
  `images` json DEFAULT NULL COMMENT '图片URL列表JSON',
  `status` enum('pending','answered','closed','expired') NOT NULL DEFAULT 'pending' COMMENT '状态',
  `is_anonymous` tinyint NOT NULL DEFAULT 0 COMMENT '是否匿名提问',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `answer_count` int NOT NULL DEFAULT 0 COMMENT '回答数',
  `adopted_answer_id` bigint DEFAULT NULL COMMENT '已采纳回答ID',
  `reward_points` int NOT NULL DEFAULT 0 COMMENT '悬赏积分',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `closed_at` datetime DEFAULT NULL COMMENT '关闭时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_expert_id` (`expert_id`),
  KEY `idx_status_time` (`status`,`create_time`),
  CONSTRAINT `fk_eq_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专家提问表';

CREATE TABLE `expert_answer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `expert_id` bigint NOT NULL COMMENT '回答专家ID',
  `content` text NOT NULL COMMENT '回答内容',
  `images` json DEFAULT NULL COMMENT '图片URL列表JSON',
  `is_adopted` tinyint NOT NULL DEFAULT 0 COMMENT '是否被采纳',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=隐藏，1=正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_expert_id` (`expert_id`),
  CONSTRAINT `fk_ea_question` FOREIGN KEY (`question_id`) REFERENCES `expert_question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专家回答表';

-- =============================================
-- 八、姐妹问答（2张）
-- =============================================

CREATE TABLE `community_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '提问用户ID',
  `title` varchar(200) NOT NULL COMMENT '问题标题',
  `content` text NOT NULL COMMENT '问题正文',
  `images` json DEFAULT NULL COMMENT '图片URL列表JSON',
  `category` varchar(50) DEFAULT NULL COMMENT '问题分类',
  `status` enum('pending','published','closed','deleted') NOT NULL DEFAULT 'published' COMMENT '状态',
  `is_anonymous` tinyint NOT NULL DEFAULT 0 COMMENT '是否匿名提问',
  `is_top` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶',
  `is_essence` tinyint NOT NULL DEFAULT 0 COMMENT '是否精华',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `answer_count` int NOT NULL DEFAULT 0 COMMENT '回答数',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int NOT NULL DEFAULT 0 COMMENT '收藏数',
  `last_answer_time` datetime DEFAULT NULL COMMENT '最后回答时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status_published` (`status`,`create_time`),
  KEY `idx_category` (`category`),
  CONSTRAINT `fk_cq_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='姐妹问答问题表';

CREATE TABLE `community_answer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `question_id` bigint NOT NULL COMMENT '关联问题ID',
  `user_id` bigint NOT NULL COMMENT '回答用户ID',
  `content` text NOT NULL COMMENT '回答正文',
  `images` json DEFAULT NULL COMMENT '图片URL列表JSON',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `is_adopted` tinyint NOT NULL DEFAULT 0 COMMENT '是否被提问者采纳',
  `adopted_time` datetime DEFAULT NULL COMMENT '采纳时间',
  `status` enum('published','hidden','deleted') NOT NULL DEFAULT 'published' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回答时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_adopted` (`is_adopted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_ca_question` FOREIGN KEY (`question_id`) REFERENCES `community_question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='姐妹问答回答表';

-- =============================================
-- 九、通知与内容运营（3张）
-- =============================================

CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(30) DEFAULT NULL COMMENT '通知类型',
  `title` varchar(100) DEFAULT NULL COMMENT '通知标题',
  `content` text COMMENT '通知内容',
  `data` json DEFAULT NULL COMMENT '扩展数据JSON',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_unread` (`user_id`,`is_read`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_n_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知记录表';

CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) DEFAULT NULL COMMENT '轮播图标题',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `link_type` enum('article','activity','custom') DEFAULT NULL COMMENT '跳转类型',
  `link_value` varchar(255) DEFAULT NULL COMMENT '跳转值（文章ID/活动ID/URL）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `start_time` datetime DEFAULT NULL COMMENT '展示开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '展示结束时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发现页轮播图表';

CREATE TABLE `activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '活动标题',
  `type` enum('checkin','share','expert','other') DEFAULT NULL COMMENT '活动类型',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图URL',
  `description` text COMMENT '活动描述',
  `rules` json DEFAULT NULL COMMENT '活动规则JSON',
  `start_time` datetime NOT NULL COMMENT '活动开始时间',
  `end_time` datetime NOT NULL COMMENT '活动结束时间',
  `reward_points` int NOT NULL DEFAULT 0 COMMENT '参与奖励积分',
  `participant_count` int NOT NULL DEFAULT 0 COMMENT '参与人数',
  `max_participants` int DEFAULT NULL COMMENT '最大参与人数（NULL表示不限）',
  `status` enum('draft','published','ongoing','ended','cancelled') NOT NULL DEFAULT 'draft' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_time` (`status`,`start_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- =============================================
-- 十、健康与宝宝（2张）
-- =============================================

CREATE TABLE `health_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `report_type` enum('period','pregnancy','body') NOT NULL COMMENT '报告类型',
  `period_start` date DEFAULT NULL COMMENT '经期开始日期',
  `period_end` date DEFAULT NULL COMMENT '经期结束日期',
  `avg_cycle_length` decimal(4,1) DEFAULT NULL COMMENT '平均周期长度',
  `avg_period_length` decimal(4,1) DEFAULT NULL COMMENT '平均经期长度',
  `regularity_score` decimal(3,2) DEFAULT NULL COMMENT '规律度评分',
  `symptom_summary` json DEFAULT NULL COMMENT '症状汇总',
  `advice` text COMMENT '健康建议',
  `start_date` date DEFAULT NULL COMMENT '报告开始日期',
  `end_date` date DEFAULT NULL COMMENT '报告结束日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_hr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康报告表';

CREATE TABLE `baby` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（妈妈）',
  `name` varchar(50) DEFAULT NULL COMMENT '宝宝姓名',
  `birth_date` date NOT NULL COMMENT '出生日期',
  `gender` enum('male','female','unknown') NOT NULL DEFAULT 'unknown' COMMENT '性别',
  `birth_height` decimal(5,1) DEFAULT NULL COMMENT '出生身高(cm)',
  `birth_weight` decimal(5,2) DEFAULT NULL COMMENT '出生体重(kg)',
  `delivery_type` enum('natural','cesarean','unknown') NOT NULL DEFAULT 'unknown' COMMENT '分娩方式',
  `blood_type` varchar(10) DEFAULT NULL COMMENT '血型',
  `birth_order` tinyint NOT NULL DEFAULT 1 COMMENT '出生顺序（第几胎）',
  `is_primary` tinyint NOT NULL DEFAULT 0 COMMENT '是否主要宝宝',
  `notes` text COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_baby_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宝宝信息表';

-- =============================================
-- 十一、日志与审计（2张）
-- =============================================

CREATE TABLE `post_audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `operator_id` bigint DEFAULT NULL COMMENT '审核员ID',
  `action` enum('submit','pass','reject','recall') NOT NULL COMMENT '操作类型',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '驳回原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  CONSTRAINT `fk_pal_post` FOREIGN KEY (`post_id`) REFERENCES `community_post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子审核记录表';

CREATE TABLE `user_audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `action_type` varchar(50) DEFAULT NULL COMMENT '操作类型',
  `target_type` varchar(50) DEFAULT NULL COMMENT '操作对象类型',
  `target_id` bigint DEFAULT NULL COMMENT '操作对象ID',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text COMMENT 'User-Agent',
  `detail` json DEFAULT NULL COMMENT '操作详情JSON',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_action_type` (`action_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户操作日志表';

-- =============================================
-- 十二、用户收藏（1张）
-- =============================================

CREATE TABLE `user_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` enum('article','post','expert_question') NOT NULL COMMENT '收藏对象类型',
  `target_id` bigint NOT NULL COMMENT '收藏对象ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`),
  KEY `idx_target` (`target_type`,`target_id`),
  CONSTRAINT `fk_uc_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- =============================================
-- 十三、补充表（对应代码 Entity 但 init.sql 遗漏）
-- =============================================

-- 用户模式表（经期/备孕/怀孕/育儿模式 + 周期参数）
CREATE TABLE `user_mode` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `current_mode` varchar(20) NOT NULL DEFAULT 'period' COMMENT '当前模式：period/trying/pregnancy/nursing',
  `cycle_length` int NOT NULL DEFAULT 28 COMMENT '月经周期长度（天）',
  `period_duration` int NOT NULL DEFAULT 5 COMMENT '经期持续天数',
  `last_period_date` date DEFAULT NULL COMMENT '最近一次经期开始日期',
  `remind_time` varchar(10) DEFAULT '09:00' COMMENT '每日提醒时间 HH:mm',
  `notify_enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否开启通知',
  `cycle_reminder_days` int NOT NULL DEFAULT 1 COMMENT '经期提前几天提醒',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `fk_um_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户模式与周期参数表';

-- 健康提醒表
CREATE TABLE `reminder` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) NOT NULL DEFAULT 'period' COMMENT '提醒类型：period/ovulation/checkup/medicine/sleep/exercise/water/custom',
  `title` varchar(100) NOT NULL COMMENT '提醒标题',
  `content` varchar(255) DEFAULT NULL COMMENT '提醒内容',
  `remind_time` varchar(10) NOT NULL COMMENT '提醒时间 HH:mm',
  `remind_days` int DEFAULT 0 COMMENT '提前几天提醒（0=当天）',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：0=禁用 1=启用',
  `cycle_phase` tinyint DEFAULT 0 COMMENT '适用周期阶段：0=每天 1=经期 2=排卵期 3=黄体期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_rem_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康提醒表';

-- 发现页配置表
CREATE TABLE `discovery` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `sub_title` varchar(200) DEFAULT NULL COMMENT '副标题',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图URL',
  `link_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `link_type` varchar(20) DEFAULT 'url' COMMENT '链接类型：article/post/activity/url',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序，数字越大越靠前',
  `status` varchar(20) NOT NULL DEFAULT 'enabled' COMMENT '状态：enabled/disabled',
  `start_time` datetime DEFAULT NULL COMMENT '展示开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '展示结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_sort` (`status`,`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发现页配置表';

-- 生育力评估记录表
CREATE TABLE `fertility_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `record_date` date NOT NULL COMMENT '记录日期',
  `level` varchar(10) NOT NULL COMMENT '生育力等级：low/medium/high',
  `score` decimal(5,2) DEFAULT NULL COMMENT '评分 0-100',
  `factors` json DEFAULT NULL COMMENT '影响因素 JSON数组',
  `suggestions` json DEFAULT NULL COMMENT '改善建议 JSON数组',
  `cycle_length_ref` varchar(20) DEFAULT NULL COMMENT '周期长度参考',
  `ovulation_status` varchar(50) DEFAULT NULL COMMENT '排卵状况描述',
  `conception_chance` varchar(10) DEFAULT NULL COMMENT '受孕几率：low/medium/high',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`,`record_date`),
  CONSTRAINT `fk_fr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生育力评估记录表';

-- =============================================
-- 初始化数据
-- =============================================

INSERT INTO `knowledge_category` (`name`, `parent_id`, `sort_order`, `type`) VALUES
('经期管理', 0, 1, 'period'),
('排卵监测', 0, 2, 'period'),
('备孕指南', 0, 3, 'pregnancy'),
('孕早期', 0, 4, 'pregnancy'),
('孕中期', 0, 5, 'pregnancy'),
('孕晚期', 0, 6, 'pregnancy'),
('分娩准备', 0, 7, 'pregnancy'),
('产后恢复', 0, 8, 'mother'),
('宝宝护理', 0, 9, 'mother'),
('营养食谱', 0, 10, 'mother'),
('运动健身', 0, 11, 'common'),
('心理健康', 0, 12, 'common');
