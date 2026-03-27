-- 城市智能电车管理与换电系统 - 测试数据
-- 数据文件用于初始化数据库测试数据

-- 清空现有数据（可选）
-- DELETE FROM alarm_records;
-- DELETE FROM battery_history;
-- DELETE FROM battery;
-- DELETE FROM vehicle;
-- DELETE FROM charging_stations;

-- 插入充电站数据
INSERT INTO charging_stations (id, station_id, position_x, position_y, available_batteries, total_capacity, is_active) VALUES
(1, 'CS001', 50, 50, 3, 5, 1),
(2, 'CS002', 80, 20, 2, 4, 1),
(3, 'CS003', 20, 80, 4, 6, 1),
(4, 'CS004', 50, 90, 1, 3, 0),
(5, 'CS005', 50, 10, 5, 5, 1),
(6, 'CS006', 30, 30, 6, 8, 1),
(7, 'CS007', 70, 70, 4, 6, 1),
(8, 'CS008', 10, 90, 2, 4, 0),
(9, 'CS009', 90, 10, 7, 10, 1),
(10, 'CS010', 40, 60, 3, 5, 1);

-- 插入电池数据
INSERT INTO battery (id, pid, vid, voltage, temperature, battery_level, status, last_update, created_time) VALUES
(1, 'B001', 'V001', 48.5, 25.0, 85.0, 'normal', NOW(), NOW()),
(2, 'B002', 'V002', 46.3, 28.0, 65.0, 'normal', NOW(), NOW()),
(3, 'B003', 'V003', 49.2, 22.0, 92.0, 'normal', NOW(), NOW()),
(4, 'B004', 'V004', 50.1, 24.0, 95.0, 'normal', NOW(), NOW()),
(5, 'B005', 'V005', 45.8, 26.0, 55.0, 'normal', NOW(), NOW()),
(6, 'B006', 'V006', 42.5, 30.0, 35.0, 'low', NOW(), NOW()),
(7, 'B007', 'V007', 51.2, 65.5, 80.0, 'overheat', NOW(), NOW()),
(8, 'B008', 'V008', 2.8, 25.0, 60.0, 'low_voltage', NOW(), NOW()),
(9, 'B009', 'V009', 47.8, 26.5, 78.0, 'normal', NOW(), NOW()),
(10, 'B010', 'V010', 49.5, 23.5, 88.0, 'normal', NOW(), NOW()),
(11, 'B011', 'V011', 48.2, 24.8, 82.0, 'normal', NOW(), NOW()),
(12, 'B012', 'V012', 46.8, 27.2, 45.0, 'low', NOW(), NOW()),
(13, 'B013', 'V013', 50.5, 62.0, 75.0, 'overheat', NOW(), NOW()),
(14, 'B014', 'V014', 3.2, 26.0, 68.0, 'low_voltage', NOW(), NOW());

-- 插入车辆数据
INSERT INTO vehicle (id, vid, pid, voltage, temperature, battery_level, light_status, position_x, position_y, online_status, last_update, created_at, updated_at) VALUES
(1, 'V001', 'B001', 48.5, 25.0, 85.0, 'off', 10, 15, 1, NOW(), NOW(), NOW()),
(2, 'V002', 'B002', 46.3, 28.0, 65.0, 'lowBeam', 25, 30, 1, NOW(), NOW(), NOW()),
(3, 'V003', 'B003', 49.2, 22.0, 92.0, 'highBeam', 40, 45, 1, NOW(), NOW(), NOW()),
(4, 'V004', 'B004', 50.1, 24.0, 95.0, 'off', 60, 70, 0, NOW(), NOW(), NOW()),
(5, 'V005', 'B005', 45.8, 26.0, 55.0, 'off', 75, 85, 0, NOW(), NOW(), NOW()),
(6, 'V006', 'B006', 42.5, 30.0, 35.0, 'off', 20, 25, 1, NOW(), NOW(), NOW()),
(7, 'V007', 'B007', 51.2, 65.5, 80.0, 'lowBeam', 35, 40, 0, NOW(), NOW(), NOW()),
(8, 'V008', 'B008', 2.8, 25.0, 60.0, 'highBeam', 50, 55, 1, NOW(), NOW(), NOW()),
(9, 'V009', 'B009', 47.8, 26.5, 78.0, 'off', 65, 70, 1, NOW(), NOW(), NOW()),
(10, 'V010', 'B010', 49.5, 23.5, 88.0, 'lowBeam', 80, 85, 0, NOW(), NOW(), NOW()),
(11, 'V011', 'B011', 48.2, 24.8, 82.0, 'off', 15, 20, 0, NOW(), NOW(), NOW()),
(12, 'V012', 'B012', 46.8, 27.2, 45.0, 'highBeam', 30, 35, 0, NOW(), NOW(), NOW());

