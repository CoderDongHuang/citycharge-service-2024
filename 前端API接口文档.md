# 前端API接口文档

## 概述
本文档详细描述了前端各个界面使用的REST API接口，包括请求路径、方法、参数和响应格式。

## 基础信息
- **基础URL**: `http://localhost:8080/api`
- **响应格式**: JSON
- **认证方式**: 无（开发环境）

## API接口分类

### 1. 车辆管理API

#### 1.1 获取车辆列表
- **方法**: `GET`
- **路径**: `/vehicles`
- **参数**: 无
- **使用界面**: 车辆管理页面、地图监控页面
- **响应格式**:
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "vid": "V001",
      "status": "online",
      "location": {"lat": 39.9042, "lng": 116.4074},
      "batteryLevel": 85,
      "lastUpdate": "2024-01-18T10:30:00"
    }
  ]
}
```

#### 1.2 获取单个车辆信息
- **方法**: `GET`
- **路径**: `/vehicles/{vid}`
- **路径参数**: `vid` - 车辆编号
- **使用界面**: 车辆详情弹窗
- **响应格式**: 同车辆列表

#### 1.3 更新车辆状态
- **方法**: `PUT`
- **路径**: `/vehicles/{vid}/status`
- **路径参数**: `vid` - 车辆编号
- **请求体**: 状态对象
- **使用界面**: 车辆管理页面

#### 1.4 车辆控制接口

##### 灯光控制
- **方法**: `POST`
- **路径**: `/vehicles/{vid}/control/lights`
- **参数**: `command` - 控制命令（on/off）
- **使用界面**: 车辆管理页面、详情弹窗

##### 灯光闪烁
- **方法**: `POST`
- **路径**: `/vehicles/{vid}/control/flash`
- **参数**: `command` - 控制命令（start/stop）
- **使用界面**: 车辆管理页面、详情弹窗

##### 喇叭控制
- **方法**: `POST`
- **路径**: `/vehicles/{vid}/control/horn`
- **参数**: `command` - 控制命令（beep）
- **使用界面**: 车辆管理页面、详情弹窗

##### 位置设置
- **方法**: `POST`
- **路径**: `/vehicles/{vid}/control/position`
- **参数**: `command` - 位置命令
- **使用界面**: 车辆管理页面、详情弹窗、地图监控页面

### 2. 电池管理API

#### 2.1 获取电池列表
- **方法**: `GET`
- **路径**: `/batteries`
- **查询参数**:
  - `page` (可选): 页码，默认0
  - `size` (可选): 每页大小，默认20
- **使用界面**: 电池管理页面
- **响应格式**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "content": [
      {
        "id": 1,
        "pid": "B001",
        "vid": "V001",
        "voltage": 48.5,
        "temperature": 25.0,
        "batteryLevel": 85.0,
        "status": "inUse",
        "lastUpdate": "2024-01-18T10:30:00",
        "createdTime": "2024-01-18T10:00:00"
      }
    ],
    "totalElements": 15,
    "totalPages": 1,
    "size": 20,
    "number": 0
  }
}
```

#### 2.2 获取单个电池信息
- **方法**: `GET`
- **路径**: `/batteries/{pid}`
- **路径参数**: `pid` - 电池编号
- **使用界面**: 电池详情弹窗

#### 2.3 获取电池历史记录
- **方法**: `GET`
- **路径**: `/batteries/{pid}/history`
- **路径参数**: `pid` - 电池编号
- **查询参数**:
  - `startTime` (可选): 开始时间
  - `endTime` (可选): 结束时间
  - `limit` (可选): 返回记录数量
- **使用界面**: 电池详情弹窗

#### 2.4 根据车辆获取电池列表
- **方法**: `GET`
- **路径**: `/batteries/vehicle/{vid}`
- **路径参数**: `vid` - 车辆编号
- **使用界面**: 车辆详情弹窗

#### 2.5 获取电池统计信息
- **方法**: `GET`
- **路径**: `/batteries/statistics`
- **使用界面**: 电池管理页面（统计卡片）
- **响应格式**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "totalBatteries": 15,
    "inUseBatteries": 8,
    "availableBatteries": 6,
    "maintenanceBatteries": 1,
    "lowBatteryList": [],
    "overheatBatteryList": []
  }
}
```

### 3. 报警管理API

#### 3.1 获取报警列表
- **方法**: `GET`
- **路径**: `/alerts`
- **查询参数**:
  - `page` (可选): 页码
  - `size` (可选): 每页大小
  - `resolved` (可选): 是否已解决
- **使用界面**: 报警管理页面
- **响应格式**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "content": [
      {
        "id": 1,
        "type": "battery_low",
        "message": "电池B001电量过低",
        "severity": "warning",
        "timestamp": "2024-01-18T10:30:00",
        "resolved": false
      }
    ],
    "totalElements": 10
  }
}
```

#### 3.2 添加报警
- **方法**: `POST`
- **路径**: `/alerts`
- **请求体**: 报警对象
- **使用界面**: 系统自动触发

#### 3.3 解决报警
- **方法**: `PUT`
- **路径**: `/alerts/{id}/resolve`
- **路径参数**: `id` - 报警ID
- **使用界面**: 报警管理页面

### 4. 电池报警API

#### 4.1 获取报警历史
- **方法**: `GET`
- **路径**: `/alarms`
- **查询参数**:
  - `page` (可选): 页码
  - `size` (可选): 每页大小
  - `type` (可选): 报警类型
- **使用界面**: 报警监控页面

#### 4.2 获取报警统计
- **方法**: `GET`
- **路径**: `/alarms/statistics`
- **查询参数**:
  - `period` (可选): 统计周期（today/week/month）
- **使用界面**: 报警监控页面（统计图表）

#### 4.3 标记报警为已处理
- **方法**: `PUT`
- **路径**: `/alarms/{alarmId}/handle`
- **路径参数**: `alarmId` - 报警ID
- **使用界面**: 报警列表页面

#### 4.4 获取未处理报警数量
- **方法**: `GET`
- **路径**: `/alarms/unhandled/count`
- **使用界面**: 系统概览、报警监控页面

### 5. 系统统计API

#### 5.1 获取系统概览
- **方法**: `GET`
- **路径**: `/dashboard/overview`
- **使用界面**: 系统概览页面
- **响应格式**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "totalVehicles": 50,
    "onlineVehicles": 45,
    "totalBatteries": 100,
    "activeAlerts": 3,
    "systemStatus": "normal"
  }
}
```

### 6. WebSocket实时数据

#### 6.1 WebSocket连接
- **URL**: `ws://localhost:8080/ws`
- **协议**: WebSocket
- **使用界面**: 所有需要实时更新的页面
- **消息格式**:
```json
{
  "type": "vehicle_update",
  "data": {
    "vid": "V001",
    "status": "online",
    "location": {"lat": 39.9042, "lng": 116.4074},
    "batteryLevel": 85,
    "timestamp": "2024-01-18T10:30:00"
  }
}
```

## 错误响应格式

### 通用错误格式
```json
{
  "code": 404,
  "message": "资源不存在",
  "data": null
}
```

### 常见错误码
- `200`: 成功
- `400`: 请求参数错误
- `404`: 资源不存在
- `500`: 服务器内部错误

