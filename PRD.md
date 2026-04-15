# SheWell 女性健康平台产品需求文档 (PRD)

| 项目 | 内容 |
|------|------|
| **项目名称** | SheWell |
| **项目类型** | 女性健康服务平台 |
| **技术栈** | Spring Boot + Vue + UniApp + MySQL |
| **目标用户** | 成年女性（18-45岁） |
| **产品定位** | 专注女性生理周期管理的免费健康平台 |
| **版本** | V4.0.0 |
| **最后更新** | 2026-04-16 |

---

## 一、产品概述

### 1.1 产品愿景

SheWell 致力于为女性提供专业、贴心、免费的生理健康服务。通过智能算法和简洁易用的界面，帮助女性科学管理生理周期、备孕怀孕、身心健康，让每一位女性都能更好地了解和管理自己的身体健康。

### 1.2 核心价值主张

- **专业可靠**：基于医学原理的周期算法，提供科学的健康建议
- **简洁易用**：界面友好，操作流畅，学习成本低
- **隐私保护**：严格保护用户生理数据隐私
- **完全免费**：全部功能免费使用，无付费墙

### 1.3 目标用户画像

| 群体 | 特征 | 需求 |
|------|------|------|
| 都市白领 | 工作压力大，作息不规律 | 周期提醒、健康打卡 |
| 备孕女性 | 积极备孕，需精确排卵检测 | 排卵追踪、智能建议 |
| 新手妈妈 | 产后恢复，关注身心健康 | 产后恢复指导、育儿知识 |
| 学生群体 | 初次接触生理健康管理 | 基础知识、周期记录 |

---

## 二、功能架构总览

```
SheWell 女性健康平台
├── 用户体系
│   ├── 注册登录
│   ├── 个人信息
│   ├── 角色选择（经期/备孕/怀孕/妈妈）
│   └── 设置与偏好
├── 周期管理
│   ├── 经期记录
│   ├── 经期预测
│   ├── 经期提醒
│   └── 周期统计
├── 排卵追踪
│   ├── 排卵日计算
│   ├── 排卵试纸记录
│   ├── 基础体温记录
│   └── 排卵症状记录
├── 备孕怀孕
│   ├── 备孕计划
│   ├── 同房记录
│   ├── 怀孕记录
│   └── 产检提醒
├── 安全期与易孕期
│   ├── 安全期计算
│   ├── 易孕期提示
│   └── 生育力评估
├── 健康打卡
│   ├── 每日打卡
│   ├── 习惯追踪
│   ├── 健康提醒
│   └── 成就系统
├── 数据统计
│   ├── 周期分析
│   ├── 健康报告
│   ├── 趋势图表
│   └── 对比历史
├── 知识库
│   ├── 生理知识
│   ├── 健康食谱
│   ├── 运动指导
│   └── 专家问答
├── 社区
│   ├── 话题广场
│   ├── 姐妹圈
│   ├── 问答互助
│   └── 经验分享
└── 发现页
    ├── 每日推荐
    ├── 热门话题
    ├── 活动中心
    └── 个性化内容
```

---

## 三、详细功能需求

## 3.1 用户体系

### 3.1.1 注册登录

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 手机号注册 | 支持+86手机号注册，验证码登录 | P0 |
| 第三方登录 | 微信、Apple ID、QQ登录 | P1 |
| 密码设置 | 设置/修改登录密码 | P1 |
| 忘记密码 | 通过验证码重置密码 | P1 |

### 3.1.2 个人信息

| 字段 | 类型 | 说明 |
|------|------|------|
| 昵称 | 字符串 | 用户昵称，用于社区展示 |
| 头像 | 图片 | 支持相册选择或拍照 |
| 生日 | 日期 | 用于年龄计算和周期预测 |
| 所在城市 | 选择 | 用于天气服务和本地内容 |
| 职业 | 选择 | 用于健康建议个性化 |

### 3.1.3 角色选择

| 角色 | 说明 | 关联功能 |
|------|------|----------|
| 经期管理 | 默认模式，管理普通经期 | 经期追踪、统计 |
| 备孕期 | 积极备孕模式 | 排卵追踪、备孕计划 |
| 怀孕期 | 已确认怀孕 | 产检提醒、孕期知识 |
| 妈妈期 | 产后妈妈 | 产后恢复、育儿知识 |

### 3.1.4 设置与偏好

- 消息通知设置（开启/关闭各类通知）
- 隐私设置（数据授权、生理数据展示偏好）
- 主题设置（浅色/深色/跟随系统）
- 清除缓存
- 关于我们
- 退出登录

---

## 3.2 周期管理

### 3.2.1 经期记录

| 功能 | 描述 |
|------|------|
| 开始/结束标记 | 一键记录经期开始和结束日期 |
| 经量记录 | 轻/中/重 三档记录经量 |
| 症状标签 | 痛经、头痛、恶心、情绪波动等预设标签 |
| 自定义备注 | 支持文字备注特殊经期情况 |
| 经期日记 | 每日可添加简短日记 |

### 3.2.2 经期预测

| 功能 | 描述 |
|------|------|
| 下次经期预测 | 基于历史数据预测下次经期 |
| 预测准确度 | 显示预测的可靠程度（基于数据积累） |
| 预测区间 | 显示可能的经期时间段 |

### 3.2.3 经期提醒

| 功能 | 描述 |
|------|------|
| 经期预计开始提醒 | 提前1-3天可设置提醒 |
| 经期持续提醒 | 每日提醒直到标记结束 |
| 换卫生巾提醒 | 每3-4小时提醒更换 |
| 经期结束提醒 | 确认经期已结束 |

### 3.2.4 周期统计

| 指标 | 说明 |
|------|------|
| 平均周期长度 | 过去6个周期的平均天数 |
| 平均经期长度 | 过去6个经期的平均天数 |
| 最近周期数 | 展示最近12个周期 |
| 周期规律度 | 评估周期是否规律 |

---

## 3.3 排卵追踪

### 3.3.1 排卵日计算

| 功能 | 描述 |
|------|------|
| 排卵日预测 | 基于周期和经期数据计算 |
| 排卵期窗口 | 显示5天排卵期+1天排卵日 |
| 受孕几率 | 显示每日受孕可能性 |
| LH峰值提示 | 结合试纸数据预测排卵 |

### 3.3.2 排卵试纸记录

| 功能 | 描述 |
|------|------|
| 拍照识别 | AI识别试纸结果（阳性/阴性/弱阳） |
| 手动记录 | 选择检测结果 |
| 试纸对比 | 与历史试纸结果对比 |
| 排卵确认 | 强阳后转弱确认已排卵 |

### 3.3.3 基础体温记录

| 功能 | 描述 |
|------|------|
| 体温记录 | 记录每日基础体温 |
| 体温曲线 | 绘制体温变化曲线 |
| 体温提醒 | 每日固定时间提醒测量 |
| 排卵判断 | 体温升高0.2-0.5℃提示排卵 |

### 3.3.4 排卵症状记录

| 症状标签 | 说明 |
|----------|------|
| 分泌物变化 | 水样/蛋清状/粘稠等 |
| 排卵痛 | 一侧下腹疼痛 |
| 乳房胀痛 | 排卵期乳房敏感 |
| 性欲增强 | 生理性性欲变化 |
| 轻微出血 | 排卵期出血 |

---

## 3.4 备孕怀孕

### 3.4.1 备孕计划

| 功能 | 描述 |
|------|------|
| 备孕目标 | 设置备孕开始时间和目标 |
| 排卵试纸方案 | 推荐试纸使用频率 |
| 同房安排 | 智能推荐最佳同房时间 |
| 叶酸提醒 | 每日叶酸补充提醒 |
| 备孕进度 | 显示备孕时长和状态 |

### 3.4.2 同房记录

| 功能 | 描述 |
|------|------|
| 同房时间 | 记录每次同房时间 |
| 同房质量 | 记录感觉（好/一般/不好） |
| 使用措施 | 是否使用避孕措施 |
| 备注 | 其他需要记录的信息 |

### 3.4.3 怀孕记录

| 功能 | 描述 |
|------|------|
| 末次月经 | 确认怀孕的末次月经日期 |
| 预产期计算 | 自动计算预产期 |
| 当前孕周 | 显示当前怀孕周数和天数 |
| 宝宝发育 | 每周胎儿发育状态 |
| 妈妈变化 | 每周身体变化 |

### 3.4.4 产检提醒

| 功能 | 描述 |
|------|------|
| 产检时间表 | 根据孕周自动生成产检计划 |
| 产检提醒 | 提前提醒下次产检 |
| 产检项目 | 每次产检项目说明 |
| 产检记录 | 记录产检结果 |

---

## 3.5 安全期与易孕期

### 3.5.1 安全期计算

| 功能 | 描述 |
|------|------|
| 月经安全期 | 经期结束后相对安全的天数 |
| 绝对安全期 | 受孕概率最低的几天 |
| 安全期提醒 | 安全期开始/结束提示 |

### 3.5.2 易孕期提示

| 功能 | 描述 |
|------|------|
| 易孕期预测 | 每月易孕期时间段 |
| 易孕日提醒 | 排卵日前后重点提示 |
| 受孕几率 | 每日受孕可能性展示 |

### 3.5.3 生育力评估

| 功能 | 描述 |
|------|------|
| 生育力指数 | 综合评估生育能力 |
| 影响因素 | 分析影响生育力的因素 |
| 改善建议 | 提供提升生育力的建议 |

---

## 3.6 健康打卡

### 3.6.1 每日打卡

| 打卡项目 | 说明 |
|----------|------|
| 喝水提醒 | 每日8杯水目标 |
| 运动打卡 | 每日运动时长记录 |
| 睡眠打卡 | 早睡早起习惯记录 |
| 早餐打卡 | 是否吃早餐 |
| 心情打卡 | 每日心情状态 |
| 维生素打卡 | 营养补充记录 |

### 3.6.2 习惯追踪

| 功能 | 描述 |
|------|------|
| 习惯列表 | 自定义添加需要追踪的习惯 |
| 完成情况 | 每日/每周/每月完成率 |
| 连续记录 | 显示连续完成天数 |
| 习惯统计 | 习惯坚持情况分析 |

### 3.6.3 健康提醒

| 功能 | 描述 |
|------|------|
| 喝水提醒 | 每2小时提醒喝水 |
| 运动提醒 | 固定时间提醒运动 |
| 早睡提醒 | 设置睡觉时间提醒 |
| 吃药提醒 | 定时提醒服药 |
| 自定义提醒 | 用户设置提醒时间和内容 |

### 3.6.4 成就系统

| 成就类型 | 说明 |
|----------|------|
| 连续打卡 | 坚持打卡7/30/100天 |
| 首次记录 | 首次使用各项功能 |
| 数据完整 | 记录满1/3/6个月数据 |
| 社区活跃 | 发帖/回帖达到一定数量 |
| 健康达人 | 各项健康指标达标 |

---

## 3.7 数据统计

### 3.7.1 周期分析

| 图表类型 | 说明 |
|----------|------|
| 周期日历 | 每月周期可视化展示 |
| 周期对比 | 与历史周期对比 |
| 经期长度分布 | 经期天数分布图 |
| 周期长度分布 | 周期天数分布图 |
| 症状统计 | 各症状出现频率 |

### 3.7.2 健康报告

| 报告类型 | 说明 |
|----------|------|
| 月度报告 | 每月健康数据总结 |
| 季度报告 | 季度健康趋势分析 |
| 年度报告 | 年度健康大数据 |
| 特殊报告 | 异常情况专项报告 |

### 3.7.3 趋势图表

| 图表 | 说明 |
|------|------|
| 周期趋势 | 周期长度变化趋势 |
| 经期趋势 | 经期长度和经量变化 |
| 症状趋势 | 症状随周期变化规律 |
| 体重趋势 | 体重变化记录 |
| 心情趋势 | 心情周期变化 |

### 3.7.4 对比历史

| 功能 | 描述 |
|------|------|
| 同期对比 | 与上月/去年同月对比 |
| 目标对比 | 与设定目标对比 |
| 平均值对比 | 与同年龄段平均值对比 |

---

## 3.8 知识库

### 3.8.1 生理知识

| 分类 | 内容 |
|------|------|
| 月经知识 | 经期护理、痛经原因与缓解 |
| 排卵知识 | 排卵原理、排卵期症状 |
| 妇科知识 | 常见妇科疾病预防 |
| 用药知识 | 避孕药、止痛药使用指南 |

### 3.8.2 健康食谱

| 分类 | 说明 |
|------|------|
| 经期食谱 | 经期补血、缓解痛经食谱 |
| 排卵期食谱 | 有利于卵子质量的食物 |
| 备孕食谱 | 助孕营养餐 |
| 孕期食谱 | 各孕期营养需求 |
| 美容食谱 | 养颜美肤食谱 |

### 3.8.3 运动指导

| 分类 | 说明 |
|------|------|
| 经期运动 | 经期可做的舒缓运动 |
| 排卵期运动 | 促进排卵的运动 |
| 备孕运动 | 提高生育力的运动 |
| 孕期运动 | 孕期各阶段适合的运动 |
| 产后修复 | 产后身材恢复运动 |

### 3.8.4 专家问答

| 功能 | 描述 |
|------|------|
| 常见问题 | 整理高频问题及解答 |
| 专家专栏 | 妇科医生专业解答 |
| 用户问答 | 用户提问，医生或用户回答 |
| 健康资讯 | 每日健康小知识推送 |

---

## 3.9 社区

### 3.9.1 话题广场

| 功能 | 描述 |
|------|------|
| 热门话题 | 当前最热讨论话题 |
| 话题分类 | 经期/备孕/怀孕/育儿等分类 |
| 话题订阅 | 订阅感兴趣的话题 |
| 话题榜 | 每周/每月话题热度榜 |

### 3.9.2 姐妹圈

| 功能 | 描述 |
|------|------|
| 动态发布 | 发布图文/视频动态 |
| 互动功能 | 点赞、评论、收藏 |
| 话题标签 | 添加话题标签 |
| 位置定位 | 添加当前位置（可选） |
| @功能 | @其他用户 |

### 3.9.3 问答互助

| 功能 | 描述 |
|------|------|
| 发布问题 | 描述问题并选择分类 |
| 回答问题 | 用户间互帮互助 |
| 专家回答 | 认证医生专业解答 |
| 问题采纳 | 提问者采纳最佳答案 |
| 问题打赏 | 优质回答可获得打赏 |

