# 用户端 API 接口设计文档

## 概述

本文档描述了用户端专用的 API 接口设计，与管理端接口分离，确保用户只能访问和操作自己的数据。

## 基础信息

- **基础路径**: `http://localhost:8080`
- **认证方式**: 通过请求头 `X-User-ID` 传递用户 ID
- **数据格式**: JSON

## 认证机制

所有用户端接口都需要在请求头中携带用户 ID：

```http
X-User-ID: 123
```

后端应从以下途径获取用户 ID：
1. 从请求头 `X-User-ID` 获取
2. 从 JWT Token 中解析
3. 从 Session 中获取

---

## 接口列表

### 1. 统计接口

#### 1.1 获取用户综合统计（推荐）

**接口**: `GET /user/stats/summary`

**描述**: 一次性获取用户的所有统计数据（车辆数、电池数等）

**请求头**:
```http
X-User-ID: 123
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "vehicles": 5,
    "batteries": 3
  }
}
```

**字段说明**:
- `vehicles`: 用户拥有的车辆数量
- `batteries`: 用户拥有的电池数量

---

#### 1.2 获取车辆数量

**接口**: `GET /user/stats/vehicles/count`

**描述**: 获取当前用户的车辆数量

**请求头**:
```http
X-User-ID: 123
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "count": 5
  }
}
```

---

#### 1.3 获取电池数量

**接口**: `GET /user/stats/batteries/count`

**描述**: 获取当前用户的电池数量

**请求头**:
```http
X-User-ID: 123
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "count": 3
  }
}
```

---

### 2. 车辆管理接口

#### 2.1 获取车辆列表

**接口**: `GET /user/vehicles`

**描述**: 获取当前用户的所有车辆列表

**请求头**:
```http
X-User-ID: 123
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "我的第一辆车",
      "brand": "特斯拉 Model 3",
      "vin": "LSVAV6C29EN012345",
      "plateNumber": "京 A12345",
      "purchaseDate": "2023-01-15",
      "status": "online",
      "notes": "日常通勤使用",
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

#### 2.2 获取车辆详情

**接口**: `GET /user/vehicles/{vehicleId}`

**描述**: 获取指定车辆的详细信息

**路径参数**:
- `vehicleId`: 车辆 ID

**请求头**:
```http
X-User-ID: 123
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "我的第一辆车",
    "brand": "特斯拉 Model 3",
    "vin": "LSVAV6C29EN012345",
    "plateNumber": "京 A12345",
    "purchaseDate": "2023-01-15",
    "status": "online",
    "notes": "日常通勤使用",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

---

#### 2.3 添加车辆

**接口**: `POST /user/vehicles`

**描述**: 为当前用户添加一辆新车

**请求头**:
```http
X-User-ID: 123
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "我的第一辆车",
  "brand": "特斯拉 Model 3",
  "vin": "LSVAV6C29EN012345",
  "plateNumber": "京 A12345",
  "purchaseDate": "2023-01-15",
  "notes": "日常通勤使用"
}
```

**请求字段说明**:
- `name` (必填): 车辆名称
- `brand` (必填): 品牌型号
- `vin` (必填): 车架号（17 位）
- `plateNumber` (可选): 车牌号
- `purchaseDate` (可选): 购买日期
- `notes` (可选): 备注信息

**响应格式**:
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 1,
    "name": "我的第一辆车",
    "brand": "特斯拉 Model 3",
    "vin": "LSVAV6C29EN012345",
    "plateNumber": "京 A12345",
    "purchaseDate": "2023-01-15",
    "status": "online",
    "notes": "日常通勤使用",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

---

#### 2.4 更新车辆

**接口**: `PUT /user/vehicles/{vehicleId}`

**描述**: 更新指定车辆的信息

**路径参数**:
- `vehicleId`: 车辆 ID

**请求头**:
```http
X-User-ID: 123
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "我的新车名",
  "brand": "特斯拉 Model 3",
  "vin": "LSVAV6C29EN012345",
  "plateNumber": "京 A12345",
  "purchaseDate": "2023-01-15",
  "notes": "更新备注"
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "name": "我的新车名",
    "brand": "特斯拉 Model 3",
    "vin": "LSVAV6C29EN012345",
    "plateNumber": "京 A12345",
    "purchaseDate": "2023-01-15",
    "status": "online",
    "notes": "更新备注",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-02T00:00:00Z"
  }
}
```

---

#### 2.5 删除车辆

**接口**: `DELETE /user/vehicles/{vehicleId}`

**描述**: 删除指定的车辆

**路径参数**:
- `vehicleId`: 车辆 ID

**请求头**:
```http
X-User-ID: 123
```

**响应格式**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

### 3. 电池管理接口

#### 3.1 获取电池列表

**接口**: `GET /user/batteries`

**描述**: 获取当前用户的所有电池列表

**请求头**:
```http
X-User-ID: 123
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "我的第一块电池",
      "model": "CATL-100Ah",
      "code": "BAT20240101001",
      "capacity": 100,
      "purchaseDate": "2023-06-01",
      "status": "online",
      "notes": "日常使用",
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z"
    }
  ]
}
```

