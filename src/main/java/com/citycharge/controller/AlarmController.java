package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.entity.AlarmRecord;
import com.citycharge.service.AlarmService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmController {
    
    private final AlarmService alarmService;
    
    /**
     * 4.1 获取报警历史
     */
    @GetMapping
    public ApiResponse<Page<AlarmRecord>> getAlarms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<AlarmRecord> alarmList;
        
        if (type != null && !type.isEmpty()) {
            alarmList = alarmService.findByAlarmType(type);
        } else {
            alarmList = alarmService.findAll();
        }
        
        Page<AlarmRecord> alarms = new org.springframework.data.domain.PageImpl<>(alarmList, pageable, alarmList.size());
        return ApiResponse.success(alarms);
    }
    
    /**
     * 4.2 获取报警统计
     */
    @GetMapping("/statistics")
    public ApiResponse<AlarmStatistics> getAlarmStatistics(@RequestParam(required = false) String period) {
        AlarmStatistics statistics = new AlarmStatistics();
        
        // 根据时间段过滤
        final LocalDateTime startTime;
        if ("today".equals(period)) {
            startTime = LocalDate.now().atStartOfDay();
        } else if ("week".equals(period)) {
            startTime = LocalDate.now().minus(7, ChronoUnit.DAYS).atStartOfDay();
        } else if ("month".equals(period)) {
            startTime = LocalDate.now().minus(30, ChronoUnit.DAYS).atStartOfDay();
        } else {
            startTime = null;
        }
        
        // 获取统计数据
        List<AlarmRecord> allAlarms = alarmService.findAll();
        
        // 按时间段过滤
        if (startTime != null) {
            allAlarms = allAlarms.stream()
                    .filter(alarm -> alarm.getAlarmTime() != null && alarm.getAlarmTime().isAfter(startTime))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // 统计总数
        statistics.setTotal((long) allAlarms.size());
        
        // 统计未处理数量
        long unhandledCount = allAlarms.stream()
                .filter(alarm -> !Boolean.TRUE.equals(alarm.getIsResolved()))
                .count();
        statistics.setUnhandled(unhandledCount);
        
        // 按类型统计
        Map<String, Long> typeStatistics = new HashMap<>();
        List<String> distinctTypes = allAlarms.stream()
                .map(AlarmRecord::getAlarmType)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        
        for (String type : distinctTypes) {
            long count = allAlarms.stream()
                    .filter(alarm -> type.equals(alarm.getAlarmType()))
                    .count();
            typeStatistics.put(type, count);
        }
        statistics.setTypeStatistics(typeStatistics);
        
        return ApiResponse.success(statistics);
    }
    
    /**
     * 4.3 标记报警为已处理
     */
    @PutMapping("/{alarmId}/handle")
    public ApiResponse<String> handleAlarm(@PathVariable Long alarmId) {
        try {
            AlarmRecord alarmRecord = alarmService.findById(alarmId);
            if (alarmRecord == null) {
                return ApiResponse.error(404, "报警记录不存在");
            }
            
            alarmRecord.setIsResolved(true);
            alarmService.save(alarmRecord);
            return ApiResponse.success("报警已标记为已处理");
        } catch (Exception e) {
            return ApiResponse.error("报警处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 4.4 获取未处理报警数量
     */
    @GetMapping("/unhandled/count")
    public ApiResponse<Long> getUnhandledAlarmCount() {
        try {
            List<AlarmRecord> unhandledAlarms = alarmService.findUnresolvedAlarms();
            return ApiResponse.success((long) unhandledAlarms.size());
        } catch (Exception e) {
            return ApiResponse.error("获取未处理报警数量失败: " + e.getMessage());
        }
    }
    
    @Data
    public static class AlarmStatistics {
        private Long total;
        private Long unhandled;
        private Map<String, Long> typeStatistics;
    }
}