### 3.9.4 经验分享

| 分类 | 说明 |
|------|------|
| 备孕经验 | 成功备孕经验分享 |
| 怀孕经历 | 孕期日记和经验 |
| 产后恢复 | 产后恢复心得 |
| 减肥塑形 | 产后减肥经验 |
| 情感生活 | 夫妻关系、婆媳关系 |

---

## 3.10 发现页

### 3.10.1 每日推荐

| 内容 | 说明 |
|------|------|
| 推荐文章 | 基于用户状态推荐内容 |
| 推荐话题 | 推荐用户可能感兴趣的话题 |
| 推荐用户 | 推荐可能想关注的用户 |
| 每日一问 | 每日推送趣味问答 |

### 3.10.2 热门内容

| 功能 | 描述 |
|------|------|
| 热门文章 | 点击量最高的文章 |
| 热门动态 | 获赞最多的动态 |
| 热门问答 | 关注度最高的问题 |
| 新增内容 | 最新发布的内容 |

### 3.10.3 活动中心

| 活动类型 | 说明 |
|----------|------|
| 打卡挑战 | 21天打卡挑战等活动 |
| 知识竞赛 | 健康知识问答竞赛 |
| 话题活动 | 节日/热点话题活动 |
| 奖励活动 | 积分/勋章奖励活动 |

### 3.10.4 个性化内容

| 功能 | 描述 |
|------|------|
| 角色适配 | 根据用户角色推送内容 |
| 周期适配 | 根据当前周期阶段推送 |
| 兴趣适配 | 根据用户浏览历史推荐 |
| 地域适配 | 推送本地相关内容 |

---

## 四、非功能需求

### 4.1 性能需求

| 指标 | 要求 |
|------|------|
| APP启动时间 | 冷启动不超过3秒 |
| 页面响应时间 | 不超过1秒 |
| 数据同步 | 支持离线记录，在线同步 |
| 图片加载 | 支持懒加载，缩略图预览 |

### 4.2 兼容性需求

| 平台 | 要求 |
|------|------|
| iOS | iOS 12.0及以上 |
| Android | Android 8.0及以上 |
| 微信小程序 | 微信7.0及以上 |
| Web | Chrome/Firefox/Safari/Edge最新版 |

### 4.3 客户端功能边界

#### PC Web 端（管理后台 + 内容消费）

**核心场景**：
- 用户个人数据总览（周期日历、统计图表）
- 社区内容浏览与发帖/回帖
- 知识库文章阅读
- 个人设置管理（头像、档案、提醒设置）
- 健康报告查看与导出

**不做**：
- 每日打卡（移动端更方便）
- 推送提醒（依赖移动设备）
- 基础体温/试纸记录（移动端更顺手）

**交互特点**：鼠标悬停提示、数据表格展示、批量操作、多窗口

#### 移动端（UniApp / 主战场）

**核心场景**：
- 日常记录（经期打卡、排卵试纸拍照、体温记录、同房记录）
- 每日健康打卡
- 推送提醒接收与处理
- 社区浏览、点赞、评论
- 知识库浏览
- 个人中心

**交互特点**：单手操作、快捷入口、底部Tab导航、 swipe手势

#### 两端数据完全同步
- 用户在任一端操作，数据实时同步
- 登录态互通（扫码登录PC端）
- 推送在移动端处理，PC端不推送

### 4.4 社区内容审核机制

#### 发帖审核流程

```
用户发布帖子
     ↓
帖子进入待审核状态（status='pending'）
     ↓
┌──────────────────────────────────────┐
│  Step 1：敏感词机审（自动）           │
│  - 含敏感词 → 直接拒绝                │
│  - 无敏感词 → 进入人工审核队列         │
└──────────────────────────────────────┘
     ↓
┌──────────────────────────────────────┐
│  Step 2：人工审核（运营平台）          │
│  - 通过 → status='published'          │
│  - 拒绝 → status='rejected' + 记录原因 │
└──────────────────────────────────────┘
     ↓
记录审核日志（post_audit_log）
```

**审核标准**：
- 严禁：涉黄/涉政/涉暴/虚假医疗信息/联系方式
- 限制：医疗建议类内容需加免责声明标签
- 引导：优质内容标记精华/置顶

**审核时效**：24小时内完成

#### 评论审核
- 机器审核 + 关键词过滤
- 被多人举报的评论自动下架待审

### 4.5 专家入驻认证机制

#### 认证流程

```
医生申请入驻
     ↓
提交资料：执业证照片 + 医院证明 + 个人简介 + 擅长领域
     ↓
后台运营初审（审核证件真实性）
     ↓
二审（医学专业人士或外部顾问）
     ↓
┌──────────────────────────────────────┐
│  通过 → 获得"认证医生"标识            │
│  不通过 → 短信通知申请人，15天内可申诉  │
└──────────────────────────────────────┘
```

**认证标识显示**：
- 头像框：金色"认证医生"徽章
- 文章/回答：显示"XX医院 XX科主治医生"
- 回答排序优先展示

**专家权益**：
- 专家专属回答入口
- 提问者可选"只让专家回答"
- 平台流量扶持

**专家管理**：
- 连续30天无活跃 → 取消认证标识
- 发布违规内容 → 永久取消认证资格

### 4.6 内容运营机制

#### 内容来源

| 内容类型 | 来源 | 说明 |
|----------|------|------|
| 知识库文章 | PGC（运营方录入）+ AIGC辅助 | 初期全部PGC，AI辅助扩充 |
| 专家专栏 | 认证医生原创 | 医生自行发布，运营审核 |
| 社区帖子 | UGC（用户创作） | 发帖需审核 |
| 健康食谱 | PGC + 认证营养师 | 附营养成分数据 |
| 运动指导 | PGC + 认证健身教练 | 附动作图解 |

#### AIGC使用规范
- AI生成内容必须标注"本文由AI辅助生成"
- 医疗相关AI内容必须经运营人工校对后才能发布
- AI不能替代医生进行诊断或治疗建议

#### 内容更新频率
- 知识库文章：每周至少新增/更新5篇
- 社区热门话题：每周更新话题榜
- Banner：按运营活动实时更新

### 4.7 安全性需求

| 需求 | 说明 |
|------|------|
| 数据加密 | 敏感数据AES加密存储 |
| 传输安全 | HTTPS全站加密 |
| 隐私保护 | 生理数据仅用户可见 |
| 注销功能 | 支持用户注销账户 |

### 4.8 可靠性需求

| 指标 | 要求 |
|------|------|
| 可用性 | 99.5%以上 |
| 错误率 | 低于0.1% |
| 数据备份 | 每日增量备份，每周全量备份 |

### 4.9 文件存储方案

#### 4.9.1 存储架构

**存储服务**：FTP 服务器（现成服务，部署于 NAS）

| 文件类型 | 存储路径规则 | 单文件上限 | 说明 |
|----------|-------------|-----------|------|
| 用户头像 | /avatar/{userId}/ | 2MB | 支持 JPG/PNG/WebP |
| 社区帖子图片 | /post/{userId}/{yyyyMM}/ | 5MB/张 | 支持 JPG/PNG/GIF，最多9张 |
| 知识文章封面 | /article/cover/ | 3MB | JPG/PNG |
| 基础体温试纸拍照 | /bbt/photo/{userId}/{yyyyMM}/ | 5MB | JPG |
| 排卵试纸拍照 | /ovulation/photo/{userId}/{yyyyMM}/ | 5MB | JPG |
| 产检报告附件 | /checkup/attachment/{userId}/{yyyyMM}/ | 10MB | JPG/PDF |
| 社区帖子视频 | /post/video/{userId}/{yyyyMM}/ | 100MB | MP4 |
| Banner图片 | /banner/ | 5MB | JPG/PNG |
| 专家资质证件 | /expert/license/ | 5MB | JPG/PDF |
| 用户认证证件 | /user/cert/ | 5MB | JPG/PDF |

**FTP 服务配置**：
- 服务器地址/端口/用户名/密码：通过配置文件管理，不硬编码
- 图片访问：通过 Nginx/反向代理对外提供 HTTP 访问，不直接暴露 FTP 地址
- 分片上传：大文件（>5MB）采用分片上传，支持断点续传

#### 4.9.2 图片处理策略

| 处理类型 | 规则 |
|----------|------|
| 上传压缩 | 超过 1MB 的图片自动压缩后再存储 |
| 缩略图 | 生成 200x200、400x400 两档缩略图用于列表展示 |
| 水印 | 用户上传图片自动添加半透明水印（防盗图） |
| 格式转换 | 统一转为 WebP 存储，节省 30% 存储空间 |
| CDN | 通过 Nginx 配置 /avatar、/post 等路径的缓存头（Cache-Control: max-age=30d） |

### 4.10 缓存策略

#### 4.10.1 Redis 缓存设计

| 缓存 Key 模式 | TTL | 说明 |
|--------------|-----|------|
| `prediction:{userId}` | 24h | 经期/排卵预测结果缓存，用户经期记录变化时主动失效 |
| `banner:list` | 5min | Banner 列表缓存，运营修改 Banner 后主动刷新 |
| `article:category:{categoryId}` | 10min | 分类下的文章列表缓存 |
| `article:featured` | 10min | 推荐文章列表缓存 |
| `hot:topics` | 30min | 热门话题缓存 |
| `user:profile:{userId}` | 30min | 用户档案缓存，修改时删除 |
| `user:settings:{userId}` | 30min | 用户设置缓存，修改时删除 |
| `community:post:{postId}:like_count` | 5min | 帖子点赞数缓存 |
| `community:post:{postId}:comment_count` | 5min | 帖子评论数缓存 |
| `ai:advice:{userId}` | 24h | AI 健康建议缓存，每天重新生成 |
| `achievement:progress:{userId}` | 1h | 成就进度缓存，打卡/发帖等操作后更新 |

#### 4.10.2 缓存失效策略

- **主动失效**：用户修改档案/设置/经期记录时，删除对应缓存 Key
- **被动失效**：Banner/文章/话题等运营内容修改时，通过后台管理界面主动刷新缓存
- **超时失效**：所有缓存设置 TTL兜底，防止脏数据永久残留

### 4.11 运营后台设计

#### 4.11.1 运营后台模块

| 模块 | 功能 |
|------|------|
| 内容管理 | Banner增删改、知识库文章发布/编辑/下架、话题创建/置顶/关闭 |
| 帖子管理 | 帖子审核（通过/拒绝）、帖子举报处理、帖子删除/禁言 |
| 用户管理 | 用户列表/搜索/禁用/注销、用户角色调整、用户数据导出 |
| 专家管理 | 专家入驻审核（看执业证/医院证明）、认证通过/拒绝、专家下架 |
| 问答管理 | 专家问答列表、异常问答处理 |
| 数据看板 | DAU/MAU/新增用户趋势、发帖量/评论量趋势、活跃用户留存 |
| 系统设置 | 公告管理、敏感词库维护、推送模板配置 |
| 健康报告模板 | 报告内容模板编辑（不同时期生成规则） |

#### 4.11.2 运营后台技术方案

- **复用后端**：运营后台复用同一套 Spring Boot 后端 API，通过不同路径区分（如 `/api/admin/*`）
- **权限控制**：运营后台独立权限体系（admin 角色），与用户端完全隔离
- **前端**：复用 PC Web 技术栈（Vue 3），但使用 Element Plus 管理后台模板（而非用户端的业务组件）
- **日志**：所有后台操作均记录操作日志（operator_id, action, target_id, before/after）

### 4.12 合规与法律声明

#### 4.12.1 医疗免责声明

**在以下场景强制展示免责声明**：

1. **经期/排卵预测结果页面**：页面底部显示
2. **AI 健康建议内容**：建议内容下方显示
3. **备孕建议页面**：显示"本建议仅供参考"
4. **知识库文章详情页**：文章底部显示
5. **专家问答回答内容**：回答内容后显示

**免责声明标准文本**：

```
⚠️ 医疗免责声明
本产品提供的所有健康信息（包括但不限于经期预测、排卵日计算、
备孕建议、孕期指导等）仅供参考，不构成医疗诊断、治疗建议或
替代专业医疗意见。
如有任何健康问题，请及时咨询执业医生。
```

#### 4.12.2 生育力评估说明

生育力评估功能（API: `/api/fertility/assessment`）**不输出具体概率数值**，输出内容为：

| 字段 | 类型 | 说明 |
|------|------|------|
| level | STRING | "low" / "medium" / "high"（低/中/高） |
| factors | ARRAY | 影响生育力的主要因素列表（最多3条） |
| suggestions | ARRAY | 改善建议列表（最多3条） |

**生育力等级说明**：

| 等级 | 含义 | 建议 |
|------|------|------|
| high | 生育力良好 | 保持现有健康生活方式 |
| medium | 生育力一般 | 建议调整生活习惯（如戒烟酒、控制体重） |
| low | 生育力较低 | 建议就医进一步检查 |

### 4.13 AI 健康助手模块

#### 4.13.1 产品定位与边界

| 维度 | 说明 |
|------|------|
| 产品定位 | 女性健康 AI 助手，不是医生 |
| 核心能力 | 健康咨询、症状分析、周期解读、试纸识别、个性化建议 |
| 能力边界 | 不能诊断、不能开方、不能替代就医 |
| 对标参考 | 美柚 AI 助手 / ChatGPT 健康问答（但更垂直） |

#### 4.13.2 AI 使用场景

| 场景 | 触发方式 | AI 响应形式 | 缓存策略 |
|------|---------|------------|---------|
| 健康咨询 | 用户输入问题 | 文字对话 + 底部免责声明 | 不缓存，每次实时生成 |
| 每日健康建议 | 首页下拉刷新 | 卡片式建议（饮食/运动/作息/注意） | Redis 24h |
| 周期分析报告 | 我的页面入口 | 长文本报告（规律性/异常/建议/预测） | Redis 24h |
| 试纸识别 | 拍照上传 | 结果页（阳性/阴性/弱阳 + LH值 + 置信度） | 不缓存 |
| 症状分析 | 打卡时输入症状 | 可选：AI 在建议处给出关联解读 | 不缓存 |

#### 4.13.3 System Prompt 设计

**基础角色设定**：

