-- 创建数据库
CREATE DATABASE IF NOT EXISTS citycharge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE citycharge;

-- 车辆表 - 存储电车基本信息
CREATE TABLE IF NOT EXISTS vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    vid VARCHAR(50) NOT NULL UNIQUE COMMENT '车辆唯一编号',
    pid VARCHAR(50) COMMENT '当前电池编号',
    voltage DECIMAL(5,2) COMMENT '电池电压(V)',
    temperature DECIMAL(5,2) COMMENT '电池温度(°C)',
    battery_level DECIMAL(5,2) COMMENT '电池剩余电量百分比(%)',
    light_status ENUM('off', 'lowBeam', 'highBeam') DEFAULT 'off' COMMENT '灯光状态',
    position_x INT COMMENT '车辆X坐标',
    position_y INT COMMENT '车辆Y坐标',
    online_status TINYINT(1) DEFAULT 0 COMMENT '在线状态(0:离线,1:在线)',
    last_update DATETIME COMMENT '最后更新时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_vid (vid),
    INDEX idx_online (online_status),
    INDEX idx_position (position_x, position_y)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';

-- 电池表 - 存储电池基本信息
CREATE TABLE IF NOT EXISTS battery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    pid VARCHAR(50) NOT NULL UNIQUE COMMENT '电池唯一编号',
    vid VARCHAR(50) COMMENT '车辆编号',
    voltage DECIMAL(5,2) COMMENT '当前电压(V)',
    temperature DECIMAL(5,2) COMMENT '当前温度(°C)',
    battery_level DECIMAL(5,2) COMMENT '电池电量百分比(%)',
    status VARCHAR(20) DEFAULT 'normal' COMMENT '电池状态(normal/low/overheat/low_voltage)',
    last_update DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_pid (pid),
    INDEX idx_vid (vid),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电池信息表';

-- 报警日志表 - 存储系统报警信息（新增表）
CREATE TABLE IF NOT EXISTS alert_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type ENUM('temperature', 'lowBattery', 'unreachable', 'other') NOT NULL COMMENT '报警类型',
    vid VARCHAR(50) COMMENT '关联车辆编号',
    pid VARCHAR(50) COMMENT '关联电池编号',
    message VARCHAR(255) NOT NULL COMMENT '报警信息',
    level ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium' COMMENT '报警级别',
    resolved TINYINT(1) DEFAULT 0 COMMENT '是否已解决(0:未解决,1:已解决)',
    resolved_by VARCHAR(50) COMMENT '解决人',
    resolved_at DATETIME COMMENT '解决时间',
    resolved_note TEXT COMMENT '解决说明',
    trigger_value DECIMAL(10,4) COMMENT '触发值(如温度值、电量值等)',
    threshold_value DECIMAL(10,4) COMMENT '阈值',
    position_x INT COMMENT '报警位置X坐标',
    position_y INT COMMENT '报警位置Y坐标',
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报警时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_vid (vid),
    INDEX idx_pid (pid),
    INDEX idx_resolved (resolved),
    INDEX idx_timestamp (timestamp),
    INDEX idx_level (level),
    FOREIGN KEY (vid) REFERENCES vehicle(vid) ON DELETE SET NULL,
    FOREIGN KEY (pid) REFERENCES battery(pid) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警日志表';

