-- 城市智能电车管理与换电系统 - 测试数据
-- 数据文件用于初始化数据库测试数据

-- 清空现有数据（可选）
-- DELETE FROM alarm_records;
-- DELETE FROM battery_history;
-- DELETE FROM battery;
-- DELETE FROM vehicle;
-- DELETE FROM charging_stations;

-- 插入充电站数据
INSERT INTO charging_stations (id, station_id, station_name, position_x, position_y, available_slots, total_slots, status, created_at, updated_at) VALUES
(1, 'CS001', '中心换电站', 50, 50, 3, 5, 'active', NOW(), NOW()),
(2, 'CS002', '东区换电站', 80, 20, 2, 4, 'active', NOW(), NOW()),
(3, 'CS003', '西区换电站', 20, 80, 4, 6, 'active', NOW(), NOW()),
(4, 'CS004', '南区换电站', 50, 90, 1, 3, 'maintenance', NOW(), NOW()),
(5, 'CS005', '北区换电站', 50, 10, 5, 5, 'active', NOW(), NOW());

-- 插入电池数据
INSERT INTO battery (id, pid, voltage, temperature, remaining_capacity, cycle_count, status, current_vehicle, created_at, updated_at) VALUES
(1, 'B001', 3.8, 25.0, 65.0, 15, 'IN_USE', 'V001', NOW(), NOW()),
(2, 'B002', 3.6, 28.0, 45.0, 23, 'IN_USE', 'V002', NOW(), NOW()),
(3, 'B003', 3.9, 22.0, 80.0, 8, 'IN_USE', 'V003', NOW(), NOW()),
(4, 'B004', 4.0, 24.0, 95.0, 5, 'CHARGING', NULL, NOW(), NOW()),
(5, 'B005', 3.7, 26.0, 55.0, 18, 'AVAILABLE', NULL, NOW(), NOW()),
(6, 'B006', 3.5, 30.0, 35.0, 32, 'MAINTENANCE', NULL, NOW(), NOW());

-- 插入车辆数据
INSERT INTO vehicle (id, vid, pid, voltage, temperature, battery_level, light_status, position_x, position_y, online_status, last_update, created_at, updated_at) VALUES
(1, 'V001', 'B001', 3.8, 25.0, 65.0, 'off', 10, 15, 1, NOW(), NOW(), NOW()),
(2, 'V002', 'B002', 3.6, 28.0, 45.0, 'lowBeam', 25, 30, 1, NOW(), NOW(), NOW()),
(3, 'V003', 'B003', 3.9, 22.0, 80.0, 'highBeam', 40, 45, 1, NOW(), NOW(), NOW()),
(4, 'V004', NULL, NULL, NULL, NULL, 'off', 60, 70, 0, NOW(), NOW(), NOW()),
(5, 'V005', NULL, NULL, NULL, NULL, 'off', 75, 85, 0, NOW(), NOW(), NOW());

-- 插入电池历史数据
INSERT INTO battery_history (id, pid, voltage, temperature, remaining_capacity, record_time, created_at) VALUES
(1, 'B001', 3.8, 25.0, 65.0, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
(2, 'B001', 3.7, 26.0, 64.5, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW()),
(3, 'B002', 3.6, 28.0, 45.0, DATE_SUB(NOW(), INTERVAL 45 MINUTE), NOW()),
(4, 'B002', 3.5, 29.0, 44.0, DATE_SUB(NOW(), INTERVAL 15 MINUTE), NOW()),
(5, 'B003', 3.9, 22.0, 80.0, DATE_SUB(NOW(), INTERVAL 20 MINUTE), NOW());

-- 插入报警记录数据
INSERT INTO alarm_records (id, alarm_id, vehicle_vid, alarm_type, alarm_level, alarm_message, alarm_time, is_resolved, resolved_time, created_at, updated_at) VALUES
(1, 'AL001', 'V002', 'LOW_BATTERY', 'WARNING', '电池电量低于50%', DATE_SUB(NOW(), INTERVAL 2 HOUR), 0, NULL, NOW(), NOW()),
(2, 'AL002', 'V001', 'HIGH_TEMPERATURE', 'ERROR', '电池温度超过30°C', DATE_SUB(NOW(), INTERVAL 1 HOUR), 1, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW()),
(3, 'AL003', 'V003', 'OFFLINE', 'INFO', '车辆长时间离线', DATE_SUB(NOW(), INTERVAL 3 HOUR), 0, NULL, NOW(), NOW());

-- 插入换电记录数据
INSERT INTO battery_swap_records (id, record_id, vehicle_vid, old_pid, new_pid, swap_time, station_id, created_at) VALUES
(1, 'SW001', 'V001', 'B000', 'B001', DATE_SUB(NOW(), INTERVAL 5 DAY), 'CS001', NOW()),
(2, 'SW002', 'V002', 'B000', 'B002', DATE_SUB(NOW(), INTERVAL 3 DAY), 'CS002', NOW()),
(3, 'SW003', 'V003', 'B000', 'B003', DATE_SUB(NOW(), INTERVAL 1 DAY), 'CS003', NOW());

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