```
你是 SheWell 的 AI 健康助手，专为女性用户提供健康相关的
信息咨询和建议。你的目标是：
1. 用温暖、专业的语气回应
2. 每次回复末尾必须包含医疗免责声明
3. 不能给出诊断结论，只能提供健康信息参考
4. 遇到疑似严重症状（如异常出血、剧烈疼痛），提醒用户立即就医
5. 不回答与女性健康无关的问题
6. 如果用户询问的内容超出你的能力范围，礼貌拒绝并建议咨询医生
```

**场景化 Prompt 片段**：

```
【经期问题】
用户描述了[具体症状]。请从以下角度给出建议：
- 可能是哪些原因
- 日常生活注意事项
- 是否需要就医
回复末尾附加免责声明。

【排卵试纸识别】
用户上传了试纸照片，请判断：
- 结果：阳性 / 阴性 / 弱阳性
- LH 激素估算值（如果可判断）
- 置信度（高/中/低）
- 接下来建议（如同房时间建议）
回复末尾附加免责声明。

【周期分析】
用户近[N]个月的经期记录如下：[数据]。请生成：
- 周期规律性评估（规律/基本规律/不规律）
- 平均周期长度和经期长度
- 异常分析（如周期过短/过长/经期过长）
- 个性化建议
回复末尾附加免责声明。
```

#### 4.13.4 个性化上下文注入规则

AI 每次对话前，会自动注入以下上下文数据（从用户档案 + 实时数据中提取）：

| 数据源 | 注入内容 | 说明 |
|--------|---------|------|
| `user_profile` | 年龄、身高、体重、肤色、孕产史 | 仅首次对话注入 |
| `period_prediction` | 当前周期天数、距下次经期天数、距排卵日天数 | 实时获取 |
| `period_record` | 最近 3 条经期记录（开始/结束/持续/流量） | 实时获取 |
| `checkin_record` | 最近 3 天打卡（症状/心情/体温） | 实时获取 |
| `current_mode` | 当前模式：经期模式 / 备孕模式 / 怀孕模式 / 育儿模式 | 实时获取 |

**注入格式**：

```
【用户档案】
年龄：26岁 | 身高：163cm | 体重：52kg
月经周期：28天（参考值） | 经期长度：5天（参考值）

【当前状态】
模式：经期模式 | 周期第14天 | 距排卵日：约2天

【近期记录】
2026-04-10：乳房胀痛、疲劳 | 心情：😔 低落
2026-04-11：腹胀 | 心情：😐 一般
2026-04-12：轻微头痛 | 心情：😊 愉快

---
用户问题：[用户输入]
```

#### 4.13.5 AI 模型配置

| 模型 | API 地址 | 适用场景 | temperature |
|------|---------|---------|-------------|
| DeepSeek Chat | `https://api.deepseek.com/chat/completions` | 健康咨询、周期分析 | 0.7 |
| 智谱 GLM-4 | `https://open.bigmodel.cn/api/paas/v4/chat/completions` | 同上（国内访问） | 0.7 |
| MiniMax | `https://api.minimax.chat/v1/text/chatcompletion_v2` | 同上（备选） | 0.7 |

**配置方式**：
- 通过数据库 `system_config` 表或配置文件配置
- 支持用户自选：设置页 → AI 模型选择（默认 DeepSeek）
- API Key：通过环境变量或配置中心管理，不硬编码

#### 4.13.6 数据安全与隐私

| 规则 | 说明 |
|------|------|
| 对话不持久化 | AI 对话内容仅用于当前请求，不存储到数据库 |
| 健康数据最小化 | 注入 AI 的上下文仅包含必要的健康字段 |
| 传输加密 | AI API 调用必须使用 HTTPS |
| 日志脱敏 | 调用日志不记录用户问题原文，仅记录 userId + 调用时间 + 耗时 |

#### 4.13.7 免责声明展示规范

| 场景 | 展示方式 | 文本 |
|------|---------|------|
| AI 健康咨询回复 | 固定附在回复末尾 | `⚠️ 以上内容仅供参考，不能替代专业医生的诊断和治疗。如有不适，请及时就医。` |
| AI 每日建议卡片 | 卡片底部一行 | `⚠️ 本建议仅供参考` |
| AI 周期分析报告 | 报告结尾加粗显示 | `⚠️ 本报告仅供参考，不构成医疗诊断` |
| 试纸识别结果页 | 结果下方 | `⚠️ AI 识别结果仅供参考，请以临床检测为准` |

---

## 5. 数据字典

### 5.1 核心数据表

| 序号 | 表名 | 说明 |
|------|------|------|
| 1 | user | 用户基本信息 |
| 2 | user_profile | 用户详细档案 |
| 3 | user_settings | 用户设置 |
| 4 | user_role_history | 角色切换记录 |
| 5 | period_record | 经期记录 |
| 6 | period_prediction | 经期预测 |
| 7 | period_stats | 经期月度统计 |
| 8 | ovulation_record | 排卵记录 |
| 9 | bbt_temp_record | 基础体温记录 |
| 10 | intercourse_record | 同房记录 |
| 11 | pregnancy_record | 怀孕记录 |
| 12 | checkup_record | 产检记录 |
| 13 | pregnancy_diary | 孕期日记 |
| 14 | checkin_record | 打卡记录 |
| 15 | habit | 用户习惯 |
| 16 | body_metrics_record | 统一体征记录（体重/体温/血压/血糖） |
| 17 | achievement | 成就定义 |
| 18 | user_achievement | 用户成就 |
| 19 | knowledge_category | 知识分类 |
| 20 | knowledge_article | 知识文章 |
| 21 | user_read_history | 阅读历史 |
| 22 | user_collect | 用户收藏 |
| 23 | community_topic | 社区话题 |
| 24 | community_post | 社区帖子 |
| 25 | community_comment | 社区评论 |
| 26 | post_like | 帖子点赞 |
| 27 | post_collect | 帖子收藏 |
| 28 | user_follow | 用户关注 |
| 29 | expert | 专家信息 |
| 30 | expert_question | 专家提问 |
| 31 | expert_answer | 专家回答 |
| 32 | notification | 通知记录 |
| 33 | banner | 发现页轮播图 |
| 34 | health_report | 健康报告 |
| 35 | baby | 宝宝信息 |
| 36 | post_audit_log | 帖子审核记录 |
| 37 | user_audit_log | 用户操作日志 |
| 38 | activity | 活动表 |
| 39 | community_question | 姐妹问答问题 |
| 40 | community_answer | 姐妹问答回答 |

### 5.2 详细字段设计

#### 5.2.1 用户表 (user)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 用户唯一标识 | PK, AUTO_INCREMENT |
| phone | VARCHAR | 20 | 手机号 | UNIQUE, NOT NULL |
| password | VARCHAR | 128 | 密码（加密存储） | NOT NULL |
| nickname | VARCHAR | 50 | 昵称 | NOT NULL |
| avatar | VARCHAR | 255 | 头像URL | DEFAULT '' |
| user_role | ENUM | - | 角色类型 | 'period','trying','pregnant','mother', DEFAULT 'period' |
| status | TINYINT | 1 | 账号状态 | 0=禁用,1=正常, DEFAULT 1 |
| last_login_time | DATETIME | - | 最后登录时间 | - |
| last_login_ip | VARCHAR | 50 | 最后登录IP | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |
| delete_time | DATETIME | - | 删除时间（软删除） | - |

#### 5.2.2 用户档案表 (user_profile)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 档案ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id, UNIQUE |
| birthday | DATE | - | 生日 | NOT NULL |
| city | VARCHAR | 50 | 所在城市 | - |
| occupation | VARCHAR | 50 | 职业 | - |
| height | DECIMAL | 5,2 | 身高(cm) | - |
| weight | DECIMAL | 5,2 | 体重(kg) | - |
| menarche_age | TINYINT | 3 | 初潮年龄 | - |
| cycle_length | SMALLINT | 3 | 周期长度(天) | DEFAULT 28 |
| period_length | SMALLINT | 3 | 经期长度(天) | DEFAULT 5 |
| latest_period_start | DATE | - | 最近经期开始日期 | - |
| due_date | DATE | - | 预产期/目标日期 | - |
| partner_name | VARCHAR | 50 | 伴侣姓名 | - |
| partner_birthday | DATE | - | 伴侣生日 | - |
|过敏史 | TEXT | - | 过敏史 | - |
| 既往病史 | TEXT | - | 既往病史 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.3 经期记录表 (period_record)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| start_date | DATE | - | 经期开始日期 | NOT NULL |
| end_date | DATE | - | 经期结束日期 | - |
| cycle_day | SMALLINT | 3 | 周期第几天 | - |
| flow | ENUM | - | 经量 | 'light','medium','heavy' |
| flow_level | TINYINT | 1 | 经量等级(1-3) | 1=轻,2=中,3=重 |
| status | ENUM | - | 状态 | 'active','ended' |
| symptoms | JSON | - | 症状标签JSON | ['cramp','headache','nausea','mood_swings'] |
| notes | TEXT | - | 备注 | - |
| diary | TEXT | - | 经期日记 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.4 经期预测表 (period_prediction)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 预测ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id, UNIQUE |
| predicted_start | DATE | - | 预测经期开始日期 | NOT NULL |
| predicted_end | DATE | - | 预测经期结束日期 | - |
| prediction_start_range | DATE | - | 预测开始日期范围(最早) | - |
| prediction_end_range | DATE | - | 预测开始日期范围(最晚) | - |
| confidence | TINYINT | 3 | 预测准确度(0-100%) | DEFAULT 0 |
| avg_cycle_length | SMALLINT | 3 | 平均周期长度 | - |
| avg_period_length | SMALLINT | 3 | 平均经期长度 | - |
| cycle_count | SMALLINT | 3 | 用于预测的周期数 | DEFAULT 0 |
| ovulation_date | DATE | - | 预测排卵日 | - |
| fertile_start | DATE | - | 易孕期开始 | - |
| fertile_end | DATE | - | 易孕期结束 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.5 排卵记录表 (ovulation_record)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| record_date | DATE | - | 记录日期 | NOT NULL |
| ovulation_result | ENUM | - | 试纸结果 | 'negative','weak','positive','strong' |
| lh_value | DECIMAL | 4,2 | LH值 | - |
| ovulation_confirmed | TINYINT | 1 | 是否已确认排卵 | DEFAULT 0 |
| confirmed_date | DATE | - | 确认排卵日期 | - |
| symptoms | JSON | - | 排卵症状 | ['discharge','pain','breast_tenderness'] |
| notes | TEXT | - | 备注 | - |
| photo_url | VARCHAR | 255 | 试纸照片URL | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.6 基础体温记录表 (bbt_temp_record)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| record_date | DATE | - | 记录日期 | NOT NULL |
| temperature | DECIMAL | 4,2 | 体温值(℃) | NOT NULL |
| temperature_time | TIME | - | 测量时间 | - |
| cycle_day | SMALLINT | 3 | 周期第几天 | - |
| notes | VARCHAR | 255 | 备注 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.7 怀孕记录表 (pregnancy_record)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id, UNIQUE |
| last_period_start | DATE | - | 末次月经开始日期 | NOT NULL |
| due_date | DATE | - | 预产期 | NOT NULL |
| conception_date | DATE | - | 实际受孕日期 | - |
| current_week | TINYINT | 2 | 当前孕周 | DEFAULT 0 |
| current_day | TINYINT | 2 | 孕周第几天 | DEFAULT 0 |
| pregnancy_status | ENUM | - | 怀孕状态 | 'confirmed','checking','ended' |
| end_date | DATE | - | 怀孕结束日期 | - |
| end_reason | VARCHAR | 50 | 结束原因 | - |
| baby_id | BIGINT | 20 | 关联宝宝ID（怀孕结束后回填） | FK -> baby.id |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.8 产检记录表 (checkup_record)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| pregnancy_id | BIGINT | 20 | 怀孕记录ID | FK -> pregnancy_record.id |
| checkup_week | TINYINT | 2 | 产检孕周 | NOT NULL |
| checkup_date | DATE | - | 产检日期 | - |
| checkup_item | VARCHAR | 255 | 产检项目 | - |
| checkup_result | TEXT | - | 产检结果 | - |
| next_checkup_week | TINYINT | 2 | 下次产检孕周 | - |
| next_checkup_date | DATE | - | 下次产检日期 | - |
| notes | TEXT | - | 备注 | - |
| attachments | JSON | - | 附件(超声图片等) | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.9 打卡记录表 (checkin_record)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| checkin_date | DATE | - | 打卡日期 | NOT NULL |
| checkin_type | ENUM | - | 打卡类型 | 'water','sport','sleep','breakfast','mood','vitamin','other' |
| checkin_value | VARCHAR | 50 | 打卡值 | - |
| mood | ENUM | - | 心情状态 | 'happy','calm','anxious','sad','angry' |
| water_count | TINYINT | 2 | 喝水量(杯) | - |
| sport_duration | SMALLINT | 4 | 运动时长(分钟) | - |
| sleep_time | TIME | - | 睡眠时间 | - |
| wake_time | TIME | - | 起床时间 | - |
| notes | TEXT | - | 备注 | - |
| streak_days | INT | - | 连续打卡天数 | DEFAULT 0 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.10 用户习惯表 (habit)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 习惯ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| habit_name | VARCHAR | 50 | 习惯名称 | NOT NULL |
| habit_type | ENUM | - | 习惯类型 | 'water','sport','sleep','breakfast','mood','vitamin','other' |
| target_value | VARCHAR | 50 | 目标值 | - |
| target_days | TINYINT | 2 | 目标天数/周 | - |
| reminder_time | TIME | - | 提醒时间 | - |
| reminder_enabled | TINYINT | 1 | 是否启用提醒 | DEFAULT 1 |
| current_streak | INT | - | 当前连续天数 | DEFAULT 0 |
| longest_streak | INT | - | 最长连续天数 | DEFAULT 0 |
| total_completed | INT | - | 总完成次数 | DEFAULT 0 |
| status | TINYINT | 1 | 状态 | 0=停用,1=启用, DEFAULT 1 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.11 知识文章表 (knowledge_article)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 文章ID | PK, AUTO_INCREMENT |
| category | ENUM | - | 文章分类 | 'period','ovulation','pregnancy','mother','recipe','exercise' |
| title | VARCHAR | 200 | 文章标题 | NOT NULL |
| summary | VARCHAR | 500 | 文章摘要 | - |
| content | TEXT | - | 文章内容 | NOT NULL |
| cover_image | VARCHAR | 255 | 封面图片 | - |
| author | VARCHAR | 50 | 作者 | - |
| author_title | VARCHAR | 100 | 作者职称 | - |
| tags | JSON | - | 标签 | - |
| view_count | INT | - | 阅读数 | DEFAULT 0 |
| like_count | INT | - | 点赞数 | DEFAULT 0 |
| collect_count | INT | - | 收藏数 | DEFAULT 0 |
| is_featured | TINYINT | 1 | 是否推荐 | DEFAULT 0 |
| status | TINYINT | 1 | 状态 | 0=下架,1=上架, DEFAULT 1 |
| publish_time | DATETIME | - | 发布时间 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.12 社区帖子表 (community_post)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 帖子ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 发布用户ID | FK -> user.id |
| post_type | ENUM | - | 帖子类型 | 'dynamic','question','share','experience' |
| title | VARCHAR | 200 | 帖子标题 | - |
| content | TEXT | - | 帖子内容 | NOT NULL |
| images | JSON | - | 图片列表 | - |
| video_url | VARCHAR | 255 | 视频URL | - |
| topic_id | BIGINT | 20 | 话题ID | FK -> community_topic.id |
| topic_tags | JSON | - | 话题标签 | - |
| location | VARCHAR | 100 | 位置 | - |
| view_count | INT | - | 浏览数 | DEFAULT 0 |
| like_count | INT | - | 点赞数 | DEFAULT 0 |
| comment_count | INT | - | 评论数 | DEFAULT 0 |
| collect_count | INT | - | 收藏数 | DEFAULT 0 |
| share_count | INT | - | 分享数 | DEFAULT 0 |
| status | ENUM | - | 状态 | 'draft','published','deleted', DEFAULT 'published' |
| is_top | TINYINT | 1 | 是否置顶 | DEFAULT 0 |
| is_essence | TINYINT | 1 | 是否精华 | DEFAULT 0 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |
| delete_time | DATETIME | - | 删除时间 | - |

