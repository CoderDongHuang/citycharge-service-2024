package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.entity.AlertLog;
import com.citycharge.repository.AlertLogRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmController {
    
    private final AlertLogRepository alertLogRepository;
    
    /**
     * 1. 获取报警列表
     * GET /alarms
     * 参数：page, pageSize, type, level, startTime, endTime, resolved
     */
    @GetMapping
    public ApiResponse<AlarmListResponse> getAlarms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "false") boolean resolved) {
        
        try {
            // 构建查询条件
            List<AlertLog> allAlarms = alertLogRepository.findAll();
            
            // 类型筛选
            if (type != null && !type.isEmpty()) {
                allAlarms = allAlarms.stream()
                        .filter(alarm -> type.equals(alarm.getType()))
                        .collect(Collectors.toList());
            }
            
            // 级别筛选
            if (level != null && !level.isEmpty()) {
                allAlarms = allAlarms.stream()
                        .filter(alarm -> level.equals(alarm.getLevel()))
                        .collect(Collectors.toList());
            }
            
            // 是否已解决筛选
            allAlarms = allAlarms.stream()
                    .filter(alarm -> resolved == alarm.getResolved())
                    .collect(Collectors.toList());
            
            // 时间筛选
            if (startTime != null && !startTime.isEmpty()) {
                LocalDateTime start = LocalDateTime.parse(startTime);
                allAlarms = allAlarms.stream()
                        .filter(alarm -> alarm.getTimestamp().isAfter(start))
                        .collect(Collectors.toList());
            }
            
            if (endTime != null && !endTime.isEmpty()) {
                LocalDateTime end = LocalDateTime.parse(endTime);
                allAlarms = allAlarms.stream()
                        .filter(alarm -> alarm.getTimestamp().isBefore(end))
                        .collect(Collectors.toList());
            }
            
            // 分页处理
            int total = allAlarms.size();
            int validPage = Math.max(1, page);
            int startIndex = (validPage - 1) * pageSize;
            
            if (startIndex >= total) {
                AlarmListResponse response = new AlarmListResponse();
                response.setTotal(total);
                response.setPage(validPage);
                response.setPageSize(pageSize);
                response.setAlarms(new java.util.ArrayList<>());
                return ApiResponse.success(response);
            }
            
            int endIndex = Math.min(startIndex + pageSize, total);
            List<AlertLog> pageAlarms = allAlarms.subList(startIndex, endIndex);
            
            // 转换为前端需要的格式
            List<AlarmDTO> alarmDTOs = pageAlarms.stream()
                    .map(this::convertToAlarmDTO)
                    .collect(Collectors.toList());
            
            AlarmListResponse response = new AlarmListResponse();
            response.setTotal(total);
            response.setPage(validPage);
            response.setPageSize(pageSize);
            response.setAlarms(alarmDTOs);
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("获取报警列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 2. 获取报警统计
     * GET /alarms/statistics
     */
    @GetMapping("/statistics")
    public ApiResponse<AlarmStatistics> getAlarmStatistics() {
        try {
            List<AlertLog> allAlarms = alertLogRepository.findAll();
            
            AlarmStatistics statistics = new AlarmStatistics();
            
            // 统计今日报警数
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            long today = allAlarms.stream()
                    .filter(alarm -> alarm.getTimestamp().isAfter(startOfDay))
                    .count();
            statistics.setToday(today);
            
            // 统计本周报警数
            LocalDateTime startOfWeek = LocalDate.now().minus(7, ChronoUnit.DAYS).atStartOfDay();
            long week = allAlarms.stream()
                    .filter(alarm -> alarm.getTimestamp().isAfter(startOfWeek))
                    .count();
            statistics.setWeek(week);
            
            // 统计本月报警数
            LocalDateTime startOfMonth = LocalDate.now().minus(30, ChronoUnit.DAYS).atStartOfDay();
            long month = allAlarms.stream()
                    .filter(alarm -> alarm.getTimestamp().isAfter(startOfMonth))
                    .count();
            statistics.setMonth(month);
            
            // 统计未读数量（未处理的报警）
            long unreadCount = allAlarms.stream()
                    .filter(alarm -> Boolean.FALSE.equals(alarm.getResolved()))
                    .count();
            statistics.setUnreadCount(unreadCount);
            
            // 按类型统计
            Map<String, Long> byType = new HashMap<>();
            allAlarms.forEach(alarm -> {
                String type = alarm.getType() != null ? alarm.getType() : "unknown";
                byType.put(type, byType.getOrDefault(type, 0L) + 1);
            });
            statistics.setByType(byType);
            
            // 按级别统计
            Map<String, Long> byLevel = new HashMap<>();
            allAlarms.forEach(alarm -> {
                String level = alarm.getLevel() != null ? alarm.getLevel() : "unknown";
                byLevel.put(level, byLevel.getOrDefault(level, 0L) + 1);
            });
            statistics.setByLevel(byLevel);
            
            return ApiResponse.success(statistics);
            
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 3. 处理报警
     * PUT /alarms/{id}/handle
     */
    @PutMapping("/{id}/handle")
    public ApiResponse<HandleAlarmResponse> handleAlarm(@PathVariable Long id, @RequestBody(required = false) HandleAlarmRequest request) {
        try {
            AlertLog alertLog = alertLogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("报警记录不存在"));
            
            alertLog.setResolved(true);
            alertLog.setResolvedAt(LocalDateTime.now());
            
            if (request != null && request.getHandledBy() != null) {
                alertLog.setResolvedBy(request.getHandledBy());
            }
            if (request != null && request.getNotes() != null) {
                alertLog.setResolvedNote(request.getNotes());
            }
            
            AlertLog updated = alertLogRepository.save(alertLog);
            
            HandleAlarmResponse response = new HandleAlarmResponse();
            response.setId(updated.getId().toString());
            response.setResolved(true);
            response.setHandledBy(updated.getResolvedBy());
            response.setHandledAt(updated.getResolvedAt());
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("处理报警失败：" + e.getMessage());
        }
    }
    
    /**
     * 4. 删除报警
     * DELETE /alarms/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAlarm(@PathVariable Long id) {
        try {
            if (!alertLogRepository.existsById(id)) {
                return ApiResponse.error(404, "报警记录不存在");
            }
            alertLogRepository.deleteById(id);
            return ApiResponse.success("报警删除成功");
        } catch (Exception e) {
            return ApiResponse.error("报警删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 转换 AlertLog 为 AlarmDTO
     */
    private AlarmDTO convertToAlarmDTO(AlertLog alertLog) {
        AlarmDTO dto = new AlarmDTO();
        dto.setId(alertLog.getId().toString());
        dto.setVid(alertLog.getVid());
        dto.setPid(alertLog.getPid());
        dto.setAlarmType(alertLog.getType());
        dto.setLevel(alertLog.getLevel());
        dto.setTriggerValue(alertLog.getTriggerValue());
        dto.setThresholdValue(alertLog.getThresholdValue());
        dto.setTimestamp(alertLog.getTimestamp());
        dto.setResolved(alertLog.getResolved());
        dto.setMessage(alertLog.getMessage());
        dto.setHandledBy(alertLog.getResolvedBy());
        dto.setHandledAt(alertLog.getResolvedAt());
        
        return dto;
    }
    
    @Data
    public static class AlarmDTO {
        private String id;
        private String vid;
        private String pid;
        private String alarmType;
        private String level;
        private Double triggerValue;
        private Double thresholdValue;
        private LocalDateTime timestamp;
        private Boolean resolved;
        private String message;
        private String handledBy;
        private LocalDateTime handledAt;
    }
    
    @Data
    public static class AlarmListResponse {
        private Integer total;
        private Integer page;
        private Integer pageSize;
        private List<AlarmDTO> alarms;
    }
    
    @Data
    public static class AlarmStatistics {
        private Long today;
        private Long week;
        private Long month;
        private Long unreadCount;
        private Map<String, Long> byType;
        private Map<String, Long> byLevel;
    }
    
    @Data
    public static class HandleAlarmRequest {
        private String handledBy;
        private String notes;
    }
    
    @Data
    public static class HandleAlarmResponse {
        private String id;
        private Boolean resolved;
        private String handledBy;
        private LocalDateTime handledAt;
    }
}
