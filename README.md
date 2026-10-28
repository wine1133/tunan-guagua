# 兔南呱呱同城网

## 在线演示

- 公网演示站：https://wine1133.github.io/tunan-guagua/
- GitHub 仓库：https://github.com/wine1133/tunan-guagua

> GitHub Pages 为前端演示模式，使用浏览器内演示数据，包含用户端与管理端完整交互；正式微服务版本需要 Docker、MySQL、Redis、Nacos 运行。

一套可直接运行的前后端分离同城社区项目。项目围绕“圈子、话题、帖子、评论、收藏、关注、点赞、资讯、轮播图”构建，采用 Spring Cloud Alibaba 微服务架构，并使用 Redisson 分布式锁保护帖子更新。

## 一键启动（Windows）

前置条件：

- JDK 17，并已配置 `JAVA_HOME`
- Maven 3.8+
- Node.js 18+ / npm
- Docker Desktop，且 Linux Engine 已启动

启动：

1. 双击根目录的 `start.bat`
2. 首次启动会自动拉取 MySQL、Redis、Nacos 镜像，安装前端依赖并构建后端
3. 启动完成后浏览器自动打开 `http://localhost:5173`

停止：

- 双击 `stop.bat`
- 容器数据卷会保留，下次启动不会丢失数据

> 如果 Docker Desktop 报 daemon 错误，请先在 Docker Desktop 中确认 Engine 状态为 Running，再重新双击 `start.bat`。

### macOS / Linux

```bash
chmod +x start.sh stop.sh start.command stop.command
./start.sh
# 或双击 start.command
```

停止：

```bash
./stop.sh
```

### 体验账号

| 角色 | 账号 | 密码 |
|---|---|---|
| 管理员 | `admin` | `123456` |
| 普通用户 | `xiaonan` | `123456` |
| 普通用户 | `agua` | `123456` |

### 访问地址

| 服务 | 地址 |
|---|---|
| 用户端 | http://localhost:5173 |
| 管理端 | http://localhost:5173/admin |
| API 网关 | http://localhost:8080 |
| Nacos 控制台 | http://localhost:8848/nacos |
| MySQL | localhost:3307 |
| Redis | localhost:6380 |

## 技术栈

### 后端

- Spring Boot 3.2.5
- Spring Cloud 2023.0.1
- Spring Cloud Alibaba 2023.0.1.0
- Nacos 注册中心
- Spring Cloud Gateway
- OpenFeign 服务调用
- Spring Cloud LoadBalancer 负载均衡
- Redisson 分布式锁
- MyBatis-Plus（基于 MyBatis）
- MySQL 8.0
- Redis
- Hutool
- JWT
- Logback
- Actuator 健康检查

> 原课程中的 Ribbon 已停止维护，且与 Spring Boot 3 / Spring Cloud 2023 不兼容。本项目使用官方替代方案 Spring Cloud LoadBalancer，职责和负载均衡能力保持一致。

### 前端

- Vue 3
- Vue Router
- Axios
- Vite
- Element Plus

## 江南视觉

- 首页轮播、圈子/话题广场、帖子与资讯封面全部使用本地江南水乡图片
- 图片资产位于 `frontend/public/images/jiangnan/`
- 首页轮播使用 3200px 高清版本，并采用左侧渐变压暗以保留文字可读性
- 页面保留轻量悬浮、光影与卡片视差动画，不遮挡主图
- 图片来源及授权信息见 `frontend/public/images/jiangnan/ATTRIBUTION.md`
- 已有数据库可执行 `sql/update-jiangnan-images.sql` 更新图片字段
- 首页第 4 个轮播项使用 30 秒竖版视频，资源位于 rontend/public/videos/jiangnan-carousel.mp4，视频带原声，轮播停留 30 秒后自动切回图片

## 架构

```text
Vue 3 + Element Plus (5173)
            |
            v
Spring Cloud Gateway (8080)
            |
   +--------+---------+----------+
   |                  |          |
User Service       Content     Post Service
  (8081)            (8082)        (8083)
   |                  |          |
guagua_user       guagua_content guagua_post
   |                  |          |
   +---------- MySQL 8.0 --------+
                  |
               Redis / Nacos
```

- `guagua-user-service`：登录注册、用户资料、用户管理、关注/取关
- `guagua-content-service`：圈子、话题、轮播图、同城资讯及审核
- `guagua-post-service`：帖子、评论、点赞、收藏、审核、统计、分布式锁
- `guagua-gateway`：统一入口、路由转发、跨域处理
- `guagua-common`：统一响应、异常处理、JWT、用户上下文

Post 服务通过 OpenFeign 调用 User 服务和 Content 服务，批量补全作者、圈子和话题信息，避免前端多次请求。

## 数据库

初始化脚本：`sql/init.sql`

采用三库隔离：

- `guagua_user`：`users`、`follows`
- `guagua_content`：`circles`、`topics`、`banners`、`news`
- `guagua_post`：`posts`、`comments`、`post_likes`、`post_favorites`

