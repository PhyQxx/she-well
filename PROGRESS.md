# SheWell 项目开发进度

> 最后更新：2026-04-14
> 项目路径：`/home/phy/IdeaProjects/PHY/she-well/`

---

## 任务总览

| # | 任务 | 状态 |
|---|------|------|
| 1 | 建表 SQL（40张表） | ✅ 完成 |
| 2 | Spring Boot 后端骨架 | ✅ 完成 |
| 3 | Entity + Mapper（40个类） | ✅ 完成 |
| 4 | Service + Controller（100+ API） | ✅ 完成 |
| 5 | PC 前端项目（Vue3 + TS + Vite） | ✅ 完成 |
| 6 | 后端缺失代码（AI/FTP/异常处理/5个新Controller） | ✅ 完成 |
| 7 | 移动端项目（UniApp + Vue3，5个页面） | ✅ 完成 |
| 8 | 完善 AI 模块设计（PRD 4.13节） | ✅ 完成 |
| 9 | 后端编译验证 + JAR 打包 | ✅ 完成 |
| 10 | PC 前端构建验证（Vite 4 + Node 18 兼容版本） | ✅ 完成 |
| 11 | 运营后台构建验证（she-well-admin，10个管理页面） | ✅ 完成 |
| 12 | 移动端 H5 构建验证 | ⚠️ Node 18 不兼容 Vite 5，需 Node 20+ |
| 13 | 移动端 4 TabBar 动态模式切换（经期/备孕/怀孕/妈妈） | ✅ 完成 |
| 14 | PC 端补充页面（Discovery/Achievement/Notification） | ✅ 完成 |
| 15 | 后端补充接口（管理员禁用用户/社区审核/统计看板） | ✅ 完成 |

---

## 当前进行中：任务 6 — 后端缺失代码

### 已完成
- ✅ `pom.xml` — 已补充 knife4j、OkHttp（AI）、commons-net（FTP）
- ✅ `Result.java` — 统一响应格式
- ✅ `JwtUtil.java` — JWT 工具
- ✅ `AuthInterceptor.java` — 登录拦截
- ✅ `WebConfig.java` — CORS + 拦截器注册
- ✅ `RedisConfig.java` — RedisTemplate 配置
- ✅ `FtpConfig.java` — FTP 连接配置
- ✅ `FtpUtil.java` — FTP 上传工具
- ✅ `AiService.java` — AI 服务接口
- ✅ `AiServiceImpl.java` — AI 服务实现（DeepSeek/GLM/MiniMax 三模型，演示模式）
- ✅ `AiController.java` — 完善版（咨询/建议/试纸识别/周期分析）
- ✅ `GlobalExceptionHandler.java` — 全局异常处理
- ✅ `HabitController.java` — 习惯 CRUD
- ✅ `StatsController.java` — 统计数据
- ✅ `BabyController.java` — 宝宝记录 CRUD
- ✅ `ActivityController.java` — 活动列表
- ✅ `HealthReportController.java` — 健康报告
- ✅ `BannerController.java` — Banner 管理（带 Redis 5min 缓存）
- ✅ `FileController.java` — FTP 文件上传（头像/帖子图片/打卡）
- ✅ `DiscoveryController.java` — 发现页（Banner/热门文章/推荐）

### 待完成（任务 6）

需要创建以下文件：

```
src/main/java/com/shewell/
├── config/
│   └── FtpConfig.java          # FTP 连接配置
├── util/
│   └── FtpUtil.java            # FTP 上传工具
├── service/ai/
│   ├── AiService.java          # AI 服务接口
│   └── impl/AiServiceImpl.java # AI 服务实现（DeepSeek/GLM/MiniMax）
├── controller/ai/
│   └── AiController.java      # 已存在，需完善
└── exception/
    └── GlobalExceptionHandler.java  # 全局异常处理
```

---

## 文件结构（完整）