-- 指令日志表 - 存储远程控制指令记录（新增表）
CREATE TABLE IF NOT EXISTS command_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    command_type ENUM('lights', 'horn', 'other') NOT NULL COMMENT '指令类型',
    target_vid VARCHAR(50) NOT NULL COMMENT '目标车辆编号',
    command_data JSON COMMENT '指令数据',
    status ENUM('pending', 'sent', 'executed', 'failed', 'timeout') DEFAULT 'pending' COMMENT '指令状态',
    sent_by VARCHAR(50) COMMENT '发送人',
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    executed_at DATETIME COMMENT '执行时间',
    response_data JSON COMMENT '响应数据',
    error_message TEXT COMMENT '错误信息',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_command_type (command_type),
    INDEX idx_target_vid (target_vid),
    INDEX idx_status (status),
    INDEX idx_sent_at (sent_at),
    FOREIGN KEY (target_vid) REFERENCES vehicle(vid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指令日志表';

-- 换电站表 - 存储换电站信息（新增表）
CREATE TABLE IF NOT EXISTS station (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    station_id VARCHAR(50) NOT NULL UNIQUE COMMENT '换电站编号',
    name VARCHAR(100) NOT NULL COMMENT '换电站名称',
    position_x INT NOT NULL COMMENT 'X坐标',
    position_y INT NOT NULL COMMENT 'Y坐标',
    address VARCHAR(255) COMMENT '详细地址',
    battery_capacity INT DEFAULT 10 COMMENT '电池容量',
    available_batteries INT DEFAULT 0 COMMENT '可用电池数量',
    status ENUM('active', 'maintenance', 'closed') DEFAULT 'active' COMMENT '状态',
    operating_hours VARCHAR(100) COMMENT '营业时间',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    manager VARCHAR(50) COMMENT '负责人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_station_id (station_id),
    INDEX idx_position (position_x, position_y),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换电站信息表';

-- 用户表 - 存储用户账号信息
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) COMMENT '密码（MD5加密，GitHub登录可为空）',
    email VARCHAR(100) COMMENT '邮箱',
    github_id VARCHAR(50) COMMENT 'GitHub用户ID',
    provider VARCHAR(20) DEFAULT 'local' COMMENT '登录方式（local/github）',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色（admin/operator/user）',
    avatar VARCHAR(255) COMMENT '头像URL',
    status INT DEFAULT 1 COMMENT '状态（1:正常 0:禁用）',
    last_login DATETIME COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_github_id (github_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 用户车辆表 - 存储用户绑定的车辆信息
CREATE TABLE IF NOT EXISTS user_vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '车辆名称',
    brand VARCHAR(100) NOT NULL COMMENT '品牌',
    vin VARCHAR(17) NOT NULL UNIQUE COMMENT '车辆识别码(VIN)',
    plate_number VARCHAR(20) COMMENT '车牌号',
    purchase_date DATE COMMENT '购买日期',
    notes VARCHAR(500) COMMENT '备注',
    status ENUM('online', 'offline') DEFAULT 'offline' COMMENT '状态(online/offline)',
    battery_level INT COMMENT '电池电量(%)',
    latitude DECIMAL(10,7) COMMENT '纬度',
    longitude DECIMAL(10,7) COMMENT '经度',
    last_online_time DATETIME COMMENT '最后在线时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_vin (vin),
    INDEX idx_plate_number (plate_number),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户车辆表';

-- 用户电池表 - 存储用户绑定的电池信息
CREATE TABLE IF NOT EXISTS user_battery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '电池名称',
    model VARCHAR(100) NOT NULL COMMENT '电池型号',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '电池编码',
    capacity INT COMMENT '容量(kWh)',
    purchase_date DATE COMMENT '购买日期',
    notes VARCHAR(500) COMMENT '备注',
    status ENUM('online', 'offline', 'charging') DEFAULT 'offline' COMMENT '状态(online/offline/charging)',
    current_level INT COMMENT '当前电量(%)',
    voltage DOUBLE COMMENT '电压(V)',
    temperature DOUBLE COMMENT '温度(℃)',
    cycle_count INT DEFAULT 0 COMMENT '循环次数',
    last_charge_time DATETIME COMMENT '最后充电时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_code (code),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户电池表';

-- 用户订单表 - 存储用户换电订单信息
CREATE TABLE IF NOT EXISTS user_order (
    id VARCHAR(50) PRIMARY KEY COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    vehicle_id BIGINT COMMENT '车辆ID',
    vehicle_name VARCHAR(100) COMMENT '车辆名称',
    station_id BIGINT COMMENT '换电站ID',
    station_name VARCHAR(100) COMMENT '换电站名称',
    battery_info VARCHAR(100) COMMENT '电池信息',
    amount DECIMAL(10,2) COMMENT '订单金额',
    status ENUM('pending', 'processing', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time DATETIME COMMENT '支付时间',
    complete_time DATETIME COMMENT '完成时间',
    cancel_time DATETIME COMMENT '取消时间',
    notes VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES user_vehicle(id) ON DELETE SET NULL,
    FOREIGN KEY (station_id) REFERENCES station(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户订单表';