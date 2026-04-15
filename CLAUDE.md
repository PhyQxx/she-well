# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

SheWell（她好）— 女性健康管理平台，包含四个独立子项目：

| 子项目 | 说明 | 技术栈 | 端口 |
|--------|------|--------|------|
| `she-well-backend` | REST API 后端 | Java 17, Spring Boot 3.2.5, MyBatis-Plus, MySQL, Redis | 8097 |
| `she-well-ui` | PC 端用户界面 | Vue 3, Vite, Element Plus, Pinia, ECharts | 8091 |
| `she-well-mobile` | 移动端（H5/微信小程序/App） | UniApp (Vue 3), DCloudio, Pinia | — |
| `she-well-admin` | 管理后台 | Vue 3, Vite, Element Plus, ECharts | 8013 |

各子项目独立构建，无 monorepo 构建工具。

## 构建与运行命令

### 后端（she-well-backend）

```bash
cd she-well-backend
mvn clean package            # 构建
mvn spring-boot:run          # 运行（端口 8097）
```

依赖：Java 17、MySQL（127.0.0.1:3306/she_well）、Redis（127.0.0.1:6379）
API 文档：启动后访问 `/doc.html`（Knife4j/Swagger）

### PC 端（she-well-ui）

```bash
cd she-well-ui
npm install
npm run dev     # 开发服务器端口 8091，/api 代理到 localhost:8097
npm run build
```

### 移动端（she-well-mobile）

```bash
cd she-well-mobile
npm install
npm run dev:h5              # H5 开发
npm run dev:mp-weixin       # 微信小程序开发
npm run build:h5            # H5 构建
```

注意：移动端 API 地址默认指向 `localhost:8080`，与后端实际端口 8097 不一致，需手动调整。

### 管理后台（she-well-admin）

```bash
cd she-well-admin
npm install
npm run dev     # 开发服务器端口 8013，/api 代理到 localhost:8080
npm run build
```

## 架构要点

### 后端分层

```
controller/  → 31 个 REST 控制器，所有接口以 /api/ 为前缀
service/     → 接口定义
service/impl/ → 43 个服务实现
service/ai/  → AI 服务（DeepSeek/GLM/MiniMax，当前为 demo 模式）
mapper/      → MyBatis-Plus Mapper，无 XML，全部使用 Lambda 查询
entity/      → 37 个实体类，自动递增 ID，逻辑删除字段 isDeleted
config/      → WebConfig（跨域、拦截器注册）、RedisConfig
interceptor/ → AuthInterceptor（JWT 验证，排除 /api/auth/** 和 /api/public/**）
util/        → JwtUtil（HS256, 24h TTL）、Result（统一响应）
exception/   → GlobalExceptionHandler
```

### 认证机制

- JWT Bearer Token，存储在 `Authorization` 头
- PC/移动端用 `sw_token`，管理后台用 `sw_admin_token`
- **未实现角色权限控制**：`/api/admin/**` 端点无独立角色校验

### 数据层

- MySQL + Druid 连接池（5-20 连接）
- Redis 缓存：AI 建议（24h）、轮播图（5min）、热门文章（1h）
- 无 SQL 迁移文件，数据库结构需手动维护

### 前端共性

- 统一使用 Pinia 状态管理 + Axios（移动端用 uni.request）
- 请求拦截器自动附加 Token，401 自动跳转登录
- Element Plus 组件库 + ECharts 图表

### 文件上传

基于 FTP 存储，支持头像、帖子、文章、排卵试纸、基础体温、产检、轮播图、专家等多种业务类型。

## gstack

使用 `/browse` skill 进行所有网页浏览操作。不要使用 `mcp__claude-in-chrome__*` 工具。

可用的 gstack skills：
`/office-hours`, `/plan-ceo-review`, `/plan-eng-review`, `/plan-design-review`, `/design-consultation`, `/design-shotgun`, `/design-html`, `/review`, `/ship`, `/land-and-deploy`, `/canary`, `/benchmark`, `/browse`, `/connect-chrome`, `/qa`, `/qa-only`, `/design-review`, `/setup-browser-cookies`, `/setup-deploy`, `/retro`, `/investigate`, `/document-release`, `/codex`, `/cso`, `/autoplan`, `/plan-devex-review`, `/devex-review`, `/careful`, `/freeze`, `/guard`, `/unfreeze`, `/gstack-upgrade`, `/learn`

## 已知问题

- 移动端 API 端口配置（8080）与后端实际端口（8097）不一致
- AI 服务当前为 demo 模式，返回硬编码假数据
- 无测试代码
- 无数据库迁移脚本