```
she-well/
├── PRD.md                      # 产品需求文档 V4.0.0
├── PROGRESS.md                 # 本文件
├── sql/
│   └── init.sql                # 建表 SQL（757行，40张表）
├── she-well-backend/           # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/shewell/
│       │   ├── SheWellApplication.java
│       │   ├── config/
│       │   │   ├── RedisConfig.java
│       │   │   └── WebConfig.java
│       │   ├── interceptor/
│       │   │   └── AuthInterceptor.java
│       │   ├── util/
│       │   │   ├── Result.java
│       │   │   └── JwtUtil.java
│       │   ├── entity/         # 40个实体类
│       │   ├── mapper/         # 40个 Mapper
│       │   ├── service/        # 40个 Service + 40个 Impl
│       │   └── controller/     # 12个 Controller
│       └── resources/
│           └── application.yml
└── she-well-ui/                # PC 前端（Vue3 + TS）
    ├── package.json
    ├── vite.config.ts
    ├── index.html
    └── src/
        ├── main.ts
        ├── App.vue
        ├── api/index.ts
        ├── stores/auth.ts
        ├── router/index.ts
        └── views/
            ├── Login.vue
            ├── Layout.vue
            ├── Home.vue
            ├── Period.vue
            ├── Pregnancy.vue
            ├── Community.vue
            ├── Knowledge.vue
            ├── Health.vue
            └── Settings.vue
```

---

## 任务 6 详细待办

### 1. FtpConfig.java
路径：`src/main/java/com/shewell/config/FtpConfig.java`
- 读取 application.yml 中的 FTP 配置
- 创建 FTPClient Bean

### 2. FtpUtil.java
路径：`src/main/java/com/shewell/util/FtpUtil.java`
- `upload(byte[] data, String remotePath, String fileName) : String` — 上传文件，返回 URL
- `uploadInputStream(InputStream is, String remotePath, String fileName) : String`
- `delete(String remotePath) : boolean`
- 支持配置化（主机/端口/用户名/密码/基础路径）

### 3. AiService.java + AiServiceImpl.java
路径：`src/main/java/com/shewell/service/ai/`
- 接口定义
- 实现：支持 DeepSeek / 智谱 GLM / MiniMax 三个模型
- 配置项：apiUrl、apiKey、modelName、temperature
- 统一 chat 方法：`String chat(String userMessage, AiContext context)`
- 医疗免责声明注入（每次回复末尾追加）

### 4. AiController.java（完善现有）
路径：`src/main/java/com/shewell/controller/AiController.java`
- 已生成，需要补充完整 AI 对话逻辑
- 新增端点：
  - `POST /api/ai/consult` — AI 健康咨询
  - `GET /api/ai/history` — 咨询历史
  - `GET /api/ai/advice` — 每日健康建议
  - `POST /api/ai/recognize-strip` — AI 试纸识别
  - `GET /api/ai/cycle-analysis` — AI 周期分析

### 5. GlobalExceptionHandler.java
路径：`src/main/java/com/shewell/exception/GlobalExceptionHandler.java`
- `@RestControllerAdvice`
- 处理：业务异常、验证异常、AI 调用异常、未知异常
- 统一返回 `Result.fail(code, message)`

---

## 任务 7：移动端（UniApp）

✅ 已完成 `she-well-mobile/` 目录，共 13 个文件：

```
she-well-mobile/
├── package.json              # UniApp + Vue3（与 biaofan-sop mobile 同版本）
├── src/
│   ├── pages.json            # 4 TabBar（首页/经期/打卡/我的）+ 登录页
│   ├── manifest.json         # App 配置（权限：相机/存储）
│   ├── main.js
│   ├── App.vue
│   ├── uni.scss              # SheWell 品牌色变量
│   ├── api/index.js          # API 调用封装（基于后端 /api/*）
│   ├── store/auth.js         # Pinia 认证状态
│   └── pages/
│       ├── login/login.vue   # 手机号+验证码登录
│       ├── index/index.vue   # 首页（Banner/概览/AI建议/快捷入口/打卡）
│       ├── period/index.vue   # 经期（记录/预测/怀孕切换）
│       ├── checkin/index.vue  # 打卡（习惯管理/今日打卡/弹窗）
│       └── mine/mine.vue     # 我的（用户卡/菜单/退出）
└── static/                   # TabBar 图标占位（需替换真实图片）
```

