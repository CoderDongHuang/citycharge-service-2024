# CityCharge - 城市智能电车管理与换电系统

## 项目概述

本项目是 **2024年湖南省物联网应用创新设计竞赛** 的参赛作品 —— 城市智能电车管理与换电系统的后端服务部分。系统基于 Spring Boot 框架构建，实现了智能电车全生命周期管理，涵盖车辆状态实时监控、电池健康度管理、智能换电站推荐、路径规划、远程控制、异常报警等核心功能。系统采用前后端分离架构，通过 RESTful API 与 WebSocket 双向通信，为智能出行场景提供完整的数据支撑与业务逻辑支持。

---

## 技术栈

### 后端核心框架
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 核心框架 |
| Spring Data JPA | - | ORM持久化框架 |
| Spring Data Redis | - | 缓存层 |
| Spring WebSocket | - | 实时通信 |
| Spring Boot Mail | - | 邮件服务 |

### 数据存储
| 技术 | 版本 | 说明 |
|------|------|------|
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存与会话管理 |

### 通信协议
| 技术 | 版本 | 说明 |
|------|------|------|
| MQTT (Eclipse Paho) | 1.2.5 | 物联网消息协议 |
| WebSocket | - | 前端实时推送 |
| REST API | - | HTTP接口服务 |

### 安全与认证
| 技术 | 版本 | 说明 |
|------|------|------|
| JWT (jjwt) | 0.11.5 | Token认证 |
| GitHub OAuth | - | 第三方登录 |

### 工具库
| 技术 | 版本 | 说明 |
|------|------|------|
| Lombok | - | 代码简化 |
| Apache Commons Lang3 | - | 通用工具 |
| Jackson | - | JSON序列化 |
| SpringDoc OpenAPI | 1.7.0 | API文档生成 |

### AI集成
| 技术 | 说明 |
|------|------|
| DeepSeek API | 智能对话服务 |

---

## 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端应用层                                │
│                   (Vue.js / React SPA)                          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API Gateway Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │ REST API    │  │ WebSocket   │  │ MQTT Broker │             │
│  │ (HTTP)      │  │ (WS)        │  │ (TCP:1883)  │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Spring Boot Service                         │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │ Controller   │ │ Service      │ │ Repository   │            │
│  │ Layer        │→│ Layer        │→│ Layer        │            │
│  └──────────────┘ └──────────────┘ └──────────────┘            │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │ JWT Auth     │ │ MQTT Handler │ │ Alarm Service│            │
│  └──────────────┘ └──────────────┘ └──────────────┘            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Data Storage Layer                        │
│  ┌──────────────┐              ┌──────────────┐                │
│  │ MySQL 8.0    │              │ Redis 6.0    │                │
│  │ (持久化存储)  │              │ (缓存层)      │                │
│  └──────────────┘              └──────────────┘                │
└─────────────────────────────────────────────────────────────────┘
```

---

## 核心功能模块

### 1. 车辆管理模块 (Vehicle Management)
- 车辆注册与状态管理
- 实时位置追踪与更新
- 车辆在线状态监控
- 心跳检测与超时处理

### 2. 电池管理模块 (Battery Management)
- 电池状态实时监控（电压、温度、电量）
- 电池历史数据记录与分析
- 电量百分比计算算法
- 换电记录管理

### 3. 换电站管理模块 (Station Management)
- 换电站信息CRUD操作
- 站点照片上传与管理
- 站点状态上报功能
- 基于地理位置的附近站点查询

### 4. 地图服务模块 (Map Service)
- 100x100 网格地图加载
- BFS 最短路径规划算法
- 最近换电站推荐
- 可达性分析（基于电量）

### 5. 报警服务模块 (Alarm Service)
- 电池温度异常报警（T ≥ 60°C 或 T ≤ 0°C）
- 电量过低报警（C ≤ 20%）
- 报警级别自动判定（critical/high/medium/low）
- 重复报警过滤（5分钟内同类型）
- 报警日志持久化

### 6. 远程控制模块 (Remote Control)
- 车灯控制（开/关）
- 喇叭控制
- 消息通知推送
- WebSocket 实时指令下发

### 7. 用户系统模块 (User System)
- 用户注册/登录
- JWT Token 认证
- GitHub OAuth 第三方登录
- 用户资料管理
- 用户车辆/电池绑定

### 8. 消息通知模块 (Notification)
- 系统消息推送
- 站内消息管理
- 邮件通知服务
- 钉钉机器人集成

### 9. AI 智能对话模块
- DeepSeek API 集成
- 智能问答服务

---

## 数据模型

### 核心实体

| 实体 | 说明 | 主要字段 |
|------|------|----------|
| Vehicle | 车辆实体 | vid, position, batteryLevel, status |
| Battery | 电池实体 | pid, voltage, temperature, capacity |
| BatteryHistory | 电池历史 | pid, voltage, temperature, timestamp |
| Station | 换电站实体 | stationId, name, latitude, longitude, availableBatteries |
| StationPhoto | 站点照片 | photoId, stationId, url, type |
| User | 用户实体 | id, username, email, password, role |
| UserVehicle | 用户车辆关联 | userId, vid |
| UserBattery | 用户电池关联 | userId, pid |
| UserOrder | 用户订单 | orderId, userId, stationId, status |
| AlarmRecord | 报警记录 | id, vid, type, level, timestamp |
| AlertLog | 告警日志 | id, vid, type, level, triggerValue |
| Message | 系统消息 | id, title, content, type |
| ContactMessage | 联系消息 | id, name, email, message |

---

## 快速开始

### 环境要求

| 软件 | 版本要求 |
|------|----------|
| JDK | 1.8+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Maven | 3.6+ |
| MQTT Broker | (如 EMQX、Mosquitto) |

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd citycharge-service-2024
```

