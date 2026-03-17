package com.citycharge.service;

import com.citycharge.entity.AlarmRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmService {
    
    public void checkBatteryAlarms(String vid, String pid, Double voltage, Double temperature, Double capacity, Integer x, Integer y) {
        // 检查电池报警条件
    }
    
    public void createAlarmRecord(String vid, String pid, String alarmType, String message, Double voltage, Double temperature, Double capacity, Integer x, Integer y) {
        // 创建报警记录
    }
    
    public List<AlarmRecord> getRecentAlarms() {
        // 获取最近的报警记录
        return null;
    }
    
    public void resolveAlarm(Long alarmId) {
        // 解决报警
    }
}