**字段说明**:
- `capacity`: 电池容量，单位 Ah（安时）

---

#### 3.2 获取电池详情

**接口**: `GET /user/batteries/{batteryId}`

**描述**: 获取指定电池的详细信息

**路径参数**:
- `batteryId`: 电池 ID

**请求头**:
```http
X-User-ID: 123
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "我的第一块电池",
    "model": "CATL-100Ah",
    "code": "BAT20240101001",
    "capacity": 100,
    "purchaseDate": "2023-06-01",
    "status": "online",
    "notes": "日常使用",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

---

#### 3.3 添加电池

**接口**: `POST /user/batteries`

**描述**: 为当前用户添加一块新电池

**请求头**:
```http
X-User-ID: 123
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "我的第一块电池",
  "model": "CATL-100Ah",
  "code": "BAT20240101001",
  "capacity": 100,
  "purchaseDate": "2023-06-01",
  "notes": "日常使用"
}
```

**请求字段说明**:
- `name` (必填): 电池名称
- `model` (必填): 电池型号
- `code` (必填): 电池编码（唯一标识）
- `capacity` (可选): 电池容量（Ah）
- `purchaseDate` (可选): 购买日期
- `notes` (可选): 备注信息

**响应格式**:
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 1,
    "name": "我的第一块电池",
    "model": "CATL-100Ah",
    "code": "BAT20240101001",
    "capacity": 100,
    "purchaseDate": "2023-06-01",
    "status": "online",
    "notes": "日常使用",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

---

#### 3.4 更新电池

**接口**: `PUT /user/batteries/{batteryId}`

**描述**: 更新指定电池的信息

**路径参数**:
- `batteryId`: 电池 ID

**请求头**:
```http
X-User-ID: 123
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "新电池名",
  "model": "CATL-120Ah",
  "code": "BAT20240101001",
  "capacity": 120,
  "purchaseDate": "2023-06-01",
  "notes": "更新备注"
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "name": "新电池名",
    "model": "CATL-120Ah",
    "code": "BAT20240101001",
    "capacity": 120,
    "purchaseDate": "2023-06-01",
    "status": "online",
    "notes": "更新备注",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-02T00:00:00Z"
  }
}
```

---

#### 3.5 删除电池

**接口**: `DELETE /user/batteries/{batteryId}`

**描述**: 删除指定的电池

**路径参数**:
- `batteryId`: 电池 ID

**请求头**:
```http
X-User-ID: 123
```

**响应格式**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（缺少用户 ID 或 Token） |
| 403 | 禁止访问（用户无权操作该资源） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 安全考虑

1. **用户隔离**: 所有接口必须验证 `X-User-ID`，确保用户只能访问自己的数据
2. **权限验证**: 后端应验证用户是否有权限操作指定资源
3. **数据校验**: 所有输入数据应进行严格校验
4. **SQL 注入防护**: 使用参数化查询，防止 SQL 注入
5. **速率限制**: 对频繁请求进行限制

## 实现建议

### 后端实现示例（伪代码）

```java
// 统计接口示例
@GetMapping("/user/stats/summary")
public ResponseEntity<ApiResponse<StatsDTO>> getSummary(
    @RequestHeader("X-User-ID") Long userId
) {
    StatsDTO stats = userService.getUserStats(userId);
    return ResponseEntity.ok(ApiResponse.success(stats));
}

// 车辆列表示例
@GetMapping("/user/vehicles")
public ResponseEntity<ApiResponse<List<VehicleDTO>>> getVehicles(
    @RequestHeader("X-User-ID") Long userId
) {
    List<VehicleDTO> vehicles = vehicleService.getUserVehicles(userId);
    return ResponseEntity.ok(ApiResponse.success(vehicles));
}

// 添加车辆示例
@PostMapping("/user/vehicles")
public ResponseEntity<ApiResponse<VehicleDTO>> addVehicle(
    @RequestHeader("X-User-ID") Long userId,
    @RequestBody @Valid VehicleCreateDTO dto
) {
    VehicleDTO vehicle = vehicleService.addVehicle(userId, dto);
    return ResponseEntity.ok(ApiResponse.success(vehicle));
}
```

## 前端调用示例

```javascript
// 获取统计信息
const statsRes = await userStatsAPI.getSummary()
if (statsRes.code === 200) {
  vehicleCount.value = statsRes.data?.vehicles || 0
  batteryCount.value = statsRes.data?.batteries || 0
}

// 获取车辆列表
const vehiclesRes = await userVehicleAPI.getVehicles()
if (vehiclesRes.code === 200) {
  vehicles.value = vehiclesRes.data || []
}

// 添加车辆
const addRes = await userVehicleAPI.addVehicle({
  name: '我的车',
  brand: '特斯拉',
  vin: 'LSVAV6C29EN012345'
})
if (addRes.code === 200) {
  // 添加成功
}
```

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2024-01-01 | 初始版本 |