2. **创建数据库**
```sql
CREATE DATABASE citycharge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **配置环境变量** (可选，或直接修改 application.yml)

创建 `.env` 文件：
```properties
DB_USERNAME=root
DB_PASSWORD=your_password
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-jwt-secret-key
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
DEEPSEEK_API_KEY=your-deepseek-api-key
MAIL_USERNAME=your-email@qq.com
MAIL_PASSWORD=your-email-auth-code
```

4. **修改配置文件**

编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/citycharge
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:123456}
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
```

5. **启动 MQTT Broker**
```bash
# 使用 Docker 启动 EMQX
docker run -d --name emqx -p 1883:1883 -p 8083:8083 -p 8084:8084 -p 8883:8883 -p 18083:18083 emqx/emqx:latest
```

6. **构建并运行**
```bash
mvn clean install
mvn spring-boot:run
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 应用主页 | http://localhost:8080/api |
| Swagger API 文档 | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## API 接口文档

### 认证接口 (Auth)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 用户登录 |
| POST | `/auth/register` | 用户注册 |
| POST | `/auth/logout` | 用户登出 |
| GET | `/auth/user` | 获取当前用户信息 |
| POST | `/auth/refresh` | 刷新 Token |
| GET | `/auth/github` | GitHub OAuth 登录 |

### 车辆接口 (Vehicle)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/vehicles/{vid}/register` | 注册车辆 |
| POST | `/api/vehicles/{vid}/status` | 更新车辆状态 |
| GET | `/api/vehicles/online` | 获取在线车辆列表 |
| GET | `/api/vehicles/{vid}` | 获取车辆详情 |
| POST | `/api/vehicles/{vid}/position` | 更新车辆位置 |
| POST | `/api/vehicles/{vid}/heartbeat` | 车辆心跳 |

### 电池接口 (Battery)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/batteries/{pid}` | 获取电池信息 |
| GET | `/api/batteries/{pid}/history` | 获取电池历史数据 |
| POST | `/api/batteries/{pid}/status` | 更新电池状态 |

### 换电站接口 (Station)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/stations` | 获取站点列表 |
| GET | `/api/stations/{stationId}` | 获取站点详情 |
| GET | `/api/stations/search` | 搜索站点 |
| GET | `/api/stations/nearby` | 获取附近站点 |
| GET | `/api/stations/available` | 获取可用站点 |
| POST | `/api/stations/{stationId}/photos` | 上传站点照片 |
| DELETE | `/api/stations/{stationId}/photos/{photoId}` | 删除站点照片 |
| POST | `/api/stations/{stationId}/status-report` | 上报站点状态 |

### 地图接口 (Map)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/map/stations/nearest` | 获取最近换电站 |
| GET | `/api/map/path` | 获取最短路径 |
| GET | `/api/map/reachable` | 检查是否可达 |

### 远程控制接口 (Control)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/control/headlight` | 控制车灯 |
| POST | `/api/control/horn` | 控制喇叭 |
| POST | `/api/control/notification` | 发送通知 |

### 用户接口 (User)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/user/profile` | 获取用户资料 |
| PUT | `/api/user/profile` | 更新用户资料 |
| PUT | `/api/user/password` | 修改密码 |
| GET | `/api/user/vehicles` | 获取用户车辆 |
| GET | `/api/user/batteries` | 获取用户电池 |
| GET | `/api/user/orders` | 获取用户订单 |
| GET | `/api/user/stats` | 获取用户统计 |

