# 城市智能电车管理与换电系统 - 后端服务

## 项目概述

本项目是2024年湖南省物联网应用创新设计竞赛的城市智能电车管理与换电系统的后端服务部分。系统实现了电车状态监控、电池管理、地图路径规划、远程控制等功能。

## 技术栈

- **后端框架**: Spring Boot 3.2.0
- **数据库**: MySQL + Redis
- **通信协议**: WebSocket + REST API
- **地图处理**: 自定义网格算法
- **项目管理**: Maven
- **API文档**: Swagger/OpenAPI

## 系统架构

### 核心模块

1. **车辆管理模块** - 车辆状态、位置、电池信息管理
2. **电池管理模块** - 电池状态、历史数据、换电记录
3. **地图服务模块** - 地图加载、路径规划、换电站推荐
4. **通信服务模块** - WebSocket通信、消息推送
5. **报警服务模块** - 异常检测、报警处理
6. **控制服务模块** - 远程控制指令处理

### 数据模型

- **Vehicle**: 车辆实体，包含VID、位置、电池状态等信息
- **Battery**: 电池实体，包含PID、电压、温度等信息
- **BatteryHistory**: 电池历史数据记录
- **ChargingStation**: 换电站实体
- **AlarmRecord**: 报警记录实体

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 配置说明

1. 修改 `src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/citycharge
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

2. 地图文件位于 `src/main/resources/map/map.txt`，格式为100x100的数字矩阵：
   - 0: 道路
   - 1: 换电站
   - 2: 障碍

### 启动应用

```bash
mvn clean install
mvn spring-boot:run
```

应用启动后访问：
- 应用主页: http://localhost:8080/api
- Swagger文档: http://localhost:8080/api/swagger-ui.html

## API接口

### 车辆管理

- `POST /api/vehicles/{vid}/register` - 注册车辆
- `POST /api/vehicles/{vid}/status` - 更新车辆状态
- `GET /api/vehicles/online` - 获取在线车辆列表
- `GET /api/vehicles/{vid}` - 获取车辆信息
- `POST /api/vehicles/{vid}/position` - 更新车辆位置
- `POST /api/vehicles/{vid}/heartbeat` - 车辆心跳

### 地图服务

- `GET /api/map/stations/nearest` - 获取最近换电站
- `GET /api/map/path` - 获取最短路径
- `GET /api/map/reachable` - 检查是否可达

### 远程控制

- `POST /api/control/headlight` - 控制车灯
- `POST /api/control/horn` - 控制喇叭
- `POST /api/control/notification` - 发送通知

## WebSocket通信

系统支持WebSocket实时通信，主要频道：

- `/topic/vehicle/{vid}/status` - 车辆状态更新
- `/topic/vehicle/{vid}/control` - 控制指令
- `/topic/system` - 系统消息
- `/topic/alarms` - 报警通知

## 核心功能

### 电池电量计算

根据电压计算电池剩余电量百分比：
```
C = (Vcurrent - Vmin) / (Vmax - Vmin)
```

### 报警检测

系统自动检测以下异常情况：
1. 电池温度异常 (T ≥ 60°C 或 T ≤ 0°C)
2. 电池电量过低 (C ≤ 20%)
3. 无法到达最近换电站

### 路径规划

基于BFS算法实现最短路径规划，考虑障碍物和电量消耗。

## 开发说明

### 项目结构

```
src/main/java/com/citycharge/
├── config/          # 配置类
├── controller/      # 控制器层
├── dto/            # 数据传输对象
├── entity/         # 实体类
├── repository/     # 数据访问层
├── service/        # 服务层
└── util/           # 工具类
```

### 扩展开发

1. **添加新的实体**: 在 `entity` 包中创建新的实体类
2. **添加API接口**: 在 `controller` 包中创建新的控制器
3. **实现业务逻辑**: 在 `service` 包中实现具体的业务逻辑
4. **工具类扩展**: 在 `util` 包中添加通用工具方法

## 部署说明

### 生产环境配置

1. 修改 `application.yml` 中的生产环境配置
2. 配置数据库连接池参数
3. 设置合适的日志级别
4. 配置Redis集群（如需要）

### 性能优化建议

1. 使用Redis缓存频繁访问的数据
2. 优化数据库查询语句
3. 合理配置线程池参数
4. 启用GZIP压缩减少网络传输

## 联系方式

如有问题请联系项目开发团队。