**注意**：TabBar 图标目前是空文件占位，需要替换为真实图片。

---

## 任务 8：完善 AI 模块设计（PRD 补充）

✅ 已完成。`PRD.md` 新增 **4.13 AI 健康助手模块**（共7个子节）：

### 4.13 AI 健康助手模块

- **4.13.1 产品定位与边界**：AI 助手定位、不能诊断不能开方的边界
- **4.13.2 AI 使用场景**：5个场景（健康咨询/每日建议/周期分析/试纸识别/症状分析）+ 缓存策略
- **4.13.3 System Prompt 设计**：基础角色设定 + 场景化 Prompt 片段（经期问题/试纸识别/周期分析）
- **4.13.4 个性化上下文注入规则**：5类数据源 + 注入格式模板
- **4.13.5 AI 模型配置**：DeepSeek / GLM / MiniMax 三模型 + API 地址
- **4.13.6 数据安全与隐私**：对话不持久化、HTTPS传输、日志脱敏
- **4.13.7 免责声明展示规范**：4个场景的展示方式与文本

同时补充了 **4.12.2 生育力评估说明**（level/factors/suggestions 三字段）。

---

## 快速继续指令

```bash
# 任务9 — 完善后端缺失的文件
# FTP上传 FileController、文件上传配置、bodyMetrics/Car/Exercise等 Service

# 任务10 — 后端编译验证
cd /home/phy/IdeaProjects/PHY/she-well/she-well-backend
# 验证 Maven 能否正常编译

# 任务11 — PC前端完善
# she-well-ui 补充更多 view 页面（Knowledge/Community/Expert 等）

# 任务12 — 运营后台
# she-well-admin/ 目录新建（参考 biaofan-sop-admin）
```

---

## 项目完整文件清单（截至 2026-04-14）

```
she-well/
├── PRD.md                       # 产品需求文档 V4.0.0 + 4.13 AI模块
├── PROGRESS.md                  # 本文件
├── sql/init.sql                 # 建表 SQL（757行，40张表）
│
├── she-well-backend/             # Spring Boot 后端
│   ├── pom.xml                  # 依赖完整（knife4j/OkHttp/commons-net）
│   └── src/main/
│       ├── java/com/shewell/
│       │   ├── SheWellApplication.java
│       │   ├── config/
│       │   │   ├── RedisConfig.java
│       │   │   └── WebConfig.java
│       │   ├── interceptor/
│       │   │   └── AuthInterceptor.java
│       │   ├── util/
│       │   │   ├── Result.java
│       │   │   ├── JwtUtil.java
│       │   │   ├── FtpConfig.java     # ✅ 新增
│       │   │   └── FtpUtil.java       # ✅ 新增
│       │   ├── entity/                 # 40个实体类
│       │   ├── mapper/                 # 40个 Mapper
│       │   ├── service/                # 40个 Service
│       │   │   └── ai/
│       │   │       ├── AiService.java  # ✅ 新增
│       │   │       └── impl/
│       │   │           └── AiServiceImpl.java  # ✅ 新增
│       │   ├── controller/             # 20个 Controller（含 AiController ✅完善）
│       │   └── exception/
│       │       └── GlobalExceptionHandler.java  # ✅ 新增
│       └── resources/
│           └── application.yml
│
├── she-well-ui/                 # PC 前端（Vue3 + TS + Vite）
│   └── src/...
│
└── she-well-mobile/             # 移动端（UniApp + Vue3）✅ 新增
    ├── package.json
    ├── src/
    │   ├── pages.json            # 4 TabBar
    │   ├── main.js
    │   ├── App.vue
    │   ├── uni.scss
    │   ├── api/index.js
    │   ├── store/auth.js
    │   └── pages/
    │       ├── login/login.vue
    │       ├── index/index.vue
    │       ├── period/index.vue
    │       ├── checkin/index.vue
    │       └── mine/mine.vue
    └── static/                   # TabBar 图标占位（需替换）
```
