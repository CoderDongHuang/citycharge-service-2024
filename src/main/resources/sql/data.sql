-- 清空现有数据（可选，根据需求决定是否启用）
-- DELETE FROM command_log;
-- DELETE FROM alert_log;
-- DELETE FROM station;
-- DELETE FROM alarm_records;
-- DELETE FROM battery_history;
-- DELETE FROM battery;
-- DELETE FROM vehicle;

-- 插入车辆测试数据（10辆车辆）
INSERT IGNORE INTO vehicle (vid, pid, voltage, temperature, battery_level, light_status, position_x, position_y, online_status, last_update) VALUES
('V001', 'B001', 3.8, 25.0, 65.0, 'off', 10, 15, 1, NOW()),
('V002', 'B002', 3.2, 28.0, 25.0, 'lowBeam', 45, 60, 1, NOW()),
('V003', 'B003', 4.0, 22.0, 85.0, 'off', 80, 25, 0, DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
('V004', 'B004', 3.5, 26.0, 45.0, 'highBeam', 120, 80, 1, NOW()),
('V005', 'B005', 3.9, 24.0, 70.0, 'off', 35, 45, 1, NOW()),
('V006', 'B006', 3.1, 29.0, 20.0, 'lowBeam', 90, 30, 1, NOW()),
('V007', 'B007', 4.1, 21.0, 90.0, 'off', 150, 65, 0, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
('V008', 'B008', 3.6, 25.5, 55.0, 'off', 65, 90, 1, NOW()),
('V009', 'B009', 3.3, 27.0, 35.0, 'lowBeam', 110, 40, 1, NOW()),
('V010', 'B010', 3.7, 23.5, 75.0, 'off', 25, 70, 1, NOW());

-- 插入电池测试数据（15块电池）
INSERT IGNORE INTO battery (pid, status, current_vehicle, voltage, temperature, remaining_capacity, health) VALUES
('B001', 'inUse', 'V001', 3.8, 25.0, 65.0, 95.0),
('B002', 'inUse', 'V002', 3.2, 28.0, 25.0, 88.0),
('B003', 'available', NULL, 4.0, 22.0, 85.0, 92.0),
('B004', 'inUse', 'V004', 3.5, 26.0, 45.0, 90.0),
('B005', 'inUse', 'V005', 3.9, 24.0, 70.0, 94.0),
('B006', 'inUse', 'V006', 3.1, 29.0, 20.0, 85.0),
('B007', 'available', NULL, 4.1, 21.0, 90.0, 96.0),
('B008', 'inUse', 'V008', 3.6, 25.5, 55.0, 91.0),
('B009', 'inUse', 'V009', 3.3, 27.0, 35.0, 89.0),
('B010', 'inUse', 'V010', 3.7, 23.5, 75.0, 93.0),
('B011', 'available', NULL, 4.0, 22.5, 88.0, 95.0),
('B012', 'maintenance', NULL, 3.0, 30.0, 15.0, 75.0),
('B013', 'available', NULL, 3.8, 24.0, 80.0, 92.0),
('B014', 'available', NULL, 3.9, 23.0, 85.0, 94.0),
('B015', 'available', NULL, 4.0, 22.0, 90.0, 96.0);

-- 插入电池历史测试数据（每个电池3-5条历史记录）
INSERT IGNORE INTO battery_history (pid, vid, voltage, temperature, capacity, timestamp) VALUES
('B001', 'V001', 3.9, 24.0, 70.0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('B001', 'V001', 3.8, 25.0, 65.0, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
('B002', 'V002', 3.5, 26.0, 45.0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('B002', 'V002', 3.2, 28.0, 25.0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('B003', NULL, 4.0, 22.0, 85.0, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('B004', 'V004', 3.6, 25.0, 50.0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('B004', 'V004', 3.5, 26.0, 45.0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('B005', 'V005', 4.0, 23.0, 75.0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('B005', 'V005', 3.9, 24.0, 70.0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('B006', 'V006', 3.3, 28.0, 25.0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('B006', 'V006', 3.1, 29.0, 20.0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('B007', NULL, 4.1, 21.0, 90.0, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
('B008', 'V008', 3.7, 25.0, 60.0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('B008', 'V008', 3.6, 25.5, 55.0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('B009', 'V009', 3.5, 26.0, 40.0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('B009', 'V009', 3.3, 27.0, 35.0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('B010', 'V010', 3.8, 23.0, 80.0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('B010', 'V010', 3.7, 23.5, 75.0, DATE_SUB(NOW(), INTERVAL 1 HOUR));

-- 插入报警记录测试数据（8条报警记录）
INSERT IGNORE INTO alarm_records (alarm_type, vehicle_vid, battery_pid, alarm_message, alarm_time) VALUES
('lowBattery', 'V002', 'B002', '电池电量过低', NOW()),
('temperature', 'V001', 'B001', '电池温度异常', DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
('lowBattery', 'V006', 'B006', '电池电量严重不足', DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
('temperature', 'V004', 'B004', '电池温度过高', DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
('unreachable', 'V003', 'B003', '车辆连接超时', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
('unreachable', 'V007', 'B007', '车辆长时间离线', DATE_SUB(NOW(), INTERVAL 25 MINUTE)),
('lowBattery', 'V009', 'B009', '电池电量低', DATE_SUB(NOW(), INTERVAL 8 MINUTE)),
('temperature', 'V002', 'B002', '电池温度异常', DATE_SUB(NOW(), INTERVAL 12 MINUTE));

-- 插入报警日志测试数据（12条报警日志）
INSERT IGNORE INTO alert_log (type, vid, pid, message, level, resolved, trigger_value, threshold_value, position_x, position_y, timestamp) VALUES
('lowBattery', 'V002', 'B002', '电池电量低于30%', 'high', 0, 25.0, 30.0, 45, 60, NOW()),
('temperature', 'V001', 'B001', '电池温度超过40°C', 'medium', 1, 41.0, 40.0, 10, 15, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
('lowBattery', 'V006', 'B006', '电池电量低于20%', 'critical', 0, 20.0, 20.0, 90, 30, DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
('temperature', 'V004', 'B004', '电池温度超过45°C', 'high', 0, 46.0, 45.0, 120, 80, DATE_SUB(NOW(), INTERVAL 25 MINUTE)),
('unreachable', 'V003', 'B003', '车辆连接超时10分钟', 'medium', 1, 600.0, 300.0, 80, 25, DATE_SUB(NOW(), INTERVAL 40 MINUTE)),
('unreachable', 'V007', 'B007', '车辆离线超过30分钟', 'high', 0, 1800.0, 900.0, 150, 65, DATE_SUB(NOW(), INTERVAL 35 MINUTE)),
('lowBattery', 'V009', 'B009', '电池电量低于35%', 'medium', 0, 35.0, 35.0, 110, 40, DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
('temperature', 'V002', 'B002', '电池温度异常波动', 'low', 1, 35.0, 30.0, 45, 60, DATE_SUB(NOW(), INTERVAL 50 MINUTE)),
('other', 'V005', 'B005', '车辆异常停车', 'medium', 0, NULL, NULL, 35, 45, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
('lowBattery', 'V008', 'B008', '电池电量低于40%', 'low', 1, 40.0, 40.0, 65, 90, DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
('temperature', 'V010', 'B010', '电池温度过低', 'low', 0, 15.0, 20.0, 25, 70, DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
('unreachable', 'V001', 'B001', '车辆信号不稳定', 'medium', 0, 120.0, 60.0, 10, 15, DATE_SUB(NOW(), INTERVAL 8 MINUTE));

-- 插入指令日志测试数据（10条指令记录）
INSERT IGNORE INTO command_log (command_type, target_vid, command_data, status, sent_by, sent_at, executed_at) VALUES
('lights', 'V001', '{"command": "flash", "duration": 5}', 'executed', 'admin', DATE_SUB(NOW(), INTERVAL 20 MINUTE), DATE_SUB(NOW(), INTERVAL 19 MINUTE)),
('horn', 'V002', '{"command": "beep", "times": 3}', 'sent', 'operator', DATE_SUB(NOW(), INTERVAL 15 MINUTE), NULL),
('lights', 'V004', '{"command": "highBeam", "duration": 10}', 'executed', 'admin', DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 29 MINUTE)),
('horn', 'V006', '{"command": "beep", "times": 2}', 'failed', 'operator', DATE_SUB(NOW(), INTERVAL 10 MINUTE), NULL),
('lights', 'V008', '{"command": "lowBeam", "duration": 8}', 'executed', 'admin', DATE_SUB(NOW(), INTERVAL 25 MINUTE), DATE_SUB(NOW(), INTERVAL 24 MINUTE)),
('other', 'V009', '{"command": "reboot", "force": true}', 'pending', 'system', DATE_SUB(NOW(), INTERVAL 5 MINUTE), NULL),
('lights', 'V010', '{"command": "off", "duration": 0}', 'executed', 'admin', DATE_SUB(NOW(), INTERVAL 35 MINUTE), DATE_SUB(NOW(), INTERVAL 34 MINUTE)),
('horn', 'V005', '{"command": "beep", "times": 1}', 'timeout', 'operator', DATE_SUB(NOW(), INTERVAL 40 MINUTE), NULL),
('lights', 'V002', '{"command": "flash", "duration": 3}', 'executed', 'admin', DATE_SUB(NOW(), INTERVAL 50 MINUTE), DATE_SUB(NOW(), INTERVAL 49 MINUTE)),
('other', 'V007', '{"command": "diagnostic", "level": "full"}', 'sent', 'system', DATE_SUB(NOW(), INTERVAL 2 MINUTE), NULL);

-- 插入换电站测试数据（5个换电站）
INSERT IGNORE INTO station (station_id, name, position_x, position_y, address, battery_capacity, available_batteries, status, operating_hours, contact_phone, manager) VALUES
('ST001', '中心换电站', 50, 50, '市中心人民路100号', 20, 8, 'active', '07:00-23:00', '13800138001', '张经理'),
('ST002', '东区换电站', 120, 30, '东区科技园88号', 15, 5, 'active', '08:00-22:00', '13800138002', '李主管'),
('ST003', '西区换电站', 20, 70, '西区工业园66号', 12, 3, 'maintenance', '09:00-21:00', '13800138003', '王技术'),
('ST004', '南区换电站', 80, 100, '南区商业中心55号', 18, 10, 'active', '07:30-22:30', '13800138004', '赵主任'),
('ST005', '北区换电站', 40, 20, '北区大学城33号', 10, 2, 'closed', '10:00-20:00', '13800138005', '钱站长');