#### 5.2.13 社区评论表 (community_comment)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 评论ID | PK, AUTO_INCREMENT |
| post_id | BIGINT | 20 | 帖子ID | FK -> community_post.id |
| user_id | BIGINT | 20 | 评论用户ID | FK -> user.id |
| parent_id | BIGINT | 20 | 父评论ID | FK -> community_comment.id |
| content | TEXT | - | 评论内容 | NOT NULL |
| like_count | INT | - | 点赞数 | DEFAULT 0 |
| reply_count | INT | - | 回复数 | DEFAULT 0 |
| status | TINYINT | 1 | 状态 | 0=删除,1=正常, DEFAULT 1 |
| is_adopted | TINYINT | 1 | 是否被采纳 | DEFAULT 0 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |
| delete_time | DATETIME | - | 删除时间 | - |

#### 5.2.14 同房记录表 (intercourse_record)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| record_date | DATE | - | 同房日期 | NOT NULL |
| record_time | TIME | - | 同房时间 | - |
| quality | ENUM | - | 同房质量 | 'good','normal','poor' |
| contraception | TINYINT | 1 | 是否使用避孕措施 | 0=否,1=是 |
| is_conceived | TINYINT | 1 | 是否因此次受孕 | 0=否,1=是, DEFAULT 0 |
| notes | TEXT | - | 备注 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**索引**：idx_user_date(user_id, record_date)

#### 5.2.15 孕期日记表 (pregnancy_diary)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 日记ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| pregnancy_id | BIGINT | 20 | 关联怀孕记录ID | FK -> pregnancy_record.id |
| record_date | DATE | - | 记录日期 | NOT NULL |
| weight | DECIMAL | 5,2 | 当日体重（kg） | - |
| blood_pressure_systolic | SMALLINT | 3 | 收缩压（mmHg） | - |
| blood_pressure_diastolic | SMALLINT | 3 | 舒张压（mmHg） | - |
| blood_sugar | DECIMAL | 4,1 | 血糖值（mmol/L） | - |
| fetal_movement_count | INT | - | 胎动次数（孕28周后填写） | - |
| mood | VARCHAR | 20 | 当日心情 | - |
| symptoms | JSON | - | 当日不适症状数组 | - |
| notes | TEXT | - | 其他备注 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**复合唯一索引**：idx_user_pregnancy_date(user_id, pregnancy_id, record_date)

#### 5.2.16 统一体征记录表 (body_metrics_record)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| record_date | DATE | - | 记录日期 | NOT NULL |
| metric_type | ENUM | - | 体征类型 | 'weight','temperature','blood_pressure','blood_sugar' |
| metric_value | DECIMAL | 6,2 | 测量值 | NOT NULL |
| unit | VARCHAR | 20 | 单位 | NOT NULL |
| source | ENUM | - | 数据来源 | 'manual','checkin','bbt','pregnancy_diary' |
| notes | VARCHAR | 255 | 备注 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**复合唯一索引**：idx_user_date_type(user_id, record_date, metric_type)

#### 5.2.17 专家信息表 (expert)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 专家ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 关联用户ID | FK -> user.id, UNIQUE |
| real_name | VARCHAR | 50 | 真实姓名 | NOT NULL |
| specialty | VARCHAR | 100 | 擅长领域 | NOT NULL |
| hospital | VARCHAR | 100 | 所属医院 | - |
| department | VARCHAR | 100 | 科室 | - |
| professional_title | VARCHAR | 50 | 职称（主任医师/副主任医师等） | - |
| license_image | VARCHAR | 255 | 执业证书图片URL | NOT NULL |
| certification_status | ENUM | - | 认证状态 | 'pending','approved','rejected' |
| intro | TEXT | - | 个人简介 | - |
| avatar_url | VARCHAR | 255 | 专家头像 | - |
| answer_count | INT | - | 回答数 | DEFAULT 0 |
| follow_count | INT | - | 被关注数 | DEFAULT 0 |
| rating | DECIMAL | 2,1 | 平均评分 | DEFAULT 5.0 |
| status | TINYINT | 1 | 状态 | 0=禁用,1=启用, DEFAULT 1 |
| apply_time | DATETIME | - | 申请时间 | NOT NULL |
| certify_time | DATETIME | - | 认证时间 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.18 专家提问表 (expert_question)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 提问ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 提问用户ID | FK -> user.id |
| expert_id | BIGINT | 20 | 指定专家ID（NULL表示不限专家） | FK -> expert.id |
| category | VARCHAR | 50 | 问题分类 | - |
| title | VARCHAR | 200 | 问题标题 | NOT NULL |
| content | TEXT | - | 问题正文 | NOT NULL |
| images | JSON | - | 图片列表 | - |
| status | ENUM | - | 状态 | 'pending','answered','closed','expired', DEFAULT 'pending' |
| is_anonymous | TINYINT | 1 | 是否匿名 | DEFAULT 0 |
| view_count | INT | - | 浏览次数 | DEFAULT 0 |
| answer_count | INT | - | 回答数 | DEFAULT 0 |
| adopted_answer_id | BIGINT | 20 | 已采纳回答ID | FK -> expert_answer.id |
| reward_points | INT | - | 悬赏积分（0为免费提问） | DEFAULT 0 |
| expire_time | DATETIME | - | 问题过期时间（过期后不可回答） | - |
| closed_at | DATETIME | - | 关闭时间 | - |
| create_time | DATETIME | - | 提问时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**索引**：idx_user_id(user_id), idx_expert_id(expert_id), idx_status(status), idx_create_time(create_time)

#### 5.2.19 专家回答表 (expert_answer)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 回答ID | PK, AUTO_INCREMENT |
| question_id | BIGINT | 20 | 关联提问ID | FK -> expert_question.id |
| expert_id | BIGINT | 20 | 回答专家ID | FK -> expert.id |
| content | TEXT | - | 回答正文 | NOT NULL |
| images | JSON | - | 图片列表 | - |
| is_adopted | TINYINT | 1 | 是否被采纳 | DEFAULT 0 |
| like_count | INT | - | 点赞数 | DEFAULT 0 |
| status | TINYINT | 1 | 状态 | 0=隐藏,1=显示, DEFAULT 1 |
| create_time | DATETIME | - | 回答时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**索引**：idx_question_id(question_id), idx_expert_id(expert_id)

#### 5.2.20 宝宝信息表 (baby)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 宝宝ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| name | VARCHAR | 50 | 宝宝姓名/昵称 | - |
| birth_date | DATE | - | 出生日期 | NOT NULL |
| gender | ENUM | - | 性别 | 'male','female','unknown', DEFAULT 'unknown' |
| birth_height | DECIMAL | 5,1 | 出生身高（cm） | - |
| birth_weight | DECIMAL | 5,2 | 出生体重（kg） | - |
| delivery_type | ENUM | - | 分娩方式 | 'natural','cesarean','unknown', DEFAULT 'unknown' |
| blood_type | VARCHAR | 10 | 血型 | - |
| birth_order | TINYINT | 1 | 家中排行 | DEFAULT 1 |
| is_primary | TINYINT | 1 | 是否为主要记录宝宝 | DEFAULT 0 |
| notes | TEXT | - | 备注 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**复合唯一索引**：idx_user_id(user_id)

#### 5.2.21 成就定义表 (achievement)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 成就ID | PK, AUTO_INCREMENT |
| code | VARCHAR | 50 | 成就代码（唯一标识） | UNIQUE, NOT NULL |
| name | VARCHAR | 100 | 成就名称 | NOT NULL |
| description | VARCHAR | 255 | 成就描述 | - |
| icon_url | VARCHAR | 255 | 徽章图标URL | - |
| category | ENUM | - | 成就分类 | 'checkin','period','social','health' |
| condition_type | VARCHAR | 50 | 触发条件类型 | NOT NULL |
| condition_value | INT | - | 触发条件值 | NOT NULL |
| reward_points | INT | - | 奖励积分 | DEFAULT 0 |
| sort_order | INT | - | 排序 | DEFAULT 0 |
| status | TINYINT | 1 | 状态 | 0=禁用,1=启用, DEFAULT 1 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.22 用户成就表 (user_achievement)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| achievement_id | BIGINT | 20 | 成就ID | FK -> achievement.id |
| progress | INT | - | 当前进度 | DEFAULT 0 |
| status | TINYINT | 1 | 状态 | 0=未解锁,1=已解锁, DEFAULT 0 |
| earned_at | DATETIME | - | 解锁时间 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**复合唯一索引**：(user_id, achievement_id)

#### 5.2.23 用户设置表 (user_settings)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 设置ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id, UNIQUE |
| period_remind_days | TINYINT | 2 | 经期提前提醒天数 | DEFAULT 3 |
| period_remind_enabled | TINYINT | 1 | 经期提醒开关 | DEFAULT 1 |
| ovulation_remind_enabled | TINYINT | 1 | 排卵提醒开关 | DEFAULT 1 |
| diaper_remind_enabled | TINYINT | 1 | 换卫生巾提醒开关 | DEFAULT 0 |
| diaper_remind_interval | TINYINT | 2 | 换卫生巾间隔（小时） | DEFAULT 4 |
| bbt_remind_enabled | TINYINT | 1 | 体温提醒开关 | DEFAULT 1 |
| bbt_remind_time | TIME | - | 体温提醒时间 | DEFAULT '07:00:00' |
| water_remind_enabled | TINYINT | 1 | 喝水提醒开关 | DEFAULT 0 |
| water_remind_interval | TINYINT | 2 | 喝水提醒间隔（小时） | DEFAULT 2 |
| sport_remind_enabled | TINYINT | 1 | 运动提醒开关 | DEFAULT 0 |
| sport_remind_time | TIME | - | 运动提醒时间 | DEFAULT '19:00:00' |
| folic_remind_enabled | TINYINT | 1 | 叶酸提醒开关 | DEFAULT 0 |
| folic_remind_time | TIME | - | 叶酸提醒时间 | DEFAULT '08:00:00' |
| theme | ENUM | - | 主题 | 'light','dark','system', DEFAULT 'system' |
| privacy_level | TINYINT | 1 | 隐私等级 | 0=完全私密,1=仅自己可见,2=好友可见, DEFAULT 0 |
| silent_mode_enabled | TINYINT | 1 | 勿扰模式开关 | DEFAULT 0 |
| silent_start | TIME | - | 勿扰开始时间 | DEFAULT '22:00:00' |
| silent_end | TIME | - | 勿扰结束时间 | DEFAULT '08:00:00' |
| push_channel | ENUM | - | 推送渠道 | 'app','wechat',DEFAULT 'app' |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.24 知识分类表 (knowledge_category)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 分类ID | PK, AUTO_INCREMENT |
| parent_id | BIGINT | 20 | 父分类ID | FK -> knowledge_category.id |
| name | VARCHAR | 50 | 分类名称 | NOT NULL |
| icon | VARCHAR | 50 | 分类图标 | - |
| sort_order | INT | - | 排序 | DEFAULT 0 |
| type | ENUM | - | 内容类型 | 'article','recipe','exercise','qa', DEFAULT 'article' |
| status | TINYINT | 1 | 状态 | 0=禁用,1=启用, DEFAULT 1 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**初始数据**：
- 经期护理（parent_id=0）
- 排卵受孕（parent_id=0）
- 孕期保健（parent_id=0）
- 产后恢复（parent_id=0）
- 健康食谱（parent_id=0）
- 运动指导（parent_id=0）
- 妇科常识（parent_id=0）

#### 5.2.25 社区话题表 (community_topic)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 话题ID | PK, AUTO_INCREMENT |
| name | VARCHAR | 50 | 话题名称 | NOT NULL |
| cover_image | VARCHAR | 255 | 话题封面图 | - |
| description | VARCHAR | 500 | 话题描述 | - |
| post_count | INT | - | 帖子数量 | DEFAULT 0 |
| follower_count | INT | - | 关注人数 | DEFAULT 0 |
| is_hot | TINYINT | 1 | 是否热门 | DEFAULT 0 |
| sort_order | INT | - | 排序 | DEFAULT 0 |
| status | TINYINT | 1 | 状态 | 0=禁用,1=启用, DEFAULT 1 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**初始数据**：
- 经期交流、备孕怀孕、产后恢复、情感树洞、美妆穿搭、运动健身