-- 插入电池历史数据
INSERT INTO battery_history (id, pid, voltage, temperature, battery_level, status, timestamp) VALUES
(1, 'B001', 48.5, 25.0, 85.0, 'normal', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 'B001', 48.3, 25.5, 84.5, 'normal', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(3, 'B001', 48.1, 26.0, 84.0, 'normal', DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
(4, 'B002', 46.3, 28.0, 65.0, 'normal', DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
(5, 'B002', 46.1, 28.5, 64.0, 'normal', DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
(6, 'B002', 45.9, 29.0, 63.5, 'normal', DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
(7, 'B003', 49.2, 22.0, 92.0, 'normal', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
(8, 'B003', 49.0, 22.5, 91.5, 'normal', DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
(9, 'B004', 50.1, 24.0, 95.0, 'normal', DATE_SUB(NOW(), INTERVAL 40 MINUTE)),
(10, 'B005', 45.8, 26.0, 55.0, 'normal', DATE_SUB(NOW(), INTERVAL 35 MINUTE)),
(11, 'B006', 42.5, 30.0, 35.0, 'low', DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
(12, 'B006', 42.3, 30.5, 34.5, 'low', DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
(13, 'B007', 51.2, 65.5, 80.0, 'overheat', DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
(14, 'B008', 2.8, 25.0, 60.0, 'low_voltage', DATE_SUB(NOW(), INTERVAL 2 MINUTE)),
(15, 'B009', 47.8, 26.5, 78.0, 'normal', DATE_SUB(NOW(), INTERVAL 25 MINUTE)),
(16, 'B010', 49.5, 23.5, 88.0, 'normal', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
(17, 'B011', 48.2, 24.8, 82.0, 'normal', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(18, 'B012', 46.8, 27.2, 45.0, 'low', DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
(19, 'B013', 50.5, 62.0, 75.0, 'overheat', DATE_SUB(NOW(), INTERVAL 8 MINUTE)),
(20, 'B014', 3.2, 26.0, 68.0, 'low_voltage', DATE_SUB(NOW(), INTERVAL 3 MINUTE));

-- 插入报警记录数据
INSERT INTO alarm_records (id, alarm_type, vehicle_vid, battery_pid, alarm_message, voltage, temperature, capacity_percentage, position_x, position_y, is_resolved, alarm_time, resolve_time, created_at) VALUES
(1, 'lowBattery', 'V002', 'B002', '电池电量过低', 46.3, 28.0, 65.0, 25, 30, 0, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NOW()),
(2, 'temperature', 'V001', 'B001', '电池温度过高', 48.5, 65.5, 85.0, 10, 15, 1, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW()),
(3, 'unreachable', 'V003', 'B003', '车辆无法连接', 49.2, 22.0, 92.0, 40, 45, 0, DATE_SUB(NOW(), INTERVAL 3 HOUR), NULL, NOW()),
(4, 'lowBattery', 'V006', 'B006', '电池电量严重不足', 42.5, 30.0, 35.0, 20, 25, 0, DATE_SUB(NOW(), INTERVAL 4 HOUR), NULL, NOW()),
(5, 'temperature', 'V007', 'B007', '电池温度异常升高', 51.2, 65.5, 80.0, 35, 40, 0, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NOW()),
(6, 'lowVoltage', 'V008', 'B008', '电池电压过低', 2.8, 25.0, 60.0, 50, 55, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
(7, 'unreachable', 'V010', 'B010', '车辆长时间离线', 49.5, 23.5, 88.0, 80, 85, 0, DATE_SUB(NOW(), INTERVAL 6 HOUR), NULL, NOW()),
(8, 'lowBattery', 'V004', 'B004', '电池电量下降', 50.1, 24.0, 95.0, 60, 70, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
(9, 'temperature', 'V009', 'B009', '电池温度偏高', 47.8, 26.5, 78.0, 65, 70, 0, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL, NOW()),
(10, 'lowBattery', 'V010', 'B012', '备用电池电量低', 46.8, 27.2, 45.0, 30, 35, 0, DATE_SUB(NOW(), INTERVAL 7 HOUR), NULL, NOW()),
(11, 'temperature', 'V011', 'B013', '备用电池过热', 50.5, 62.0, 75.0, 40, 45, 0, DATE_SUB(NOW(), INTERVAL 8 HOUR), NULL, NOW()),
(12, 'lowVoltage', 'V012', 'B014', '备用电池电压异常', 3.2, 26.0, 68.0, 55, 60, 0, DATE_SUB(NOW(), INTERVAL 9 HOUR), NULL, NOW());

-- 插入报警日志数据
INSERT INTO alert_log (id, type, vid, pid, message, level, resolved, resolved_by, resolved_at, resolved_note, trigger_value, threshold_value, position_x, position_y, timestamp, created_at, updated_at) VALUES
(1, 'temperature', 'V001', 'B001', '电池温度超过60℃', 'high', 1, 'admin', DATE_SUB(NOW(), INTERVAL 30 MINUTE), '已更换散热系统', 65.5, 60.0, 10, 15, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(2, 'lowBattery', 'V002', 'B002', '电池电量低于20%', 'medium', 0, 'system', DATE_SUB(NOW(), INTERVAL 1 HOUR), '等待处理', 15.0, 20.0, 25, 30, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW()),
(3, 'unreachable', 'V003', 'B003', '车辆无法连接超过3小时', 'high', 0, 'monitor', DATE_SUB(NOW(), INTERVAL 3 HOUR), '持续监控中', 180.0, 180.0, 40, 45, DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW(), NOW()),
(4, 'temperature', 'V004', 'B004', '电池温度异常升高', 'critical', 1, 'operator', DATE_SUB(NOW(), INTERVAL 1 HOUR), '已远程降温', 70.2, 65.0, 60, 70, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW(), NOW()),
(5, 'lowBattery', 'V005', 'B005', '电池电量严重不足', 'high', 0, 'alert', DATE_SUB(NOW(), INTERVAL 4 HOUR), '已发送提醒', 10.5, 15.0, 75, 85, DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW(), NOW()),
(6, 'unreachable', 'V006', 'B006', '车辆信号中断', 'medium', 0, 'system', DATE_SUB(NOW(), INTERVAL 5 HOUR), '自动重连中', 240.0, 180.0, 20, 25, DATE_SUB(NOW(), INTERVAL 6 HOUR), NOW(), NOW()),
(7, 'temperature', 'V007', 'B007', '电池过热警告', 'critical', 0, 'technician', DATE_SUB(NOW(), INTERVAL 1 HOUR), '技术人员已派遣', 75.8, 70.0, 35, 40, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW()),
(8, 'lowBattery', 'V008', 'B008', '电池电量过低', 'high', 1, 'technician', DATE_SUB(NOW(), INTERVAL 2 HOUR), '已安排充电', 8.2, 10.0, 50, 55, DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW(), NOW()),
(9, 'unreachable', 'V009', 'B009', '车辆长时间离线', 'low', 0, 'monitor', DATE_SUB(NOW(), INTERVAL 7 HOUR), '定期检查中', 480.0, 360.0, 65, 70, DATE_SUB(NOW(), INTERVAL 8 HOUR), NOW(), NOW()),
(10, 'other', 'V010', 'B010', '系统异常报警', 'medium', 1, 'system', DATE_SUB(NOW(), INTERVAL 1 HOUR), '系统自动修复', 0.0, 0.0, 80, 85, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW(), NOW());

-- 插入指令日志数据
INSERT INTO command_log (id, command_type, target_vid, command_data, status, sent_by, sent_at, executed_at, response_data, error_message, retry_count, created_at, updated_at) VALUES
(1, 'lights', 'V001', '{"action": "turnOn", "lightType": "lowBeam"}', 'executed', 'operator', DATE_SUB(NOW(), INTERVAL 60 MINUTE), DATE_SUB(NOW(), INTERVAL 55 MINUTE), '{"result": "success", "message": "灯光已开启"}', '无错误', 0, NOW(), NOW()),
(2, 'horn', 'V002', '{"action": "beep", "duration": 3}', 'sent', 'admin', DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 25 MINUTE), '{"result": "pending", "message": "指令已发送"}', '等待响应', 0, NOW(), NOW()),
(3, 'lights', 'V003', '{"action": "turnOff", "lightType": "highBeam"}', 'executed', 'operator', DATE_SUB(NOW(), INTERVAL 120 MINUTE), DATE_SUB(NOW(), INTERVAL 110 MINUTE), '{"result": "success", "message": "远光灯已关闭"}', '无错误', 0, NOW(), NOW()),
(4, 'other', 'V004', '{"action": "reboot", "system": "main"}', 'failed', 'technician', DATE_SUB(NOW(), INTERVAL 180 MINUTE), DATE_SUB(NOW(), INTERVAL 175 MINUTE), '{"result": "failed", "message": "重启失败"}', '系统重启失败，请检查连接', 2, NOW(), NOW()),
(5, 'lights', 'V005', '{"action": "toggle", "lightType": "lowBeam"}', 'executed', 'operator', DATE_SUB(NOW(), INTERVAL 240 MINUTE), DATE_SUB(NOW(), INTERVAL 235 MINUTE), '{"result": "success", "message": "近光灯状态已切换"}', '无错误', 0, NOW(), NOW()),
(6, 'horn', 'V006', '{"action": "beep", "duration": 5}', 'timeout', 'admin', DATE_SUB(NOW(), INTERVAL 300 MINUTE), DATE_SUB(NOW(), INTERVAL 295 MINUTE), '{"result": "timeout", "message": "响应超时"}', '指令超时，车辆未响应', 3, NOW(), NOW()),
(7, 'lights', 'V007', '{"action": "turnOn", "lightType": "highBeam"}', 'pending', 'operator', DATE_SUB(NOW(), INTERVAL 10 MINUTE), DATE_SUB(NOW(), INTERVAL 5 MINUTE), '{"result": "pending", "message": "等待执行"}', '等待执行', 0, NOW(), NOW()),
(8, 'other', 'V008', '{"action": "statusCheck", "components": ["battery", "motor"]}', 'executed', 'technician', DATE_SUB(NOW(), INTERVAL 360 MINUTE), DATE_SUB(NOW(), INTERVAL 350 MINUTE), '{"battery": "normal", "motor": "normal", "voltage": 48.5}', '无错误', 0, NOW(), NOW()),
(9, 'lights', 'V009', '{"action": "turnOff", "lightType": "all"}', 'executed', 'operator', DATE_SUB(NOW(), INTERVAL 420 MINUTE), DATE_SUB(NOW(), INTERVAL 415 MINUTE), '{"result": "success", "message": "所有灯光已关闭"}', '无错误', 0, NOW(), NOW()),
(10, 'horn', 'V010', '{"action": "beep", "duration": 2}', 'sent', 'admin', DATE_SUB(NOW(), INTERVAL 480 MINUTE), DATE_SUB(NOW(), INTERVAL 475 MINUTE), '{"result": "sent", "message": "指令已发送"}', '等待响应', 0, NOW(), NOW());

-- 插入换电站数据
INSERT INTO station (id, station_id, name, position_x, position_y, address, battery_capacity, available_batteries, status, operating_hours, contact_phone, manager, created_at, updated_at) VALUES
(1, 'ST001', '中心换电站', 50, 50, '市中心人民路100号', 20, 15, 'active', '07:00-22:00', '13800138001', '张经理', NOW(), NOW()),
(2, 'ST002', '东区换电站', 80, 20, '东区科技园88号', 15, 10, 'active', '08:00-21:00', '13800138002', '李主管', NOW(), NOW()),
(3, 'ST003', '西区换电站', 20, 80, '西区工业区66号', 25, 20, 'active', '06:00-23:00', '13800138003', '王站长', NOW(), NOW()),
(4, 'ST004', '南区换电站', 50, 90, '南区商业街55号', 18, 12, 'maintenance', '09:00-20:00', '13800138004', '赵技术员', NOW(), NOW()),
(5, 'ST005', '北区换电站', 50, 10, '北区大学城33号', 22, 18, 'active', '07:30-21:30', '13800138005', '刘主任', NOW(), NOW()),
(6, 'ST006', '开发区换电站', 70, 70, '开发区创新园22号', 16, 14, 'active', '08:00-20:00', '13800138006', '陈经理', NOW(), NOW()),
(7, 'ST007', '老城区换电站', 30, 30, '老城区文化街11号', 12, 8, 'closed', '09:00-18:00', '13800138007', '周主管', NOW(), NOW()),
(8, 'ST008', '新区换电站', 90, 40, '新区政务中心44号', 30, 25, 'active', '07:00-22:00', '13800138008', '吴站长', NOW(), NOW()),
(9, 'ST009', '工业园换电站', 40, 60, '工业园A区77号', 20, 16, 'active', '06:30-22:30', '13800138009', '郑技术员', NOW(), NOW()),
(10, 'ST010', '商业中心换电站', 60, 20, '商业中心B座99号', 24, 20, 'active', '08:00-21:00', '13800138010', '钱主任', NOW(), NOW());

-- 数据验证查询（可选）
-- SELECT '车辆数据:' AS info, COUNT(*) AS count FROM vehicle
-- UNION ALL
-- SELECT '电池数据:', COUNT(*) FROM battery
-- UNION ALL
-- SELECT '充电站数据:', COUNT(*) FROM charging_stations
-- UNION ALL
-- SELECT '报警记录:', COUNT(*) FROM alarm_records
-- UNION ALL
-- SELECT '换电记录:', COUNT(*) FROM battery_swap_records;