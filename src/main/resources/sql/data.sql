-- 城市智能电车管理与换电系统 - 测试数据
-- 数据文件用于初始化数据库测试数据

-- 清空现有数据（可选）
-- DELETE FROM alarm_records;
-- DELETE FROM battery_history;
-- DELETE FROM battery;
-- DELETE FROM vehicle;
-- DELETE FROM charging_stations;

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

-- 插入用户数据（密码为 123456 的加密值）
INSERT INTO user (id, username, password, email, phone, role, avatar, status, notifications, dark_mode, last_login, created_at, updated_at) VALUES
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@citycharge.com', '13800138001', 'admin', NULL, 1, 1, 0, NULL, NOW(), NOW()),
(2, 'operator', 'e10adc3949ba59abbe56e057f20f883e', 'operator@citycharge.com', '13800138002', 'operator', NULL, 1, 1, 0, NULL, NOW(), NOW()),
(3, 'user', 'e10adc3949ba59abbe56e057f20f883e', 'user@citycharge.com', '13800138003', 'user', NULL, 1, 1, 0, NULL, NOW(), NOW());

-- 插入用户车辆数据
INSERT INTO user_vehicle (id, user_id, name, brand, vin, plate_number, purchase_date, notes, status, battery_level, latitude, longitude, last_online_time, created_at, updated_at) VALUES
(1, 3, '特斯拉 Model 3', 'Tesla', 'LSVNV2182N1234567', '京A12345', '2023-06-15', '日常通勤用车', 'online', 85, 39.9042, 116.4074, NOW(), NOW(), NOW()),
(2, 3, '比亚迪汉EV', 'BYD', 'LGXCE1CB2N7654321', '京B67890', '2023-08-20', '家用车', 'offline', 60, 39.9142, 116.4174, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), NOW()),
(3, 1, '蔚来ES6', 'NIO', 'LSGAJ8E52N1111111', '京C11111', '2023-03-10', '公司用车', 'online', 92, 39.9242, 116.4274, NOW(), NOW(), NOW()),
(4, 2, '小鹏P7', 'XPeng', 'LFPCH1AA2N2222222', '京D22222', '2023-09-01', '测试车辆', 'offline', 45, 39.9342, 116.4374, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NOW());

-- 插入用户电池数据
INSERT INTO user_battery (id, user_id, name, model, code, capacity, purchase_date, notes, status, current_level, voltage, temperature, cycle_count, last_charge_time, created_at, updated_at) VALUES
(1, 3, '主电池包', 'CATL-60kWh', 'BAT20240001', 60, '2024-01-15', '三元锂电池', 'online', 85, 380.5, 25.5, 120, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), NOW()),
(2, 3, '备用电池包', 'BYD-55kWh', 'BAT20240002', 55, '2024-02-20', '磷酸铁锂电池', 'offline', 45, 370.2, 22.0, 85, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), NOW()),
(3, 1, '主电池包', 'CATL-84kWh', 'BAT20240003', 84, '2024-03-01', '三元锂电池', 'charging', 92, 395.0, 28.0, 50, NOW(), NOW(), NOW()),
(4, 2, '主电池包', 'CALB-70kWh', 'BAT20240004', 70, '2023-12-10', '磷酸铁锂电池', 'online', 78, 375.8, 24.5, 200, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), NOW());

-- 插入用户订单数据
INSERT INTO user_order (id, user_id, vehicle_id, vehicle_name, station_id, station_name, battery_info, amount, status, create_time, pay_time, complete_time, cancel_time, notes, created_at, updated_at) VALUES
('ORD20240115001', 3, 1, '特斯拉 Model 3', 1, '中心换电站', '60kWh - 95%', 89.00, 'completed', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240116001', 3, 1, '特斯拉 Model 3', 2, '东区换电站', '60kWh - 88%', 85.00, 'completed', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240117001', 3, 2, '比亚迪汉EV', 3, '西区换电站', '76kWh - 92%', 95.00, 'completed', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240118001', 3, 1, '特斯拉 Model 3', 1, '中心换电站', '60kWh - 90%', 89.00, 'processing', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, '正在换电中', NOW(), NOW()),
('ORD20240119001', 3, 1, '特斯拉 Model 3', 5, '北区换电站', '60kWh - 85%', 89.00, 'pending', NOW(), NULL, NULL, NULL, '等待支付', NOW(), NOW()),
('ORD20240119002', 3, 2, '比亚迪汉EV', 6, '开发区换电站', '76kWh - 80%', 95.00, 'pending', NOW(), NULL, NULL, NULL, '等待支付', NOW(), NOW()),
('ORD20240119003', 3, 1, '特斯拉 Model 3', 8, '新区换电站', '60kWh - 75%', 89.00, 'cancelled', DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), '用户取消', NOW(), NOW()),
('ORD20240115002', 1, 3, '蔚来ES6', 1, '中心换电站', '84kWh - 95%', 120.00, 'completed', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240116002', 1, 3, '蔚来ES6', 4, '南区换电站', '84kWh - 88%', 120.00, 'completed', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240115003', 2, 4, '小鹏P7', 2, '东区换电站', '70kWh - 92%', 99.00, 'completed', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), NULL, NULL, NOW(), NOW());