### 管理员接口 (Admin)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/statistics` | 获取系统统计 |
| GET | `/api/admin/users` | 获取用户列表 |
| GET | `/api/admin/messages` | 获取消息列表 |
| POST | `/api/admin/messages/send` | 发送消息 |

### AI 对话接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/ai/chat` | AI 智能对话 |

---

## WebSocket 通信

### 连接地址
```
ws://localhost:8080/ws
```

### 订阅主题

| 主题 | 说明 |
|------|------|
| `/topic/vehicle/{vid}/status` | 车辆状态更新 |
| `/topic/vehicle/{vid}/control` | 控制指令 |
| `/topic/system` | 系统消息 |
| `/topic/alarms` | 报警通知 |
| `/topic/vehicle/{vid}/online` | 在线状态变化 |

### 消息格式示例

**车辆状态更新：**
```json
{
  "type": "status_update",
  "vid": "V001",
  "position": {"x": 50, "y": 30},
  "batteryLevel": 75.5,
  "timestamp": "2024-01-15T10:30:00"
}
```

**控制指令：**
```json
{
  "type": "control",
  "command": "headlight",
  "value": "on",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## MQTT 通信

### Broker 配置
- 地址: `tcp://localhost:1883`
- 清除会话: `true`
- 连接超时: `10s`
- 保活间隔: `60s`
- 自动重连: `开启`

### 主题设计

| 主题 | 方向 | 说明 |
|------|------|------|
| `vehicle/{vid}/status` | 上行 | 车辆状态上报 |
| `vehicle/{vid}/alarm` | 上行 | 电池报警消息 |
| `vehicle/{vid}/control` | 下行 | 控制指令下发 |
| `vehicle/{vid}/heartbeat` | 上行 | 心跳消息 |

### 报警消息格式
```json
{
  "vid": "V001",
  "pid": "P001",
  "type": "lowBattery",
  "level": "high",
  "triggerValue": 15.5,
  "thresholdValue": 20.0,
  "positionX": 50,
  "positionY": 30,
  "message": "电池电量过低",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 核心算法

### 电池电量计算

根据当前电压计算电池剩余电量百分比：

```
C = (Vcurrent - Vmin) / (Vmax - Vmin) × 100%

其中:
- Vcurrent: 当前电压
- Vmin: 最低电压 (默认 3.0V)
- Vmax: 最高电压 (默认 4.2V)
```

### 报警级别判定

根据触发值与阈值的偏差百分比确定报警级别：

| 偏差百分比 | 级别 |
|-----------|------|
| > 20% | critical (严重) |
| > 10% | high (高) |
| > 5% | medium (中) |
| ≤ 5% | low (低) |

### 路径规划

采用 **BFS (广度优先搜索)** 算法实现最短路径规划：
- 地图格式: 100×100 网格矩阵
- 0: 道路 (可通行)
- 1: 换电站
- 2: 障碍物 (不可通行)
- 支持电量消耗计算

---

## 项目结构

```
src/main/java/com/citycharge/
├── CityChargeApplication.java     # 启动类
├── common/
│   └── ApiResponse.java           # 统一响应封装
├── config/
│   ├── CorsConfig.java            # 跨域配置
│   ├── MqttConfig.java            # MQTT配置
│   ├── MqttMessageListenerConfig.java
│   ├── WebConfig.java             # Web配置
│   └── WebSocketConfig.java       # WebSocket配置
├── controller/
│   ├── AuthController.java        # 认证控制器
│   ├── VehicleController.java     # 车辆控制器
│   ├── BatteryController.java     # 电池控制器
│   ├── StationController.java     # 换电站控制器
│   ├── UserStationController.java # 用户站点控制器
│   ├── MapController.java         # 地图控制器
│   ├── ControlController.java     # 远程控制控制器
│   ├── AlarmController.java       # 报警控制器
│   ├── DashboardController.java   # 仪表盘控制器
│   ├── UserVehicleController.java # 用户车辆控制器
│   ├── UserBatteryController.java # 用户电池控制器
│   ├── UserOrderController.java   # 用户订单控制器
│   ├── UserProfileController.java # 用户资料控制器
│   ├── UserMessageController.java # 用户消息控制器
│   ├── UserStatsController.java   # 用户统计控制器
│   ├── AdminStationController.java
│   ├── AdminMessageController.java
│   ├── AdminUserDataController.java
│   ├── AiChatController.java      # AI对话控制器
│   ├── GitHubAuthController.java  # GitHub OAuth
│   ├── ContactMessageController.java
│   └── NotificationController.java
├── dto/                           # 数据传输对象
├── entity/                        # 实体类
├── repository/                    # 数据访问层
├── service/                       # 服务层
│   ├── VehicleService.java
│   ├── BatteryService.java
│   ├── BatteryAlarmService.java
│   ├── MapService.java
│   ├── AlarmService.java
│   ├── VehicleControlService.java
│   ├── VehicleOnlineStatusService.java
│   ├── UserVehicleService.java
│   ├── UserBatteryService.java
│   ├── UserStationService.java
│   ├── UserOrderService.java
│   ├── UserProfileService.java
│   ├── UserMessageService.java
│   ├── AdminStationService.java
│   ├── AdminMessageService.java
│   ├── AdminUserDataService.java
│   ├── AiChatService.java
│   ├── GitHubOAuthService.java
│   ├── EmailService.java
│   ├── DingtalkService.java
│   ├── MqttVehicleStatusService.java
│   ├── UserVehicleMqttService.java
│   └── MessageMqttService.java
└── util/
    ├── JwtUtil.java               # JWT工具类
    ├── AuthUtil.java              # 认证工具类
    ├── BatteryCalculator.java     # 电池计算工具
    └── MapLoader.java             # 地图加载工具