#### 5.2.26 通知记录表 (notification)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 通知ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| notification_type | ENUM | - | 通知类型 | 'period_reminder','ovulation_reminder','checkup_reminder','checkin_reminder','system','community' |
| title | VARCHAR | 100 | 通知标题 | NOT NULL |
| content | TEXT | - | 通知内容 | - |
| target_url | VARCHAR | 255 | 点击跳转链接 | - |
| is_read | TINYINT | 1 | 是否已读 | DEFAULT 0 |
| read_time | DATETIME | - | 阅读时间 | - |
| scheduled_time | DATETIME | - | 计划发送时间 | - |
| sent_time | DATETIME | - | 实际发送时间 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |

#### 5.2.27 发现页轮播图表 (banner)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | BannerID | PK, AUTO_INCREMENT |
| title | VARCHAR | 100 | 标题 | NOT NULL |
| image_url | VARCHAR | 255 | 图片URL | NOT NULL |
| link_type | ENUM | - | 跳转类型 | 'url','article','post','none', DEFAULT 'none' |
| link_value | VARCHAR | 255 | 跳转值（URL/文章ID/帖子ID） | - |
| sort_order | INT | - | 排序 | DEFAULT 0 |
| start_time | DATETIME | - | 展示开始时间 | - |
| end_time | DATETIME | - | 展示结束时间 | - |
| status | TINYINT | 1 | 状态 | 0=禁用,1=启用, DEFAULT 1 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

#### 5.2.28 用户关注表 (user_follow)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| follower_id | BIGINT | 20 | 关注者ID | FK -> user.id |
| following_id | BIGINT | 20 | 被关注者ID | FK -> user.id |
| create_time | DATETIME | - | 关注时间 | NOT NULL |

**复合唯一索引**：(follower_id, following_id)

#### 5.2.29 阅读历史表 (user_read_history)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| article_id | BIGINT | 20 | 文章ID | FK -> knowledge_article.id |
| read_at | DATETIME | - | 阅读时间 | NOT NULL |
| read_duration | INT | - | 阅读时长（秒） | DEFAULT 0 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |

**索引**：idx_user_id(user_id), idx_read_at(read_at)

#### 5.2.30 用户收藏表 (user_collect)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 收藏ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| target_type | ENUM | - | 收藏对象类型 | 'article','post','expert_question' |
| target_id | BIGINT | 20 | 收藏对象ID | NOT NULL |
| create_time | DATETIME | - | 收藏时间 | NOT NULL |

**复合唯一索引**：(user_id, target_type, target_id)

#### 5.2.31 健康报告表 (health_report)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 报告ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| report_type | ENUM | - | 报告类型 | 'monthly','quarterly','annual' |
| report_month | DATE | - | 报告月份（monthly/quarterly: YYYY-MM-01; annual: YYYY-01-01） | NOT NULL |
| summary | TEXT | - | 报告摘要 | - |
| content | JSON | - | 报告详细内容（各项指标数据） | - |
| pdf_url | VARCHAR | 255 | PDF文件URL | - |
| status | TINYINT | 1 | 生成状态 | 0=生成中,1=已完成, DEFAULT 1 |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**复合唯一索引**：(user_id, report_type, report_month)

#### 5.2.32 经期月度统计表 (period_stats)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 统计ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| stat_month | DATE | - | 统计月份（格式：YYYY-MM-01） | NOT NULL |
| cycle_length | SMALLINT | 3 | 该月周期天数 | - |
| period_length | SMALLINT | 3 | 该月经期天数 | - |
| avg_flow_level | DECIMAL | 3,2 | 该月平均经量等级 | - |
| symptom_count | INT | - | 该月症状记录数 | DEFAULT 0 |
| avg_mood_score | DECIMAL | 3,2 | 该月平均心情分 | - |
| regularity_score | TINYINT | 3 | 该月规律度评分 | - |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**复合唯一索引**：(user_id, stat_month)

#### 5.2.33 角色切换记录表 (user_role_history)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| from_role | ENUM | - | 原角色 | 'period','trying','pregnant','mother' |
| to_role | ENUM | - | 新角色 | 'period','trying','pregnant','mother' |
| switch_reason | VARCHAR | 100 | 切换原因 | - |
| switch_time | DATETIME | - | 切换时间 | NOT NULL |
| create_time | DATETIME | - | 创建时间 | NOT NULL |

**索引**：idx_user_id(user_id), idx_switch_time(switch_time)

#### 5.2.34 帖子点赞表 (post_like)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| post_id | BIGINT | 20 | 帖子ID | FK -> community_post.id |
| create_time | DATETIME | - | 点赞时间 | NOT NULL |

**复合唯一索引**：(user_id, post_id)

#### 5.2.35 帖子收藏表 (post_collect)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
| post_id | BIGINT | 20 | 帖子ID | FK -> community_post.id |
| create_time | DATETIME | - | 收藏时间 | NOT NULL |

**复合唯一索引**：(user_id, post_id)

#### 5.2.36 帖子审核记录表 (post_audit_log)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 记录ID | PK, AUTO_INCREMENT |
| post_id | BIGINT | 20 | 帖子ID | FK -> community_post.id |
| auditor_id | BIGINT | 20 | 审核员ID（NULL表示系统审核） | FK -> user.id |
| audit_result | ENUM | - | 审核结果 | 'passed','rejected' |
| audit_reason | VARCHAR | 255 | 审核拒绝原因（拒绝时填写） | - |
| audit_time | DATETIME | - | 审核时间 | NOT NULL |
| create_time | DATETIME | - | 创建时间 | NOT NULL |

#### 5.2.37 活动表 (activity)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 活动ID | PK, AUTO_INCREMENT |
| title | VARCHAR | 100 | 活动标题 | NOT NULL |
| type | ENUM | - | 活动类型 | 'checkin','share','expert','other' |
| cover_image | VARCHAR | 255 | 封面图片URL | - |
| description | TEXT | - | 活动描述 | - |
| rules | JSON | - | 活动规则（参与条件/奖励规则） | - |
| start_time | DATETIME | - | 活动开始时间 | NOT NULL |
| end_time | DATETIME | - | 活动结束时间 | NOT NULL |
| reward_points | INT | - | 参与奖励积分 | DEFAULT 0 |
| participant_count | INT | - | 参与人数 | DEFAULT 0 |
| max_participants | INT | - | 最大参与人数（NULL表示不限） | - |
| status | ENUM | - | 状态 | 'draft','published','ongoing','ended','cancelled', DEFAULT 'draft' |
| create_time | DATETIME | - | 创建时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**索引**：idx_status_time(status, start_time, end_time)

#### 5.2.38 姐妹问答问题表 (community_question)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 问题ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 20 | 提问用户ID | FK -> user.id |
| title | VARCHAR | 200 | 问题标题 | NOT NULL |
| content | TEXT | - | 问题正文 | NOT NULL |
| images | JSON | - | 图片列表 | - |
| category | VARCHAR | 50 | 问题分类（如"备孕"、"孕期"、"育儿"） | - |
| status | ENUM | - | 状态 | 'pending','published','closed','deleted', DEFAULT 'published' |
| is_anonymous | TINYINT | 1 | 是否匿名提问 | DEFAULT 0 |
| is_top | TINYINT | 1 | 是否置顶 | DEFAULT 0 |
| is_essence | TINYINT | 1 | 是否精华 | DEFAULT 0 |
| view_count | INT | - | 浏览次数 | DEFAULT 0 |
| answer_count | INT | - | 回答数 | DEFAULT 0 |
| like_count | INT | - | 点赞数 | DEFAULT 0 |
| collect_count | INT | - | 收藏数 | DEFAULT 0 |
| last_answer_time | DATETIME | - | 最后回答时间 | - |
| create_time | DATETIME | - | 提问时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**索引**：idx_user_id(user_id), idx_status_published(status, create_time), idx_category(category), idx_is_top_essence(is_top, is_essence)

#### 5.2.39 姐妹问答回答表 (community_answer)

| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
| id | BIGINT | 20 | 回答ID | PK, AUTO_INCREMENT |
| question_id | BIGINT | 20 | 关联问题ID | FK -> community_question.id |
| user_id | BIGINT | 20 | 回答用户ID | FK -> user.id |
| content | TEXT | - | 回答正文 | NOT NULL |
| images | JSON | - | 图片列表 | - |
| like_count | INT | - | 点赞数 | DEFAULT 0 |
| is_adopted | TINYINT | 1 | 是否被提问者采纳 | DEFAULT 0 |
| adopted_time | DATETIME | - | 采纳时间 | - |
| status | ENUM | - | 状态 | 'published','hidden','deleted', DEFAULT 'published' |
| create_time | DATETIME | - | 回答时间 | NOT NULL |
| update_time | DATETIME | - | 更新时间 | NOT NULL |

**索引**：idx_question_id(question_id), idx_user_id(user_id), idx_adopted(is_adopted), idx_create_time(create_time)

#### 5.2.40 用户操作日志表 (user_audit_log)

|| 字段名 | 类型 | 长度 | 说明 | 约束 |
|--------|------|------|------|------|
|| id | BIGINT | 20 | 日志ID | PK, AUTO_INCREMENT |
|| user_id | BIGINT | 20 | 用户ID | FK -> user.id |
|| action_type | VARCHAR | 50 | 操作类型 | NOT NULL |
|| action_desc | VARCHAR | 255 | 操作描述 | - |
|| request_method | VARCHAR | 10 | 请求方法 | - |
|| request_path | VARCHAR | 200 | 请求路径 | - |
|| request_params | JSON | - | 请求参数（脱敏后） | - |
|| ip_address | VARCHAR | 50 | IP地址 | - |
|| user_agent | VARCHAR | 500 | 用户代理 | - |
|| create_time | DATETIME | - | 操作时间 | NOT NULL |

**索引**：idx_user_id(user_id), idx_action_type(action_type), idx_create_time(create_time)

**操作类型枚举**：
- ACCOUNT_CANCEL（注销账户）
- PROFILE_UPDATE（修改档案）
- PASSWORD_CHANGE（修改密码）
- PERIOD_RECORD_DELETE（删除经期记录）
- PRIVACY_CHANGE（隐私设置变更）
- ROLE_SWITCH（角色切换）

### 5.3 枚举值定义

#### 5.3.1 用户角色 (user_role)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 经期管理 | period | 默认模式，管理普通经期 |
| 备孕期 | trying | 积极备孕模式 |
| 怀孕期 | pregnant | 已确认怀孕 |
| 妈妈期 | mother | 产后妈妈 |

#### 5.3.2 经量等级 (period_flow)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 轻 | light | 经量较少 |
| 中 | medium | 经量中等 |
| 重 | heavy | 经量较多 |

#### 5.3.3 经期状态 (period_status)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 未开始 | not_started | 经期尚未开始 |
| 进行中 | active | 经期正在进行 |
| 已结束 | ended | 经期已结束 |

#### 5.3.4 排卵试纸结果 (ovulation_result)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 阴性 | negative | 未检测到LH峰值 |
| 弱阳 | weak | LH值偏低，可能接近排卵 |
| 阳性 | positive | 检测到LH峰值，即将排卵 |
| 强阳 | strong | LH值达到高峰，即将排卵 |

#### 5.3.5 打卡类型 (checkin_type)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 喝水 | water | 喝水打卡 |
| 运动 | sport | 运动打卡 |
| 睡眠 | sleep | 睡眠打卡 |
| 早餐 | breakfast | 早餐打卡 |
| 心情 | mood | 心情打卡 |
| 维生素 | vitamin | 维生素补充打卡 |
| 其他 | other | 其他自定义打卡 |

#### 5.3.6 心情状态 (mood)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 开心 | happy | 心情愉悦 |
| 平静 | calm | 心情平静 |
| 焦虑 | anxious | 心情焦虑 |
| 低落 | sad | 心情低落 |
| 烦躁 | angry | 心情烦躁 |

#### 5.3.7 帖子状态 (post_status)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 草稿 | draft | 草稿状态，未发布 |
| 已发布 | published | 已发布状态 |
| 已删除 | deleted | 已删除状态 |

#### 5.3.8 帖子类型 (post_type)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 动态 | dynamic | 普通动态 |
| 提问 | question | 提问帖 |
| 分享 | share | 分享帖 |
| 经验 | experience | 经验分享帖 |

#### 5.3.9 通知类型 (notification_type)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 经期提醒 | period_reminder | 经期相关提醒 |
| 排卵提醒 | ovulation_reminder | 排卵相关提醒 |
| 产检提醒 | checkup_reminder | 产检提醒 |
| 打卡提醒 | checkin_reminder | 健康打卡提醒 |
| 系统通知 | system | 系统通知 |
| 社区通知 | community | 社区互动通知 |

#### 5.3.10 怀孕状态 (pregnancy_status)

| 枚举值 | 代码值 | 说明 |
|--------|--------|------|
| 已确认 | confirmed | 已确认怀孕 |
| 待确认 | checking | 正在确认中 |
| 已结束 | ended | 怀孕已结束 |

### 5.4 索引设计