首次创建 Docker 数据卷时会自动执行初始化脚本并写入演示数据。

## Redisson 分布式锁

帖子更新入口位于：

`backend/guagua-post-service/src/main/java/com/tunan/guagua/post/service/PostService.java`

核心逻辑：

```java
RLock lock = redissonClient.getLock("guagua:lock:post:update:" + id);
boolean acquired = lock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS);
if (!acquired) {
    throw new BusinessException(409, "帖子正在被其他操作更新，请稍后重试");
}
try {
    // 校验权限并更新帖子
} finally {
    if (acquired && lock.isHeldByCurrentThread()) {
        lock.unlock();
    }
}
```

同一篇帖子在多实例部署时，只允许一个更新任务进入临界区；锁等待时间与租约时间均可在 `application.yml` 中配置。

## 功能清单

- [x] 项目介绍与可视化首页
- [x] 管理端普通用户信息与账号启停
- [x] 登录、注册、JWT 鉴权
- [x] 管理端圈子审核与管理
- [x] 管理端话题审核与管理
- [x] 管理端帖子审核与管理
- [x] 管理端点赞记录管理
- [x] 管理端收藏记录管理
- [x] 管理端评论管理
- [x] 管理端资讯管理
- [x] 管理端轮播图管理
- [x] 用户端我的圈子
- [x] 用户端我的话题
- [x] 用户端我的帖子
- [x] 圈子广场与圈子详情
- [x] 话题广场与话题详情
- [x] 帖子详情、图片展示、编辑与删除
- [x] 评论、回复、我的评论
- [x] 我的收藏
- [x] 我的点赞
- [x] 关注与取关
- [x] 同城资讯列表与详情
- [x] 首页轮播、热门内容与本地资讯
- [x] 微服务拆分、Gateway、Nacos、Feign、LoadBalancer
- [x] 帖子服务独立部署
- [x] Redisson 分布式锁更新帖子
- [x] 管理端数据统计总览

## 目录结构

```text
.
├── backend
│   ├── pom.xml
│   ├── guagua-common
│   ├── guagua-gateway
│   ├── guagua-user-service
│   ├── guagua-content-service
│   └── guagua-post-service
├── frontend
│   ├── src
│   │   ├── api
│   │   ├── components
│   │   ├── layouts
│   │   ├── pages
│   │   ├── router
│   │   └── stores
│   └── vite.config.js
├── sql
│   └── init.sql
├── docker-compose.yml
├── start.bat
├── stop.bat
├── start.ps1
└── stop.ps1
```

## 本地开发

### 启动基础设施

```powershell
docker compose up -d
```

### 启动后端

```powershell
cd backend
mvn -DskipTests package
```

按顺序启动：

```powershell
java -jar guagua-user-service/target/guagua-user-service-1.0.0.jar
java -jar guagua-content-service/target/guagua-content-service-1.0.0.jar
java -jar guagua-post-service/target/guagua-post-service-1.0.0.jar
java -jar guagua-gateway/target/guagua-gateway-1.0.0.jar
```

### 启动前端

```powershell
cd frontend
npm install
npm run dev
```

## 生产构建

```powershell
cd backend
mvn -DskipTests package

cd ..\frontend
npm run build
```

前端产物位于 `frontend/dist`，可部署到 Nginx；后端各模块的 Spring Boot 可执行 Jar 位于各自 `target` 目录。

## 环境变量

参考 `.env.example`。常用变量：

| 变量 | 默认值 |
|---|---|
| `MYSQL_HOST` | `localhost` |
| `MYSQL_PORT` | `3307` |
| `MYSQL_USER` | `root` |
| `MYSQL_PASSWORD` | `guagua123` |
| `REDIS_HOST` | `localhost` |
| `REDIS_PORT` | `6380` |
| `NACOS_ADDR` | `localhost:8848` |
| `JWT_SECRET` | 开发默认密钥，生产必须替换 |

## 日志与排查

- 启动脚本日志：`logs/`
- 进程 PID：`run/`
- Docker 容器日志：`docker compose logs -f`
- 健康检查：
  - `http://localhost:8080/actuator/health`
  - `http://localhost:8081/actuator/health`
  - `http://localhost:8082/actuator/health`
  - `http://localhost:8083/actuator/health`

如修改了 `sql/init.sql` 并希望重新初始化数据库：

```powershell
docker compose down -v
.\start.ps1
```

`down -v` 会删除本项目 Docker 数据卷，请确认不需要保留数据后执行。

## 说明

本项目定位为企业级教学与实战基础版，已覆盖完整业务闭环。生产环境建议继续补充：

- Nacos 鉴权与独立命名空间
- HTTPS、网关统一鉴权与限流
- 对象存储文件上传
- 数据库读写分离、分库分表与审计日志
- 消息队列解耦点赞/评论等高并发写操作
- Prometheus + Grafana + SkyWalking 可观测体系