```

---

## 配置说明

### 核心配置项

```yaml
citycharge:
  map:
    width: 100
    height: 100
    file-path: classpath:map/map.txt
  
  battery:
    min-voltage: 3.0      # 最低电压
    max-voltage: 4.2      # 最高电压
    
  alarm:
    low-battery-threshold: 0.2       # 低电量阈值 (20%)
    high-temperature-threshold: 60   # 高温阈值 (°C)
    low-temperature-threshold: 0     # 低温阈值 (°C)
    
  notification:
    charging-station-threshold: 0.3  # 推荐换电站阈值

jwt:
  secret: your-jwt-secret-key
  expiration: 86400000   # Token有效期 (24小时)
```

### 文件上传配置

```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 5MB
      max-request-size: 10MB

avatar:
  upload:
    path: ${user.home}/citycharge-uploads/avatars

station:
  upload:
    path: ${user.home}/citycharge-uploads/stations
```

---

## 部署说明

### Docker 部署

1. **构建镜像**
```bash
docker build -t citycharge-service:latest .
```

2. **运行容器**
```bash
docker run -d \
  --name citycharge-service \
  -p 8080:8080 \
  -e DB_HOST=mysql \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e REDIS_HOST=redis \
  citycharge-service:latest
```

### Docker Compose 部署

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: citycharge
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:6-alpine
    volumes:
      - redis_data:/data

  emqx:
    image: emqx/emqx:latest
    ports:
      - "1883:1883"
      - "18083:18083"

  citycharge:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
      - emqx
    environment:
      DB_HOST: mysql
      DB_USERNAME: root
      DB_PASSWORD: password
      REDIS_HOST: redis
      MQTT_HOST: emqx

volumes:
  mysql_data:
  redis_data:
```

### 生产环境建议

1. **数据库优化**
   - 配置连接池参数
   - 开启慢查询日志
   - 定期备份

2. **Redis 配置**
   - 设置密码认证
   - 配置持久化
   - 主从复制（高可用）

3. **性能优化**
   - 启用 GZIP 压缩
   - 配置 CDN 加速静态资源
   - 合理设置 JVM 参数

4. **安全加固**
   - 启用 HTTPS
   - 配置防火墙规则
   - 定期更新依赖版本

---

## 开发指南

### 添加新实体

1. 在 `entity` 包中创建实体类
2. 在 `repository` 包中创建 Repository 接口
3. 在 `dto` 包中创建 DTO 类
4. 在 `service` 包中实现业务逻辑
5. 在 `controller` 包中创建控制器

### 代码规范

- 使用 Lombok 简化代码
- 遵循 RESTful API 设计规范
- 统一使用 `ApiResponse` 封装响应
- Service 层处理业务逻辑，Controller 层仅负责请求转发

### 日志规范

```java
@Slf4j
@Service
public class ExampleService {
    public void process() {
        log.debug("调试信息");
        log.info("普通信息");
        log.warn("警告信息");
        log.error("错误信息", exception);
    }
}
```

---

## 常见问题

### Q: 启动时报数据库连接失败？
A: 检查 MySQL 服务是否启动，数据库是否创建，配置信息是否正确。

### Q: MQTT 连接失败？
A: 确保 MQTT Broker 已启动，端口 1883 可访问。

### Q: WebSocket 连接被拒绝？
A: 检查跨域配置，确保前端地址在允许列表中。

### Q: JWT Token 验证失败？
A: 检查 Token 是否过期，密钥配置是否一致。

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2024 | 初始版本，实现核心功能 |

---

## 许可证

本项目仅供学习和竞赛使用。

---

## 联系方式

如有问题请联系项目开发团队。