| 表名 | 索引名称 | 索引字段 | 类型 | 说明 |
|------|----------|----------|------|------|
| user | idx_phone | phone | UNIQUE | 手机号唯一索引 |
| user | idx_status | status | - | 状态查询索引 |
| user_profile | idx_user_id | user_id | UNIQUE | 用户ID唯一索引 |
| user_settings | idx_user_id | user_id | UNIQUE | 用户设置唯一索引 |
| user_role_history | idx_user_id | user_id | - | 用户角色历史查询 |
| user_role_history | idx_switch_time | switch_time | - | 切换时间排序 |
| period_record | idx_user_date | user_id, start_date | - | 用户经期查询 |
| period_prediction | idx_user_id | user_id | UNIQUE | 用户预测唯一索引 |
| period_stats | idx_user_month | user_id, stat_month | UNIQUE | 用户月度统计唯一索引 |
| ovulation_record | idx_user_date | user_id, record_date | - | 用户排卵查询 |
| bbt_temp_record | idx_user_date | user_id, record_date | - | 用户体温查询 |
| intercourse_record | idx_user_date | user_id, record_date | - | 用户同房记录查询 |
| pregnancy_record | idx_user_id | user_id | UNIQUE | 用户怀孕唯一索引 |
| checkup_record | idx_user_pregnancy | user_id, pregnancy_id | - | 产检记录查询 |
| checkin_record | idx_user_date_type | user_id, checkin_date, checkin_type | UNIQUE | 每日打卡唯一 |
| habit | idx_user_status | user_id, status | - | 用户习惯查询 |
| achievement | idx_code | code | UNIQUE | 成就代码唯一索引 |
| user_achievement | idx_user_achievement | user_id, achievement_id | UNIQUE | 用户成就唯一索引 |
| user_achievement | idx_user_status | user_id, status | - | 用户已解锁成就查询 |
| knowledge_category | idx_parent | parent_id | - | 分类树查询 |
| knowledge_article | idx_category_status | category, status | - | 文章分类查询 |
| knowledge_article | idx_publish_time | publish_time | - | 发布时间排序 |
| knowledge_article | idx_featured | is_featured, status | - | 推荐文章查询 |
| user_read_history | idx_user_article | user_id, article_id | UNIQUE | 阅读历史唯一索引 |
| user_read_history | idx_read_at | read_at | - | 阅读时间排序 |
| community_topic | idx_hot | is_hot, status | - | 热门话题查询 |
| community_topic | idx_sort | sort_order | - | 排序查询 |
| community_post | idx_user_status | user_id, status | - | 用户帖子查询 |
| community_post | idx_topic | topic_id | - | 话题查询 |
| community_post | idx_create_time | create_time | - | 时间排序 |
| community_post | idx_audit | audit_status, status | - | 待审核帖子查询 |
| community_comment | idx_post | post_id | - | 帖子评论查询 |
| community_comment | idx_user | user_id | - | 用户评论查询 |
| post_like | idx_user_post | user_id, post_id | UNIQUE | 点赞唯一索引 |
| post_collect | idx_user_post | user_id, post_id | UNIQUE | 收藏唯一索引 |
| user_follow | idx_follower | follower_id | - | 关注者查询 |
| user_follow | idx_following | following_id | - | 被关注者查询 |
| user_follow | idx_bidirectional | follower_id, following_id | UNIQUE | 双向关系唯一索引 |
| notification | idx_user_read | user_id, is_read | - | 用户通知查询 |
| notification | idx_scheduled | scheduled_time | - | 定时发送索引 |
| banner | idx_time | start_time, end_time, status | - | 有效Banner查询 |
| banner | idx_sort | sort_order | - | 排序查询 |
| health_report | idx_user_type_month | user_id, report_type, report_month | UNIQUE | 健康报告唯一索引 |
| post_audit_log | idx_post | post_id | - | 帖子审核历史查询 |
| pregnancy_diary | idx_user_pregnancy_date | user_id, pregnancy_id, record_date | UNIQUE | 孕期日记唯一索引 |
| body_metrics_record | idx_user_date_type | user_id, record_date, metric_type | UNIQUE | 体征记录唯一索引 |
| expert | idx_user_id | user_id | UNIQUE | 专家用户唯一索引 |
| expert | idx_cert_status | certification_status, status | - | 待认证专家查询 |
| expert_question | idx_user_id | user_id | - | 用户提问查询 |
| expert_question | idx_expert_id | expert_id | - | 专家问题查询 |
| expert_question | idx_status | status, create_time | - | 待回答问题查询 |
| expert_question | idx_create_time | create_time | - | 提问时间排序 |
| expert_answer | idx_question_id | question_id | - | 问题回答查询 |
| expert_answer | idx_expert_id | expert_id | - | 专家回答查询 |
| baby | idx_user_id | user_id | - | 用户宝宝查询 |
| user_collect | idx_user_target | user_id, target_type, target_id | UNIQUE | 用户收藏唯一索引 |
| user_collect | idx_target | target_type, target_id | - | 对象被收藏次数统计 |
| user_audit_log | idx_user_id | user_id | - | 用户操作日志查询 |
| user_audit_log | idx_action_type | action_type | - | 操作类型查询 |
| user_audit_log | idx_create_time | create_time | - | 操作时间排序 |

---

## 六、API 接口设计

### 6.1 用户模块

#### 6.1.1 认证相关

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 发送验证码 | POST | /api/auth/send-code | 发送登录验证码 | {phone, type} | {code, expireTime} |
| 手机号登录 | POST | /api/auth/phone-login | 验证码登录 | {phone, code} | {token, userInfo} |
| 密码登录 | POST | /api/auth/password-login | 密码登录 | {phone, password} | {token, userInfo} |
| 第三方登录 | POST | /api/auth/third-party-login | 微信/Apple/QQ登录 | {openId, platform, unionId} | {token, userInfo, isNewUser} |
| 忘记密码 | POST | /api/auth/forgot-password | 忘记密码重置 | {phone, code, newPassword} | {success} |
| 设置密码 | POST | /api/auth/set-password | 设置登录密码 | {oldPassword, newPassword} | {success} |
| 退出登录 | POST | /api/auth/logout | 退出登录 | - | {success} |
| 刷新Token | POST | /api/auth/refresh-token | 刷新Token | - | {token, refreshToken} |

#### 6.1.2 用户信息

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取用户信息 | GET | /api/user/info | 获取当前用户信息 | - | UserInfo |
| 更新用户信息 | PUT | /api/user/info | 更新用户基本信息 | {nickname, avatar} | UserInfo |
| 获取用户档案 | GET | /api/user/profile | 获取用户详细档案 | - | UserProfile |
| 更新用户档案 | PUT | /api/user/profile | 更新用户档案 | UserProfile | UserProfile |
| 更新用户角色 | PUT | /api/user/role | 更新用户角色 | {userRole} | {success} |
| 获取其他用户信息 | GET | /api/user/{userId} | 获取其他用户公开信息 | userId | PublicUserInfo |
| 上传头像 | POST | /api/user/avatar | 上传用户头像 | multipart/form | {avatarUrl} |

#### 6.1.3 设置与偏好

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取设置 | GET | /api/user/settings | 获取用户设置 | - | UserSettings |
| 更新设置 | PUT | /api/user/settings | 更新用户设置 | UserSettings | UserSettings |
| 更换手机号 | POST | /api/user/phone/change | 更换手机号 | {newPhone, code} | {success} |
| 注销账户 | POST | /api/user/account/cancel | 注销账户 | {reason, confirmPassword} | {success} |
| 获取隐私政策 | GET | /api/user/privacy | 获取隐私政策 | - | PrivacyContent |
| 同意隐私政策 | POST | /api/user/privacy/agree | 同意隐私政策 | - | {success} |

### 6.2 经期模块

#### 6.2.1 经期记录

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 记录经期开始 | POST | /api/period/start | 标记经期开始 | {startDate, flow, symptoms} | PeriodRecord |
| 记录经期结束 | POST | /api/period/end | 标记经期结束 | {recordId, endDate} | PeriodRecord |
| 更新经期记录 | PUT | /api/period/record/{id} | 更新经期记录 | PeriodRecord | PeriodRecord |
| 删除经期记录 | DELETE | /api/period/record/{id} | 删除经期记录 | - | {success} |
| 获取经期记录列表 | GET | /api/period/records | 获取经期记录列表 | {startDate, endDate, page, size} | Page<PeriodRecord> |
| 获取经期详情 | GET | /api/period/record/{id} | 获取单条经期记录 | id | PeriodRecord |
| 添加经期日记 | POST | /api/period/diary | 添加经期日记 | {recordId, diary} | {success} |
| 获取经期日历 | GET | /api/period/calendar | 获取经期日历视图 | {year, month} | CalendarData |

#### 6.2.2 经期预测

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取经期预测 | GET | /api/period/prediction | 获取当前预测 | - | PeriodPrediction |
| 手动校正预测 | PUT | /api/period/prediction | 手动校正预测 | {predictedStart, predictedEnd} | PeriodPrediction |
| 获取周期统计 | GET | /api/period/stats | 获取周期统计数据 | {months} | PeriodStats |
| 获取历史周期 | GET | /api/period/history | 获取历史周期列表 | {page, size} | Page<PeriodCycle> |
| 获取周期规律度 | GET | /api/period/regularity | 获取周期规律评估 | - | RegularityAnalysis |

#### 6.2.3 经期提醒

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 设置经期提醒 | POST | /api/period/reminder | 设置经期提醒 | PeriodReminder | PeriodReminder |
| 获取提醒设置 | GET | /api/period/reminder | 获取提醒设置 | - | PeriodReminder |
| 更新提醒设置 | PUT | /api/period/reminder | 更新提醒设置 | PeriodReminder | PeriodReminder |
| 开启/关闭提醒 | PUT | /api/period/reminder/toggle | 开关提醒 | {type, enabled} | {success} |
| 获取下次经期倒计时 | GET | /api/period/countdown | 获取下次经期倒计时 | - | CountdownData |

### 6.3 排卵追踪模块

#### 6.3.1 排卵记录

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 记录排卵试纸 | POST | /api/ovulation/test-strip | 记录试纸结果 | {recordDate, result, photoUrl} | OvulationRecord |
| 拍照识别试纸 | POST | /api/ovulation/recognize | AI识别试纸 | multipart/form | {result, lhValue} |
| 获取试纸记录 | GET | /api/ovulation/records | 获取试纸记录列表 | {startDate, endDate} | List<OvulationRecord> |
| 确认已排卵 | POST | /api/ovulation/confirm | 确认排卵 | {confirmedDate} | {success} |
| 获取排卵日预测 | GET | /api/ovulation/prediction | 获取排卵日预测 | - | OvulationPrediction |
| 获取LH曲线 | GET | /api/ovulation/lh-curve | 获取LH变化曲线 | {months} | LHCurveData |

#### 6.3.2 基础体温

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 记录体温 | POST | /api/temperature | 记录基础体温 | {recordDate, temperature, time} | BasbTempRecord |
| 获取体温记录 | GET | /api/temperature/records | 获取体温记录列表 | {startDate, endDate} | List<BasbTempRecord> |
| 获取体温曲线 | GET | /api/temperature/curve | 获取体温曲线 | {months} | TemperatureCurve |
| 设置体温提醒 | POST | /api/temperature/reminder | 设置体温提醒 | {reminderTime, enabled} | {success} |
| 删除体温记录 | DELETE | /api/temperature/{id} | 删除体温记录 | - | {success} |

#### 6.3.3 排卵症状

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 记录排卵症状 | POST | /api/ovulation/symptoms | 记录排卵症状 | {recordDate, symptoms} | {success} |
| 获取症状记录 | GET | /api/ovulation/symptoms | 获取排卵症状记录 | {startDate, endDate} | List<OvulationSymptom> |

### 6.4 备孕怀孕模块

#### 6.4.1 备孕计划

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 创建备孕计划 | POST | /api/trying/pregnancy/plan | 创建备孕计划 | TryingPlan | TryingPlan |
| 获取备孕计划 | GET | /api/trying/pregnancy/plan | 获取备孕计划 | - | TryingPlan |
| 更新备孕计划 | PUT | /api/trying/pregnancy/plan | 更新备孕计划 | TryingPlan | TryingPlan |
| 获取备孕进度 | GET | /api/trying/pregnancy/progress | 获取备孕进度 | - | TryingProgress |
| 设置叶酸提醒 | POST | /api/trying/folic-reminder | 设置叶酸提醒 | {reminderTime, enabled} | {success} |

#### 6.4.2 同房记录

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 记录同房 | POST | /api/intercourse | 记录同房 | IntercourseRecord | IntercourseRecord |
| 获取同房记录 | GET | /api/intercourse/records | 获取同房记录列表 | {startDate, endDate, page} | Page<IntercourseRecord> |
| 更新同房记录 | PUT | /api/intercourse/{id} | 更新同房记录 | IntercourseRecord | IntercourseRecord |
| 删除同房记录 | DELETE | /api/intercourse/{id} | 删除同房记录 | - | {success} |
| 获取最佳同房时间 | GET | /api/intercourse/best-time | 获取推荐同房时间 | - | BestTimeRecommendation |

#### 6.4.3 怀孕记录

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 确认怀孕 | POST | /api/pregnancy/confirm | 确认怀孕 | {lastPeriodStart} | PregnancyRecord |
| 获取怀孕记录 | GET | /api/pregnancy/record | 获取怀孕记录 | - | PregnancyRecord |
| 更新怀孕状态 | PUT | /api/pregnancy/status | 更新怀孕状态 | {status, endDate, reason} | PregnancyRecord |
| 获取孕周信息 | GET | /api/pregnancy/week-info | 获取当前孕周信息 | - | PregnancyWeekInfo |
| 获取宝宝发育 | GET | /api/pregnancy/baby-development | 获取宝宝发育情况 | {week} | BabyDevelopment |
| 获取妈妈变化 | GET | /api/pregnancy/mom-changes | 获取妈妈身体变化 | {week} | MomChanges |

#### 6.4.4 产检记录

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取产检时间表 | GET | /api/checkup/schedule | 获取产检时间表 | - | List<CheckupSchedule> |
| 记录产检结果 | POST | /api/checkup | 记录产检结果 | CheckupRecord | CheckupRecord |
| 获取产检记录列表 | GET | /api/checkup/records | 获取产检记录列表 | {page, size} | Page<CheckupRecord> |
| 获取产检详情 | GET | /api/checkup/{id} | 获取产检详情 | id | CheckupRecord |
| 更新产检记录 | PUT | /api/checkup/{id} | 更新产检记录 | CheckupRecord | CheckupRecord |
| 获取下次产检 | GET | /api/checkup/next | 获取下次产检信息 | - | NextCheckup |
| 上传产检附件 | POST | /api/checkup/attachment | 上传产检附件 | multipart/form | {attachmentUrl} |

#### 6.4.5 安全期与易孕期

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取安全期 | GET | /api/fertility/safe-period | 获取安全期 | - | SafePeriodData |
| 获取易孕期 | GET | /api/fertility/fertile-period | 获取易孕期 | - | FertilePeriodData |
| 获取生育力评估 | GET | /api/fertility/assessment | 获取生育力评估 | - | FertilityAssessment |
| 获取每日受孕几率 | GET | /api/fertility/daily-chance | 获取每日受孕几率 | {date} | DailyConceptionChance |

### 6.5 健康打卡模块

#### 6.5.1 每日打卡

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 每日打卡 | POST | /api/checkin/daily | 每日打卡 | {checkinDate, type, value, mood} | CheckinRecord |
| 批量打卡 | POST | /api/checkin/batch | 批量打卡多项 | List<CheckinRecord> | List<CheckinRecord> |
| 获取打卡记录 | GET | /api/checkin/records | 获取打卡记录列表 | {startDate, endDate, type} | List<CheckinRecord> |
| 获取打卡历史 | GET | /api/checkin/history | 获取打卡历史 | {page, size} | Page<CheckinRecord> |
| 获取打卡统计 | GET | /api/checkin/stats | 获取打卡统计 | {startDate, endDate} | CheckinStats |
| 获取今日打卡状态 | GET | /api/checkin/today-status | 获取今日打卡状态 | - | TodayCheckinStatus |
| 删除打卡记录 | DELETE | /api/checkin/{id} | 删除打卡记录 | - | {success} |

