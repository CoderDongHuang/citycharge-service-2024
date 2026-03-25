# 城市智能电车管理与换电系统 - MQTT通信规范

## 1. 系统概述

基于竞赛任务书要求，本系统采用MQTT协议实现车载系统与后端服务之间的实时双向通信。系统包含以下实体：
- **电车（Vehicle）**：搭载电池，具备传感器和控制器
- **电池（Battery）**：为电车供电，具备电压、温度等参数
- **后端服务（Backend Service）**：数据处理和业务逻辑中心

## 2. MQTT Broker配置

- **Broker地址**: `tcp://localhost:1883` (默认配置)
- **QoS级别**: 1 (确保消息送达)
- **保留消息**: 车辆状态消息使用retain=true
- **遗嘱消息**: 车辆离线时发送离线状态

## 3. MQTT主题结构设计

### 3.1 车辆状态上报主题
```
vehicle/{vid}/status
```
- `{vid}`: 车辆唯一编号（如：V001）
- **用途**: 车辆定期上报状态信息
- **发布者**: 车载系统
- **订阅者**: 后端服务

### 3.2 车辆控制指令主题
```
vehicle/{vid}/control
```
- **用途**: 后端服务向车辆发送控制指令
- **发布者**: 后端服务
- **订阅者**: 车载系统

### 3.3 系统报警主题
```
vehicle/{vid}/alarm
```
- **用途**: 车辆上报报警信息
- **发布者**: 车载系统
- **订阅者**: 后端服务

### 3.4 车辆在线状态主题
```
vehicle/{vid}/online
```
- **用途**: 车辆上线/下线状态通知
- **发布者**: 车载系统
- **订阅者**: 后端服务

### 3.5 系统广播主题
```
system/broadcast
```
- **用途**: 系统级广播消息（如充电站推荐）
- **发布者**: 后端服务
- **订阅者**: 所有车载系统

## 4. MQTT消息格式规范

### 4.1 车辆状态上报消息
**主题**: `vehicle/{vid}/status`（完成/2026-3-22）

**消息格式**:
```json
{
  "timestamp": "2024-01-18T10:30:00Z",
  "vid": "V001",
  "pid": "B001",
  "voltage": 3.8,
  "temperature": 25.0,
  "battery_level": 65.0,
  "light_status": "off",
  "position_x": 10,
  "position_y": 15,
  "online_status": true
}
```

**字段说明**（对应vehicle表字段）：
- `timestamp`: ISO 8601时间格式（对应last_update字段）
- `vid`: 车辆编号（对应vid字段）
- `pid`: 电池编号（对应pid字段）
- `voltage`: 电池电压（V）（对应voltage字段）
- `temperature`: 电池温度（°C）（对应temperature字段）
- `battery_level`: 电池剩余电量百分比（%）（对应battery_level字段）
- `light_status`: 灯光状态（off/lowBeam/highBeam）（对应light_status字段）
- `position_x`: 车辆X坐标（对应position_x字段）
- `position_y`: 车辆Y坐标（对应position_y字段）
- `online_status`: 在线状态（对应online_status字段）

### 4.2 车辆控制指令消息
**主题**: `vehicle/{vid}/control`

**灯光控制消息格式**:（完成/2026-3-23）
```json
{
  "timestamp": "2024-01-18T10:35:00Z",
  "command": "flashLights",
  "params": {
    "pattern": "continuous",
    "duration": 3000
  }
}
```

**喇叭控制消息格式**:（完成/2026-3-24）
```json
{
  "timestamp": "2024-01-18T10:36:00Z",
  "command": "beepHorn",
  "params": {
    "pattern": "triple",
    "interval": 500
  }
}
```

**位置设置消息格式**:（完成/2026-3-24）
```json
{
  "timestamp": "2024-01-18T10:37:00Z",
  "command": "setPosition",
  "params": {
    "x": 20,
    "y": 25
  }
}
```

### 4.3 电池报警消息
**主题**: `vehicle/{vid}/alarm`

