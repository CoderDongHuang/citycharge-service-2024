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
INSERT INTO station (id, station_id, name, type, position_x, position_y, latitude, longitude, address, battery_capacity, available_batteries, available_slots, rating, total_swaps, status, service_time, operating_hours, contact_phone, manager, facilities, created_at, updated_at) VALUES
(1, 'station_001', 'CitySwap 换电站 - 市中心站', 'battery', 50, 50, 39.9042, 116.4074, '北京市朝阳区建国路88号', 20, 12, 3, 4.8, 1523, 'online', '08:00-22:00', '07:00-22:00', '400-888-8888', '张经理', '["parking", "restroom", "wifi"]', NOW(), NOW()),
(2, 'station_002', 'CitySwap 换电站 - 东区站', 'battery', 80, 20, 39.9142, 116.4174, '北京市朝阳区望京SOHO', 15, 10, 2, 4.6, 856, 'online', '08:00-21:00', '08:00-21:00', '400-888-8889', '李主管', '["parking", "restroom"]', NOW(), NOW()),
(3, 'station_003', 'CitySwap 换电站 - 西区站', 'battery', 20, 80, 39.9242, 116.4274, '北京市海淀区中关村大街', 25, 20, 5, 4.9, 2341, 'online', '06:00-23:00', '06:00-23:00', '400-888-8890', '王站长', '["parking", "restroom", "wifi", "cafe"]', NOW(), NOW()),
(4, 'station_004', 'CitySwap 服务网点 - 南区站', 'service', 50, 90, 39.9342, 116.4374, '北京市丰台区丽泽商务区', 18, 12, 4, 4.5, 678, 'active', '09:00-20:00', '09:00-20:00', '400-888-8891', '赵技术员', '["parking", "restroom", "wifi"]', NOW(), NOW()),
(5, 'station_005', 'CitySwap 换电站 - 北区站', 'battery', 50, 10, 39.9442, 116.4474, '北京市昌平区回龙观', 22, 18, 6, 4.7, 1892, 'online', '07:30-21:30', '07:30-21:30', '400-888-8892', '刘主任', '["parking", "restroom", "wifi", "charging"]', NOW(), NOW()),
(6, 'station_006', 'CitySwap 综合站 - 开发区站', 'all', 70, 70, 39.9542, 116.4574, '北京市亦庄经济开发区', 16, 14, 3, 4.8, 1234, 'online', '08:00-20:00', '08:00-20:00', '400-888-8893', '陈经理', '["parking", "restroom", "wifi", "cafe", "charging"]', NOW(), NOW()),
(7, 'station_007', 'CitySwap 换电站 - 老城区站', 'battery', 30, 30, 39.9642, 116.4674, '北京市东城区王府井', 12, 8, 2, 4.4, 456, 'maintenance', '09:00-18:00', '09:00-18:00', '400-888-8894', '周主管', '["parking", "restroom"]', NOW(), NOW()),
(8, 'station_008', 'CitySwap 换电站 - 新区站', 'battery', 90, 40, 39.9742, 116.4774, '北京市通州区运河商务区', 30, 25, 8, 4.9, 3456, 'online', '07:00-22:00', '07:00-22:00', '400-888-8895', '吴站长', '["parking", "restroom", "wifi", "cafe", "charging", "shop"]', NOW(), NOW()),
(9, 'station_009', 'CitySwap 服务网点 - 工业园站', 'service', 40, 60, 39.9842, 116.4874, '北京市大兴区生物医药基地', 20, 16, 4, 4.6, 789, 'online', '06:30-22:30', '06:30-22:30', '400-888-8896', '郑技术员', '["parking", "restroom", "wifi"]', NOW(), NOW()),
(10, 'station_010', 'CitySwap 换电站 - 商业中心站', 'battery', 60, 20, 39.9942, 116.4974, '北京市西城区金融街', 24, 20, 5, 4.7, 2100, 'online', '08:00-21:00', '08:00-21:00', '400-888-8897', '钱主任', '["parking", "restroom", "wifi", "cafe"]', NOW(), NOW());