#### 6.5.2 习惯管理

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 添加习惯 | POST | /api/habit | 添加新习惯 | Habit | Habit |
| 获取习惯列表 | GET | /api/habit/list | 获取习惯列表 | {status} | List<Habit> |
| 更新习惯 | PUT | /api/habit/{id} | 更新习惯 | Habit | Habit |
| 删除习惯 | DELETE | /api/habit/{id} | 删除习惯 | - | {success} |
| 开启/关闭习惯 | PUT | /api/habit/{id}/toggle | 开关习惯 | {enabled} | {success} |
| 获取习惯统计 | GET | /api/habit/stats/{id} | 获取习惯统计 | id | HabitStats |
| 获取连续打卡记录 | GET | /api/habit/streak | 获取连续打卡记录 | - | StreakData |

#### 6.5.3 健康提醒

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 设置提醒 | POST | /api/reminder | 设置提醒 | Reminder | Reminder |
| 获取提醒列表 | GET | /api/reminder/list | 获取提醒列表 | - | List<Reminder> |
| 更新提醒 | PUT | /api/reminder/{id} | 更新提醒 | Reminder | Reminder |
| 删除提醒 | DELETE | /api/reminder/{id} | 删除提醒 | - | {success} |
| 开启/关闭提醒 | PUT | /api/reminder/{id}/toggle | 开关提醒 | {enabled} | {success} |

#### 6.5.4 成就系统

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取成就列表 | GET | /api/achievement/list | 获取所有成就 | - | List<Achievement> |
| 获取已解锁成就 | GET | /api/achievement/unlocked | 获取已解锁成就 | - | List<Achievement> |
| 获取用户成就进度 | GET | /api/achievement/progress | 获取成就进度 | - | List<AchievementProgress> |
| 解锁成就 | POST | /api/achievement/unlock | 解锁成就(系统调用) | {type} | Achievement |

### 6.6 数据统计模块

#### 6.6.1 周期分析

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取周期日历 | GET | /api/stats/period-calendar | 获取周期日历 | {year, month} | PeriodCalendar |
| 获取周期对比 | GET | /api/stats/period-compare | 获取周期对比 | {months} | PeriodCompare |
| 获取经期长度分布 | GET | /api/stats/period-length-dist | 获取经期长度分布 | - | LengthDistribution |
| 获取周期长度分布 | GET | /api/stats/cycle-length-dist | 获取周期长度分布 | - | LengthDistribution |
| 获取症状统计 | GET | /api/stats/symptoms | 获取症状统计 | {months} | SymptomStats |

#### 6.6.2 健康报告

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取月度报告 | GET | /api/stats/monthly-report | 获取月度健康报告 | {year, month} | MonthlyReport |
| 获取季度报告 | GET | /api/stats/quarterly-report | 获取季度报告 | {year, quarter} | QuarterlyReport |
| 获取年度报告 | GET | /api/stats/annual-report | 获取年度报告 | {year} | AnnualReport |
| 生成报告 | POST | /api/stats/generate | 生成健康报告 | {type, period} | Report |
| 导出报告PDF | GET | /api/stats/report/{id}/export | 导出报告PDF | id | {pdfUrl} |
| 下载报告 | GET | /api/stats/report/{id}/download | 下载报告文件 | id | 文件流 |

#### 6.6.3 趋势图表

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取周期趋势 | GET | /api/stats/cycle-trend | 获取周期趋势 | {months} | CycleTrend |
| 获取经期趋势 | GET | /api/stats/period-trend | 获取经期趋势 | {months} | PeriodTrend |
| 获取症状趋势 | GET | /api/stats/symptom-trend | 获取症状趋势 | {months} | SymptomTrend |
| 获取体重趋势 | GET | /api/stats/weight-trend | 获取体重趋势 | {months} | WeightTrend |
| 获取心情趋势 | GET | /api/stats/mood-trend | 获取心情趋势 | {months} | MoodTrend |

#### 6.6.4 对比历史

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 同期对比 | GET | /api/stats/compare-same-period | 与历史同期对比 | {targetMonth} | CompareResult |
| 目标对比 | GET | /api/stats/compare-goal | 与设定目标对比 | - | CompareResult |
| 平均值对比 | GET | /api/stats/compare-average | 与同年龄段平均对比 | - | CompareResult |

### 6.7 知识库模块

#### 6.7.1 文章相关

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取文章分类 | GET | /api/knowledge/categories | 获取文章分类 | - | List<Category> |
| 获取文章列表 | GET | /api/knowledge/articles | 获取文章列表 | {category, page, size, tag} | Page<Article> |
| 获取文章详情 | GET | /api/knowledge/article/{id} | 获取文章详情 | id | ArticleDetail |
| 收藏文章 | POST | /api/knowledge/article/{id}/collect | 收藏文章 | - | {success} |
| 取消收藏文章 | DELETE | /api/knowledge/article/{id}/collect | 取消收藏 | - | {success} |
| 获取收藏文章 | GET | /api/knowledge/collections | 获取收藏文章 | {page, size} | Page<Article> |
| 点赞文章 | POST | /api/knowledge/article/{id}/like | 点赞文章 | - | {success} |
| 获取推荐文章 | GET | /api/knowledge/recommended | 获取推荐文章 | {page, size} | Page<Article> |
| 搜索文章 | GET | /api/knowledge/search | 搜索文章 | {keyword, page, size} | Page<Article> |

#### 6.7.2 专家问答

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取常见问题 | GET | /api/knowledge/faq | 获取常见问题 | {category} | List<FAQ> |
| 获取专家专栏 | GET | /api/knowledge/expert-column | 获取专家专栏 | {page, size} | Page<ExpertArticle> |
| 提交问题 | POST | /api/knowledge/question | 提交问题 | {title, content, category} | Question |
| 获取我的问题 | GET | /api/knowledge/my-questions | 获取我的问题 | {page, size, status} | Page<Question> |
| 获取问题详情 | GET | /api/knowledge/question/{id} | 获取问题详情 | id | QuestionDetail |

### 6.8 社区模块

#### 6.8.1 话题广场

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取话题列表 | GET | /api/community/topics | 获取话题列表 | {page, size, category} | Page<Topic> |
| 获取热门话题 | GET | /api/community/topics/hot | 获取热门话题 | {limit} | List<Topic> |
| 订阅话题 | POST | /api/community/topic/{id}/subscribe | 订阅话题 | - | {success} |
| 取消订阅话题 | DELETE | /api/community/topic/{id}/subscribe | 取消订阅 | - | {success} |
| 获取话题榜单 | GET | /api/community/topics/rank | 获取话题榜单 | {type} | List<TopicRank> |

#### 6.8.2 帖子管理

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 发布帖子 | POST | /api/community/post | 发布帖子 | {title, content, type, images, topicId} | Post |
| 获取帖子列表 | GET | /api/community/posts | 获取帖子列表 | {page, size, topicId, type, userId} | Page<Post> |
| 获取帖子详情 | GET | /api/community/post/{id} | 获取帖子详情 | id | PostDetail |
| 更新帖子 | PUT | /api/community/post/{id} | 更新帖子 | Post | Post |
| 删除帖子 | DELETE | /api/community/post/{id} | 删除帖子 | - | {success} |
| 置顶帖子 | POST | /api/community/post/{id}/top | 置顶帖子 | - | {success} |
| 设置精华 | POST | /api/community/post/{id}/essence | 设置精华 | - | {success} |
| 点赞帖子 | POST | /api/community/post/{id}/like | 点赞帖子 | - | {success} |
| 收藏帖子 | POST | /api/community/post/{id}/collect | 收藏帖子 | - | {success} |
| 分享帖子 | POST | /api/community/post/{id}/share | 分享帖子 | - | {success} |
| 获取用户帖子 | GET | /api/community/user/{userId}/posts | 获取用户帖子 | {userId, page, size} | Page<Post> |
| 获取收藏帖子 | GET | /api/community/my/collections | 获取收藏帖子 | {page, size} | Page<Post> |
| 获取热门帖子 | GET | /api/community/posts/hot | 获取热门帖子 | {limit} | List<Post> |
| 获取最新帖子 | GET | /api/community/posts/latest | 获取最新帖子 | {page, size} | Page<Post> |
| 搜索帖子 | GET | /api/community/search | 搜索帖子 | {keyword, page, size} | Page<Post> |

#### 6.8.3 评论管理

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 发表评论 | POST | /api/community/comment | 发表评论 | {postId, content, parentId} | Comment |
| 获取评论列表 | GET | /api/community/comments | 获取评论列表 | {postId, page, size} | Page<Comment> |
| 回复评论 | POST | /api/community/comment/{id}/reply | 回复评论 | {content} | Comment |
| 删除评论 | DELETE | /api/community/comment/{id} | 删除评论 | - | {success} |
| 点赞评论 | POST | /api/community/comment/{id}/like | 点赞评论 | - | {success} |
| 采纳评论 | POST | /api/community/comment/{id}/adopt | 采纳评论(提问者) | - | {success} |

#### 6.8.4 问答互助

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 发布问题 | POST | /api/community/question | 发布问题 | {title, content, category, images} | Question |
| 获取问题列表 | GET | /api/community/questions | 获取问题列表 | {page, size, category, status} | Page<Question> |
| 获取问题详情 | GET | /api/community/question/{id} | 获取问题详情 | id | QuestionDetail |
| 回答问题 | POST | /api/community/question/{id}/answer | 回答问题 | {content} | Answer |
| 采纳答案 | POST | /api/community/answer/{id}/adopt | 采纳答案 | - | {success} |
| 打赏答案 | POST | /api/community/answer/{id}/reward | 打赏答案 | {amount} | {success} |

### 6.9 发现页模块

#### 6.9.1 个性化推荐

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取推荐内容 | GET | /api/discover/recommended | 获取个性化推荐 | {page, size} | Page<DiscoverItem> |
| 获取每日推荐 | GET | /api/discover/daily | 获取每日推荐 | - | DailyRecommend |
| 获取热门内容 | GET | /api/discover/hot | 获取热门内容 | {type, limit} | List<HotContent> |
| 获取新内容 | GET | /api/discover/latest | 获取最新内容 | {page, size} | Page<Content> |
| 意见反馈 | POST | /api/discover/feedback | 提交意见反馈 | {content, contact} | {success} |

#### 6.9.2 活动中心

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取活动列表 | GET | /api/activity/list | 获取活动列表 | {page, size, type} | Page<Activity> |
| 获取活动详情 | GET | /api/activity/{id} | 获取活动详情 | id | ActivityDetail |
| 参与活动 | POST | /api/activity/{id}/join | 参与活动 | - | {success} |
| 获取我的活动 | GET | /api/activity/my | 获取我的活动 | {page, size, status} | Page<Activity> |

### 6.10 通知模块

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取通知列表 | GET | /api/notification/list | 获取通知列表 | {page, size, type, isRead} | Page<Notification> |
| 获取未读数量 | GET | /api/notification/unread-count | 获取未读数量 | - | {count} |
| 标记已读 | POST | /api/notification/read | 标记通知已读 | {ids} | {success} |
| 标记全部已读 | POST | /api/notification/read-all | 标记全部已读 | - | {success} |
| 删除通知 | DELETE | /api/notification/{id} | 删除通知 | - | {success} |
| 清空通知 | DELETE | /api/notification/clear | 清空通知 | {type} | {success} |

### 6.11 成就系统模块

#### 6.11.1 成就相关

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取成就列表（全部） | GET | /api/achievement/list | 获取所有成就定义 | - | List<Achievement> |
| 获取成就详情 | GET | /api/achievement/{code} | 获取成就详情 | code | Achievement |
| 获取我的成就 | GET | /api/achievement/my | 获取我的成就状态 | - | List<UserAchievementVO> |
| 获取成就进度 | GET | /api/achievement/progress | 获取所有成就进度 | - | List<AchievementProgress> |
| 领取成就奖励 | POST | /api/achievement/{id}/claim | 领取已解锁成就的积分奖励 | id | {points, success} |

#### 6.11.2 用户关系模块

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 关注用户 | POST | /api/user/{userId}/follow | 关注用户 | userId | {success} |
| 取消关注 | DELETE | /api/user/{userId}/follow | 取消关注 | userId | {success} |
| 获取关注列表 | GET | /api/user/{userId}/following | 获取用户关注列表 | {page, size} | Page<User> |
| 获取粉丝列表 | GET | /api/user/{userId}/followers | 获取用户粉丝列表 | {page, size} | Page<User> |
| 检查关注状态 | GET | /api/user/{userId}/follow-status | 检查是否关注 | userId | {isFollowing, isFollowed} |
| 获取互关列表 | GET | /api/user/mutual-following | 获取互相关注的用户 | {page, size} | Page<User> |

#### 6.11.3 发现页模块

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取Banner列表 | GET | /api/banner/list | 获取轮播图列表 | - | List<Banner> |
| 获取首页内容 | GET | /api/discover/home | 获取发现页首页内容 | {page, size} | DiscoverHomeVO |
| 获取热门内容 | GET | /api/discover/hot | 获取热门内容 | {type, limit} | List<HotContent> |
| 获取推荐用户 | GET | /api/discover/recommended-users | 获取推荐用户 | {limit} | List<User> |
| 获取活动列表 | GET | /api/activity/list | 获取活动列表 | {page, size, type} | Page<Activity> |
| 获取活动详情 | GET | /api/activity/{id} | 获取活动详情 | id | ActivityDetail |
| 参与活动 | POST | /api/activity/{id}/join | 参与活动 | - | {success} |
| 获取我的活动 | GET | /api/activity/my | 获取我的活动 | {page, size, status} | Page<Activity> |

#### 6.11.4 专家问答模块

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 获取专家列表 | GET | /api/expert/list | 获取认证专家列表 | {page, size, specialty} | Page<Expert> |
| 获取专家文章 | GET | /api/expert/{expertId}/articles | 获取专家文章 | {page, size} | Page<Article> |
| 提交问题 | POST | /api/expert/question | 向专家提问 | {expertId, title, content, images} | Question |
| 获取我的提问 | GET | /api/expert/my-questions | 获取我的提问列表 | {page, size, status} | Page<Question> |
| 获取问题详情 | GET | /api/expert/question/{id} | 获取问题详情 | id | QuestionDetail |
| 采纳答案 | POST | /api/expert/answer/{id}/adopt | 采纳答案 | - | {success} |
| 获取常见问题 | GET | /api/expert/faq | 获取常见问题 | {category} | List<FAQ> |