**温度异常报警**（对应alert_log表字段）：
```json
{
  "timestamp": "2024-01-18T10:38:00Z",
  "type": "temperature",
  "vid": "V001",
  "pid": "B001",
  "level": "high",
  "trigger_value": 62.0,
  "threshold_value": 60.0,
  "position_x": 10,
  "position_y": 15,
  "message": "电池温度异常"
}
```

**电量过低报警**（对应alert_log表字段）：
```json
{
  "timestamp": "2024-01-18T10:39:00Z",
  "type": "lowBattery",
  "vid": "V001",
  "pid": "B001",
  "level": "critical",
  "trigger_value": 18.0,
  "threshold_value": 20.0,
  "position_x": 10,
  "position_y": 15,
  "message": "电池电量过低"
}
```

**无法到达换电站报警**（对应alert_log表字段）：
```json
{
  "timestamp": "2024-01-18T10:40:00Z",
  "type": "unreachable",
  "vid": "V001",
  "pid": "B001",
  "level": "critical",
  "trigger_value": 15.0,
  "threshold_value": 25.0,
  "position_x": 10,
  "position_y": 15,
  "message": "无法到达最近换电站"
}
```

### 4.4 车辆在线状态消息（完成/2026-3-25）
**主题**: `vehicle/{vid}/online`

**上线消息**:
```json
{
  "timestamp": "2024-01-18T10:41:00Z",
  "vid": "V001",
  "status": "online",
  "ip": "192.168.1.100"
}
```

**下线消息**:
```json
{
  "timestamp": "2024-01-18T10:42:00Z",
  "vid": "V001",
  "status": "offline",
  "reason": "connection_lost"
}
```

### 4.5 系统广播消息
**主题**: `system/broadcast`

**充电站推荐消息**:
```json
{
  "timestamp": "2024-01-18T10:43:00Z",
  "type": "chargingRecommendation",
  "targetVid": "V001",
  "message": "电量不足30%，建议前往最近换电站",
  "nearestStation": {
    "x": 45,
    "y": 60,
    "distance": 25
  },
  "route": [
    {"x": 10, "y": 15},
    {"x": 15, "y": 15},
    {"x": 20, "y": 20},
    {"x": 45, "y": 60}
  ]
}
```

## 5. 通信频率和时序要求

### 5.1 车辆状态上报
- **频率**: 每5秒上报一次
- **QoS**: 1
- **Retain**: true（保留最新状态）

### 5.2 报警消息
- **触发条件**: 实时触发
- **QoS**: 2（确保送达）
- **Retain**: false

### 5.3 控制指令
- **响应时间**: 3秒内响应
- **QoS**: 2（确保送达）
- **Retain**: false

### 5.4 心跳检测
- **频率**: 每30秒发送一次心跳
- **主题**: `vehicle/{vid}/heartbeat`
- **超时时间**: 60秒无心跳视为离线

## 6. 错误处理和重试机制

### 6.1 连接失败处理
- 车载系统检测到MQTT连接断开时，自动重连
- 重连间隔：5秒、10秒、20秒（指数退避）
- 最大重试次数：10次

### 6.2 消息发送失败处理
- QoS 1/2消息发送失败时，自动重发
- 最大重发次数：3次
- 重发间隔：2秒

### 6.3 消息格式错误处理
- 接收方检测到消息格式错误时，丢弃消息
- 记录错误日志
- 发送错误响应到 `vehicle/{vid}/error` 主题

## 7. 安全考虑

### 7.1 认证机制
- 使用用户名/密码认证
- 每个车辆使用独立的客户端ID
- 密码定期更换

### 7.2 主题权限控制
- 车辆只能发布到自己的主题
- 车辆只能订阅控制主题
- 后端服务有全部权限

### 7.3 数据加密
- 敏感数据在传输前加密
- 使用TLS/SSL加密通信通道

## 8. 硬件接口规范

### 8.1 传感器数据采集
```json
{
  "电压传感器": {
    "范围": "0-5V",
    "精度": "0.01V",
    "采样频率": "1Hz"
  },
  "温度传感器": {
    "范围": "-40°C~125°C",
    "精度": "0.1°C",
    "采样频率": "1Hz"
  },
  "光线传感器": {
    "范围": "0-1000lux",
    "采样频率": "2Hz"
  },
  "RFID读卡器": {
    "读取距离": "5-10cm",
    "响应时间": "<100ms"
  }
}
```