-- 插入站点照片数据
INSERT INTO station_photo (id, photo_id, station_id, url, type, description, upload_time, created_at) VALUES
(1, 'photo_001', 1, 'https://cdn.cityswap.com/stations/001/main.jpg', 'main', '换电站外观', NOW(), NOW()),
(2, 'photo_002', 1, 'https://cdn.cityswap.com/stations/001/slot1.jpg', 'slot', '1号换电槽位', NOW(), NOW()),
(3, 'photo_003', 1, 'https://cdn.cityswap.com/stations/001/slot2.jpg', 'slot', '2号换电槽位', NOW(), NOW()),
(4, 'photo_004', 2, 'https://cdn.cityswap.com/stations/002/main.jpg', 'main', '东区站外观', NOW(), NOW()),
(5, 'photo_005', 3, 'https://cdn.cityswap.com/stations/003/main.jpg', 'main', '西区站外观', NOW(), NOW()),
(6, 'photo_006', 3, 'https://cdn.cityswap.com/stations/003/environment.jpg', 'environment', '站点环境', NOW(), NOW()),
(7, 'photo_007', 8, 'https://cdn.cityswap.com/stations/008/main.jpg', 'main', '新区站外观', NOW(), NOW()),
(8, 'photo_008', 8, 'https://cdn.cityswap.com/stations/008/guide.jpg', 'guide', '站点指引图', NOW(), NOW());

-- 插入站点服务数据
INSERT INTO station_service_info (id, station_id, name, price, duration, description, created_at, updated_at) VALUES
(1, 1, '电池更换', 50.00, '5-10分钟', '快速换电服务', NOW(), NOW()),
(2, 1, '电池检测', 0.00, '15分钟', '免费电池健康检测', NOW(), NOW()),
(3, 1, '电池保养', 30.00, '30分钟', '电池清洁与维护', NOW(), NOW()),
(4, 2, '电池更换', 48.00, '5-10分钟', '快速换电服务', NOW(), NOW()),
(5, 3, '电池更换', 52.00, '5-10分钟', '快速换电服务', NOW(), NOW()),
(6, 3, '电池检测', 0.00, '15分钟', '免费电池健康检测', NOW(), NOW()),
(7, 3, '快充服务', 35.00, '30分钟', '快充至80%', NOW(), NOW()),
(8, 8, '电池更换', 55.00, '5-10分钟', '快速换电服务', NOW(), NOW()),
(9, 8, '电池检测', 0.00, '15分钟', '免费电池健康检测', NOW(), NOW()),
(10, 8, '电池保养', 35.00, '30分钟', '电池清洁与维护', NOW(), NOW());

-- 插入站点状态上报数据
INSERT INTO station_status_report (id, station_id, user_id, type, description, status, created_at, updated_at) VALUES
(1, 1, 3, 'battery_low', '站点电池库存不足，仅剩5块', 'pending', NOW(), NOW()),
(2, 2, 3, 'device_error', '2号换电槽位故障', 'processing', NOW(), NOW()),
(3, 5, NULL, 'full_battery', '站点电池已充满，可以换电', 'resolved', NOW(), NOW()),
(4, 7, 1, 'device_error', '设备维护中，暂停服务', 'resolved', NOW(), NOW());

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
('ORD20240115001', 3, 1, '特斯拉 Model 3', 1, 'CitySwap 换电站 - 市中心站', '60kWh - 95%', 89.00, 'completed', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240116001', 3, 1, '特斯拉 Model 3', 2, 'CitySwap 换电站 - 东区站', '60kWh - 88%', 85.00, 'completed', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240117001', 3, 2, '比亚迪汉EV', 3, 'CitySwap 换电站 - 西区站', '76kWh - 92%', 95.00, 'completed', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240118001', 3, 1, '特斯拉 Model 3', 1, 'CitySwap 换电站 - 市中心站', '60kWh - 90%', 89.00, 'processing', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, '正在换电中', NOW(), NOW()),
('ORD20240119001', 3, 1, '特斯拉 Model 3', 5, 'CitySwap 换电站 - 北区站', '60kWh - 85%', 89.00, 'pending', NOW(), NULL, NULL, NULL, '等待支付', NOW(), NOW()),
('ORD20240119002', 3, 2, '比亚迪汉EV', 6, 'CitySwap 综合站 - 开发区站', '76kWh - 80%', 95.00, 'pending', NOW(), NULL, NULL, NULL, '等待支付', NOW(), NOW()),
('ORD20240119003', 3, 1, '特斯拉 Model 3', 8, 'CitySwap 换电站 - 新区站', '60kWh - 75%', 89.00, 'cancelled', DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), '用户取消', NOW(), NOW()),
('ORD20240115002', 1, 3, '蔚来ES6', 1, 'CitySwap 换电站 - 市中心站', '84kWh - 95%', 120.00, 'completed', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240116002', 1, 3, '蔚来ES6', 4, 'CitySwap 服务网点 - 南区站', '84kWh - 88%', 120.00, 'completed', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), NULL, NULL, NOW(), NOW()),
('ORD20240115003', 2, 4, '小鹏P7', 2, 'CitySwap 换电站 - 东区站', '70kWh - 92%', 99.00, 'completed', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), NULL, NULL, NOW(), NOW());