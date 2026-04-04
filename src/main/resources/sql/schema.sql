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