### 8.2 执行器控制接口
```json
{
  "车灯控制": {
    "近光灯": "GPIO控制",
    "远光灯": "GPIO控制",
    "响应时间": "<100ms"
  },
  "喇叭控制": {
    "控制方式": "PWM",
    "频率范围": "100-5000Hz",
    "响应时间": "<50ms"
  },
  "显示模块": {
    "类型": "LCD/OLED",
    "分辨率": "128x64",
    "刷新率": "1Hz"
  }
}
```

## 9. 测试和验证

### 9.1 功能测试用例
1. **车辆状态上报测试**
   - 验证数据格式正确性
   - 验证上报频率符合要求
   - 验证数据准确性

2. **控制指令测试**
   - 验证指令响应时间<3秒
   - 验证执行器正确动作
   - 验证错误处理机制

3. **报警功能测试**
   - 验证报警触发条件
   - 验证报警消息格式
   - 验证报警处理流程

### 9.2 性能测试指标
- **消息延迟**: <100ms
- **系统吞吐量**: >1000消息/秒
- **连接稳定性**: 99.9%可用性
- **内存使用**: <100MB

## 10. 部署和运维

### 10.1 MQTT Broker部署
- 使用EMQX或Mosquitto
- 配置持久化存储
- 设置监控和告警

### 10.2 车载系统配置
- 配置MQTT连接参数
- 设置传感器采样频率
- 配置执行器控制参数

### 10.3 监控和日志
- 实时监控MQTT连接状态
- 记录关键操作日志
- 设置性能指标监控

---

## 附录A：消息示例代码

### Python MQTT客户端示例
```python
import paho.mqtt.client as mqtt
import json
import time

def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code {rc}")
    client.subscribe(f"vehicle/{vid}/control")

def on_message(client, userdata, msg):
    payload = json.loads(msg.payload.decode())
    if payload["command"] == "flashLights":
        # 控制车灯闪烁
        control_lights(payload["params"])
    elif payload["command"] == "beepHorn":
        # 控制喇叭鸣笛
        control_horn(payload["params"])

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect("localhost", 1883, 60)

# 定期上报状态
while True:
    status_data = get_vehicle_status()
    client.publish(f"vehicle/{vid}/status", 
                   json.dumps(status_data), 
                   qos=1, retain=True)
    time.sleep(5)
```

### Java MQTT客户端示例
```java
import org.eclipse.paho.client.mqttv3.*;

MqttClient client = new MqttClient("tcp://localhost:1883", "vehicle_" + vid);
MqttConnectOptions options = new MqttConnectOptions();
options.setCleanSession(true);
client.connect(options);

// 订阅控制主题
client.subscribe("vehicle/" + vid + "/control", 1);

// 发布状态消息
MqttMessage statusMessage = new MqttMessage();
statusMessage.setPayload(statusJson.getBytes());
statusMessage.setQos(1);
statusMessage.setRetained(true);
client.publish("vehicle/" + vid + "/status", statusMessage);
```

## 11. 数据库字段映射表

### 11.1 车辆状态消息字段映射
| MQTT消息字段 | 数据库表字段 | 数据类型 | 说明 |
|-------------|-------------|----------|------|
| `vid` | `vehicle.vid` | VARCHAR(50) | 车辆编号 |
| `pid` | `vehicle.pid` | VARCHAR(50) | 电池编号 |
| `voltage` | `vehicle.voltage` | DECIMAL(5,2) | 电池电压 |
| `temperature` | `vehicle.temperature` | DECIMAL(5,2) | 电池温度 |
| `battery_level` | `vehicle.battery_level` | DECIMAL(5,2) | 剩余电量 |
| `light_status` | `vehicle.light_status` | ENUM | 灯光状态 |
| `position_x` | `vehicle.position_x` | INT | X坐标 |
| `position_y` | `vehicle.position_y` | INT | Y坐标 |
| `online_status` | `vehicle.online_status` | TINYINT(1) | 在线状态 |
| `timestamp` | `vehicle.last_update` | DATETIME | 更新时间 |