#### 6.11.5 AI 健康助手模块

| 接口 | 方法 | 路径 | 说明 | 请求体/参数 | 响应 |
|------|------|------|------|-------------|------|
| 提交健康咨询 | POST | /api/ai/consult | AI健康咨询 | {question, context} | AIResponse |
| 获取咨询历史 | GET | /api/ai/history | 获取AI咨询历史 | {page, size} | Page<AIConversation> |
| 获取健康建议 | GET | /api/ai/advice | 获取个性化健康建议 | - | HealthAdviceVO |
| AI试纸识别 | POST | /api/ai/recognize-strip | AI识别排卵试纸 | multipart/form | {result, lhValue, confidence} |
| 获取周期分析 | GET | /api/ai/cycle-analysis | AI周期分析报告 | {months} | CycleAnalysisVO |

### 6.12 通用响应格式

```json
// 成功响应
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": 1713000000000
}

// 分页响应
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [ ... ],
    "page": 1,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  },
  "timestamp": 1713000000000
}

// 错误响应
{
  "code": 400,
  "message": "参数错误",
  "error": "INVALID_PARAMETER",
  "timestamp": 1713000000000
}
```

### 6.13 错误码定义

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录或Token过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 1001 | 用户不存在 |
| 1002 | 手机号已注册 |
| 1003 | 验证码错误 |
| 1004 | 验证码已过期 |
| 1005 | 密码错误 |
| 2001 | 经期记录不存在 |
| 2002 | 无法预测（数据不足） |
| 3001 | 怀孕记录不存在 |
| 4001 | 帖子不存在或已删除 |
| 4002 | 评论不存在或已删除 |
| 4003 | 无法删除他人内容 |
| 5001 | 成就不存在 |
| 5002 | 成就已解锁 |

---

## 七、里程碑计划

| 阶段 | 时间 | 交付内容 |
|------|------|----------|
| M1 | 第1-2周 | 完成需求确认和UI设计 |
| M2 | 第3-6周 | 核心功能开发（用户体系、周期管理） |
| M3 | 第7-10周 | 进阶功能开发（备孕怀孕、健康打卡） |
| M4 | 第11-12周 | 社区和知识库开发 |
| M5 | 第13-14周 | 测试和Bug修复 |
| M6 | 第15周 | 上线准备和发布 |

---

## 八、风险评估

| 风险 | 影响 | 应对措施 |
|------|------|----------|
| 数据隐私泄露 | 高 | 加强加密，定期安全审计 |
| 预测算法不准确 | 中 | 多算法融合，提供准确度提示 |
| 用户流失 | 中 | 优化新手引导，保持功能更新 |
| 竞品压力 | 中 | 突出免费优势，深耕用户体验 |

---

## 九、核心算法说明

### 9.1 经期预测算法

**输入**：用户最近 N 次经期记录（start_date, end_date）

**步骤 1：计算周期长度**
```
cycle_i = start_date[i] - start_date[i-1]  （第 i 个周期的长度）
```

**步骤 2：加权平均**
```
avg_cycle = Σ(cycle_i × weight_i) / Σ(weight_i)

weight_i 的规则：
- 最近 3 个周期：权重 = 3, 2, 1（越近越高）
- 剔除 cycle_i < 21 或 cycle_i > 35 的异常值
- 有效数据 < 3 条时，准确度 = 0，直接提示"数据不足"
```

**步骤 3：预测下次经期**
```
predicted_start = latest_period_start + avg_cycle
```

**步骤 4：准确度计算**
```
accuracy = min(100, data_completeness × regularity_score)

data_completeness：
  - 有 6+ 条记录：100%
  - 有 4-5 条：80%
  - 有 3 条：60%
  - 有 2 条：40%
  - < 2 条：无法预测

regularity_score：
  - 所有周期差值 ≤ 3 天：100%（非常规律）
  - 所有周期差值 ≤ 7 天：70%（规律）
  - 差值 7-14 天：40%（较不规律）
  - 差值 > 14 天：10%（不规律）
```

**预测区间**：
```
predicted_range_start = predicted_start - 3 天
predicted_range_end   = predicted_start + 3 天
```

---

### 9.2 排卵日与安全期算法

**基准**：下次经期前 14 天 = 排卵日

```
ovulation_date = predicted_start - 14 天

// 易孕期（受孕概率最高）
fertile_start  = ovulation_date - 5 天
fertile_end    = ovulation_date + 4 天   // 共10天

// 安全期
// ① 月经期：start_date ~ end_date（经期本身不宜同房）
// ② 排卵前安全期：end_date + 1 ~ fertile_start - 1
// ③ 排卵后安全期：fertile_end + 1 ~ predicted_start - 1
```

**受孕概率参考**（基于统计均值，非绝对）：
```
排卵日当天：          25-30%
排卵前1天：           20-25%
排卵前2天：           15-20%
排卵前3天：           10-15%
排卵前4-5天：          5-10%
排卵后1-4天：          1-5%
其余时间：             <1%
```

---

### 9.3 孕周与预产期算法

**孕周计算**（以末次月经第一天为起点）：
```
gestational_days = 当前日期 - last_period_start_date
current_week     = floor(gestational_days / 7)
current_day      = gestational_days % 7
// 显示格式："孕 28 周 + 3 天"
```

**预产期计算**（WHO 标准）：
```
due_date = last_period_start_date + 280 天
// 简算法：月份 - 3（或 + 9），日期 + 7
// 例如：末次月经 2024-01-15 → 预产期 2024-10-22
```

---

### 9.4 周期规律度评分

```
regularity_score：
1. 收集最近 6 个周期长度
2. 计算标准差 σ
3. 若 σ ≤ 2：天数为"非常规律"（90-100分）
   若 σ ≤ 5：天数为"规律"（70-89分）
   若 σ ≤ 10：天数为"较不规律"（40-69分）
   若 σ > 10：天数为"不规律"（0-39分）
4. 结合平均周期偏离 28 天的程度做微调
```

---

### 9.5 基础体温（BBT）排卵判断

```
1. 取连续 6 天以上的 BBT 数据
2. 找到最高温度点 T_max 和最低温度点 T_min
3. 排卵后体温应升高 0.2-0.5 ℃
4. 判断条件：
   - T_max 后某天体温下降 ≥ 0.2 ℃，随后上升 → 排卵已发生
   - 体温持续升高超过 18 天 → 已进入黄体期（确认排卵）
5. 标注：排卵日 ≈ 体温升高前一天
```

---

## 十、推送通知设计

### 10.1 推送渠道

| 平台 | 推送方案 | 说明 |
|------|----------|------|
| **iOS / Android App** | 极光推送（JPush）或个推 | 移动端必备 |
| **微信小程序** | 微信订阅消息 | 需用户主动订阅 |
| **PC Web** | WebSocket + SSE | 实时推送，用户打开页面时生效 |

### 10.2 通知分类

| 类型 | 触发条件 | 推送内容示例 |
|------|----------|------------|
| `period_remind` | 经期预测日前 N 天 | "您的经期预计 2 天后开始" |
| `period_diary` | 经期中每天早上 9:00 | "今天经期第 2 天，注意保暖🌸" |
| `ovulation_remind` | 排卵日当天 | "今天是排卵日，备孕姐妹加油！" |
| `fertile_remind` | 易孕期每天 | "易孕期已到，科学备孕好时机~" |
| `checkin_remind` | 打卡项设定时间 | "该喝水啦~今日目标 8 杯" |
| `prenatal_remind` | 产检日前 1 天 | "明天是您的第 12 周产检" |
| `achievement_unlock` | 成就解锁时 | "🎉 连续打卡 7 天，获得'坚持不懈'勋章！" |
| `community_like` | 帖子被点赞 | "有人赞了您的帖子《备孕心得》" |
| `community_comment` | 帖子被评论 | "姐妹在您的帖子下留言了，快去看看吧" |
| `follow_remind` | 新粉丝 | "您有新粉丝啦~" |
| `system_notice` | 系统消息 | 公告/更新通知等 |

### 10.3 推送频率控制

| 场景 | 频率上限 |
|------|----------|
| 经期提醒 | 每天最多 1 条 |
| 打卡提醒 | 每项每天最多 1 条 |
| 社区互动 | 每小时汇总推送，不超过 3 条/小时 |
| 系统通知 | 按实际需要，不限频次 |

### 10.4 静默时间段

默认 22:00 - 次日 08:00 不推送（紧急通知除外），用户可自定义。

---

## 十一、新手引导流程

### 11.1 新用户首次打开 App

```
┌─────────────────────────────────────────┐
│          Step 1：欢迎页                 │
│                                         │
│  [产品 Logo]                            │
│  "专为女性设计的生理健康管家"             │
│                                         │
│         [开始体验 →]                     │
└──────────────────┬────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│          Step 2：手机号注册              │
│                                         │
│  +86 [输入手机号      ]                  │
│      [获取验证码]                         │
│  [输入验证码    ]                         │
│                                         │
│      [登录/注册]                         │
│                                         │
│  登录即表示同意《用户协议》《隐私政策》   │
└──────────────────┬────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│          Step 3：完善档案                │
│                                         │
│  昵称：[输入昵称     ]                   │
│  生日：[选择生日     ]                   │
│  最近一次经期开始日期：[选择日期]         │
│  你的周期大约是：[28] 天                 │
│  你的经期大约是：[5] 天                  │
│                                         │
│          [下一步 →]                      │
└──────────────────┬────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│          Step 4：选择角色                │
│                                         │
│  请选择你的当前状态：                    │
│                                         │
│  [🌸 经期管理] ← 默认选中                │
│  [🤰 备孕中]                            │
│  [👶 怀孕中]                            │
│  [🍼 宝妈期]                            │
│                                         │
│          [完成，进入首页 →]              │
└──────────────────┬────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│          Step 5：开启提醒授权            │
│                                         │
│  "开启经期提醒，及时掌握生理状态"         │
│                                         │
│  [开启推送提醒]  [稍后再说]              │
│                                         │
└─────────────────────────────────────────┘
```

### 11.2 老用户角色切换路径

```
首页 → 右下角「我的」→ 点击头像卡片 → 「切换模式」
→ 选择新角色 → 确认切换
```

---

## 十二、模式切换逻辑

### 12.1 经期模式 → 备孕模式

**触发**：用户在「我的」→「切换模式」中主动选择

**变更内容**：
- 用户角色 → `trying`
- 首页显示「备孕中」标识
- 底部导航新增「排卵追踪」Tab
- 经期提醒可保留或关闭
- 出现备孕相关知识推荐

### 12.2 备孕模式 → 怀孕确认

**两种触发方式**：

**方式 A：用户自查后手动确认**
```
我的 → 备孕进度 → 「我怀孕了」
→ 输入末次月经日期 → 确认预产期
→ 角色自动切换为 pregnant
```

**方式 B：同房后标记受孕**
```
同房记录 → 「此次已受孕」
→ 输入确认日期
→ 自动计算预产期
→ 角色切换为 pregnant
```

**变更内容**：
- 用户角色 → `pregnant`
- 首页变成「怀孕模式」：显示当前孕周 + 预产期
- 底部导航新增「孕期」Tab（产检提醒、宝宝发育）
- 经期记录入口隐藏（怀孕后无经期）
- 推送切换为产检提醒、孕期知识

### 12.3 怀孕模式 → 妈妈模式

**触发**：用户主动确认"宝宝已出生"
```
孕期 Tab → 「宝宝已出生」
→ 输入宝宝出生日期 + 宝宝性别（可选）
→ 系统自动创建 baby 记录 + 回填 pregnancy_record.baby_id
→ 角色切换为 mother
```

**baby 记录自动创建逻辑**：
- `baby.name`：用户手动填写（可跳过）
- `baby.birth_date`：取用户输入的出生日期
- `baby.gender`：用户手动选择（可跳过）
- `baby.birth_height / birth_weight`：可后续补充
- `pregnancy_record.baby_id`：宝宝出生后自动回填此字段
- `pregnancy_record.end_date`：自动设为出生日期
- `pregnancy_record.end_reason`：自动设为 "顺产" 或 "剖腹产"（用户选择）

**变更内容**：
- 用户角色 → `mother`
- 首页变成「妈妈模式」：显示宝宝月龄
- 底部导航新增「宝宝成长」Tab
- 产后恢复知识推荐
- 6 个月后可手动切回普通经期模式

### 12.4 模式切换对数据的影响

| 切换 | 数据处理方式 |
|------|------------|
| 经期 → 备孕 | 经期历史保留；重新开始记录排卵数据 |
| 备孕 → 怀孕 | 备孕数据归档；新建怀孕档案（pregnancy_record） |
| 怀孕 → 妈妈 | 自动创建 baby 记录 + 回填 pregnancy_record.baby_id；怀孕记录归档 |

---

## 十三、附录

### 13.1 术语表

| 术语 | 说明 |
|------|------|
| 周期 | 两次经期开始日的间隔天数 |
| 经期 | 阴道出血持续的时间段 |
| 排卵日 | 卵子从卵巢排出的日期 |
| 安全期 | 受孕概率较低的时期 |
| 易孕期 | 受孕概率较高的时期 |
| 基础体温（BBT）| 静息状态下测量的体温，通常指晨起后未活动时的体温 |
| 孕周 | 怀孕周数，从末次月经第一天开始计算 |
| 预产期 | 预计分娩日期，通常为末次月经第一天 + 280 天 |
| 宫颈粘液 | 宫颈分泌的粘液，其性状随周期变化（干燥→湿润→蛋清状） |
| LH峰值 | 黄体生成素峰值，触发排卵的关键激素信号 |

### 13.2 参考资料

- 《妇产科学》人民卫生出版社（第 9 版）
- 世界卫生组织（WHO）生殖健康指南 2023
- 中国女性健康白皮书 2024
- 类似产品体验参考：美柚、大姨妈、Clue（德国）、Flo（美国）
- 中医妇科学：《傅青主女科》人民卫生出版社

---

*本文档为 SheWell 产品需求文档 V4.0.0，如有任何问题或建议，请联系产品团队。*