### 11.2 报警消息字段映射
| MQTT消息字段 | 数据库表字段 | 数据类型 | 说明 |
|-------------|-------------|----------|------|
| `type` | `alert_log.type` | ENUM | 报警类型 |
| `vid` | `alert_log.vid` | VARCHAR(50) | 车辆编号 |
| `pid` | `alert_log.pid` | VARCHAR(50) | 电池编号 |
| `level` | `alert_log.level` | ENUM | 报警级别 |
| `trigger_value` | `alert_log.trigger_value` | DECIMAL(10,4) | 触发值 |
| `threshold_value` | `alert_log.threshold_value` | DECIMAL(10,4) | 阈值 |
| `position_x` | `alert_log.position_x` | INT | X坐标 |
| `position_y` | `alert_log.position_y` | INT | Y坐标 |
| `message` | `alert_log.message` | VARCHAR(255) | 报警信息 |
| `timestamp` | `alert_log.timestamp` | DATETIME | 报警时间 |

### 11.3 控制指令消息字段映射
| MQTT消息字段 | 数据库表字段 | 数据类型 | 说明 |
|-------------|-------------|----------|------|
| `command` | `command_log.command_type` | ENUM | 指令类型 |
| `target_vid` | `command_log.target_vid` | VARCHAR(50) | 目标车辆 |
| `params` | `command_log.command_data` | JSON | 指令参数 |
| `timestamp` | `command_log.sent_at` | DATETIME | 发送时间 |

### 11.4 电池历史记录字段映射
| MQTT消息字段 | 数据库表字段 | 数据类型 | 说明 |
|-------------|-------------|----------|------|
| `pid` | `battery_history.pid` | VARCHAR(50) | 电池编号 |
| `vid` | `battery_history.vid` | VARCHAR(50) | 车辆编号 |
| `voltage` | `battery_history.voltage` | DECIMAL(5,2) | 电压记录 |
| `temperature` | `battery_history.temperature` | DECIMAL(5,2) | 温度记录 |
| `capacity` | `battery_history.capacity` | DECIMAL(5,2) | 电量记录 |
| `timestamp` | `battery_history.timestamp` | DATETIME | 记录时间 |

### 11.5 电池基本信息字段映射
| MQTT消息字段 | 数据库表字段 | 数据类型 | 说明 |
|-------------|-------------|----------|------|
| `pid` | `battery.pid` | VARCHAR(50) | 电池编号 |
| `status` | `battery.status` | ENUM | 电池状态 |
| `current_vehicle` | `battery.current_vehicle` | VARCHAR(50) | 当前车辆 |
| `voltage` | `battery.voltage` | DECIMAL(5,2) | 当前电压 |
| `temperature` | `battery.temperature` | DECIMAL(5,2) | 当前温度 |
| `remaining_capacity` | `battery.remaining_capacity` | DECIMAL(5,2) | 剩余电量 |
| `health` | `battery.health` | DECIMAL(5,2) | 健康状态 |

## 12. 数据一致性保证

### 12.1 字段命名规范
- 所有字段名使用蛇形命名法（snake_case）
- 与数据库表字段名保持一致
- 避免使用缩写，确保语义清晰

### 12.2 数据类型映射
- **数值类型**: DECIMAL对应浮点数，INT对应整数
- **字符串类型**: VARCHAR对应字符串
- **枚举类型**: ENUM对应预定义值
- **时间类型**: DATETIME对应ISO 8601格式

### 12.3 数据验证规则
- 电压范围：0-5V，精度0.01V
- 温度范围：-40°C~125°C，精度0.1°C
- 电量范围：0-100%，精度0.01%
- 坐标范围：1-100

---

这个MQTT通信规范文档涵盖了竞赛任务书中的所有通信需求，包括数据格式、主题结构、时序要求和安全考虑。您可以根据这个规范来编写硬件和后端的MQTT通信代码。