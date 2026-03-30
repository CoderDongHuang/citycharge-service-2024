package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.entity.AlertLog;
import com.citycharge.repository.AlertLogRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlarmController {
    
    private final AlertLogRepository alertLogRepository;
    
    /**
     * 1. 获取报警列表
     * GET /api/alerts
     * 参数：page, size, type, level, resolved, startTime, endTime
     */
    @GetMapping
    public ApiResponse<AlertListResponse> getAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
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
            if (resolved != null) {
                allAlarms = allAlarms.stream()
                        .filter(alarm -> resolved.equals(alarm.getResolved()))
                        .collect(Collectors.toList());
            }
            
            // 时间筛选
            if (startTime != null && !startTime.isEmpty()) {
                LocalDateTime start = LocalDateTime.parse(startTime);
                allAlarms = allAlarms.stream()
                        .filter(alarm -> !alarm.getTimestamp().isBefore(start))
                        .collect(Collectors.toList());
            }
            
            if (endTime != null && !endTime.isEmpty()) {
                LocalDateTime end = LocalDateTime.parse(endTime);
                allAlarms = allAlarms.stream()
                        .filter(alarm -> !alarm.getTimestamp().isAfter(end))
                        .collect(Collectors.toList());
            }
            
            // 分页处理（前端页码从 0 开始）
            int total = allAlarms.size();
            int validPage = Math.max(0, page);
            int startIndex = validPage * size;
            
            if (startIndex >= total) {
                AlertListResponse response = new AlertListResponse();
                response.setContent(new java.util.ArrayList<>());
                response.setTotalElements(0L);
                response.setTotalPages(0);
                response.setSize(size);
                response.setNumber(0);
                return ApiResponse.success(response);
            }
            
            int endIndex = Math.min(startIndex + size, total);
            List<AlertLog> pageAlarms = allAlarms.subList(startIndex, endIndex);
            
            // 转换为前端需要的格式
            List<AlertDTO> alertDTOs = pageAlarms.stream()
                    .map(this::convertToAlertDTO)
                    .collect(Collectors.toList());
            
            // 计算总页数
            int totalPages = (int) Math.ceil((double) total / size);
            
            AlertListResponse response = new AlertListResponse();
            response.setContent(alertDTOs);
            response.setTotalElements((long) total);
            response.setTotalPages(totalPages);
            response.setSize(size);
            response.setNumber(validPage);
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("获取报警列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 2. 获取报警详情
     * GET /api/alerts/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<AlertDetailResponse> getAlertDetail(@PathVariable Long id) {
        try {
            AlertLog alertLog = alertLogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("报警记录不存在"));
            
            AlertDetailResponse response = convertToAlertDetailResponse(alertLog);
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("获取报警详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 3. 处理报警
     * PUT /api/alerts/{id}/resolve
     * 请求体：{ resolvedBy: "admin", resolvedNote: "说明" }
     */
    @PutMapping("/{id}/resolve")
    public ApiResponse<Void> resolveAlarm(@PathVariable Long id, @RequestBody ResolveAlarmRequest request) {
        try {
            AlertLog alertLog = alertLogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("报警记录不存在"));
            
            alertLog.setResolved(true);
            alertLog.setResolvedAt(LocalDateTime.now());
            
            if (request != null) {
                if (request.getResolvedBy() != null) {
                    alertLog.setResolvedBy(request.getResolvedBy());
                }
                if (request.getResolvedNote() != null) {
                    alertLog.setResolvedNote(request.getResolvedNote());
                }
            }
            
            alertLogRepository.save(alertLog);
            
            return ApiResponse.success(null);
            
        } catch (Exception e) {
            return ApiResponse.error("处理报警失败：" + e.getMessage());
        }
    }
    
    /**
     * 4. 获取报警统计
     * GET /api/alerts/statistics
     */
    @GetMapping("/statistics")
    public ApiResponse<AlertStatistics> getAlertStatistics() {
        try {
            List<AlertLog> allAlarms = alertLogRepository.findAll();
            
            // 总数
            long total = allAlarms.size();
            
            // 未处理数量
            long active = allAlarms.stream()
                    .filter(alarm -> Boolean.FALSE.equals(alarm.getResolved()))
                    .count();
            
            // 已处理数量
            long resolved = allAlarms.stream()
                    .filter(alarm -> Boolean.TRUE.equals(alarm.getResolved()))
                    .count();
            
            // 按级别统计
            long critical = allAlarms.stream()
                    .filter(alarm -> "critical".equals(alarm.getLevel()))
                    .count();
            
            long high = allAlarms.stream()
                    .filter(alarm -> "high".equals(alarm.getLevel()))
                    .count();
            
            long medium = allAlarms.stream()
                    .filter(alarm -> "medium".equals(alarm.getLevel()))
                    .count();
            
            long low = allAlarms.stream()
                    .filter(alarm -> "low".equals(alarm.getLevel()))
                    .count();
            
            // 按类型统计
            Map<String, Long> byType = new HashMap<>();
            allAlarms.forEach(alarm -> {
                String type = alarm.getType() != null ? alarm.getType() : "unknown";
                byType.put(type, byType.getOrDefault(type, 0L) + 1);
            });
            
            // 今日报警数
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            long today = allAlarms.stream()
                    .filter(alarm -> !alarm.getTimestamp().isBefore(startOfDay))
                    .count();
            
            // 本周报警数（最近 7 天）
            LocalDateTime startOfWeek = LocalDate.now().minus(6, ChronoUnit.DAYS).atStartOfDay();
            long week = allAlarms.stream()
                    .filter(alarm -> !alarm.getTimestamp().isBefore(startOfWeek))
                    .count();
            
            // 本月报警数（最近 30 天）
            LocalDateTime startOfMonth = LocalDate.now().minus(29, ChronoUnit.DAYS).atStartOfDay();
            long month = allAlarms.stream()
                    .filter(alarm -> !alarm.getTimestamp().isBefore(startOfMonth))
                    .count();
            
            // 直接返回包含所有字段的对象
            return ApiResponse.success(new AlertStatistics(
                total, active, resolved, critical, high, medium, low, byType, today, week, month
            ));
            
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 转换 AlertLog 为 AlertDTO
     */
    private AlertDTO convertToAlertDTO(AlertLog alertLog) {
        AlertDTO dto = new AlertDTO();
        dto.setId(alertLog.getId());
        dto.setType(alertLog.getType());
        dto.setVid(alertLog.getVid());
        dto.setPid(alertLog.getPid());
        dto.setMessage(alertLog.getMessage());
        dto.setLevel(alertLog.getLevel());
        dto.setResolved(alertLog.getResolved());
        dto.setResolvedBy(alertLog.getResolvedBy());
        dto.setResolvedAt(alertLog.getResolvedAt());
        dto.setResolvedNote(alertLog.getResolvedNote());
        dto.setTriggerValue(alertLog.getTriggerValue());
        dto.setThresholdValue(alertLog.getThresholdValue());
        dto.setPositionX(alertLog.getPositionX());
        dto.setPositionY(alertLog.getPositionY());
        dto.setTimestamp(alertLog.getTimestamp());
        dto.setCreatedAt(alertLog.getCreatedTime());
        dto.setUpdatedAt(alertLog.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * 转换 AlertLog 为 AlertDetailResponse
     */
    private AlertDetailResponse convertToAlertDetailResponse(AlertLog alertLog) {
        AlertDetailResponse dto = new AlertDetailResponse();
        dto.setId(alertLog.getId());
        dto.setType(alertLog.getType());
        dto.setVid(alertLog.getVid());
        dto.setPid(alertLog.getPid());
        dto.setMessage(alertLog.getMessage());
        dto.setLevel(alertLog.getLevel());
        dto.setResolved(alertLog.getResolved());
        dto.setResolvedBy(alertLog.getResolvedBy());
        dto.setResolvedAt(alertLog.getResolvedAt());
        dto.setResolvedNote(alertLog.getResolvedNote());
        dto.setTriggerValue(alertLog.getTriggerValue());
        dto.setThresholdValue(alertLog.getThresholdValue());
        dto.setPositionX(alertLog.getPositionX());
        dto.setPositionY(alertLog.getPositionY());
        dto.setTimestamp(alertLog.getTimestamp());
        dto.setCreatedAt(alertLog.getCreatedTime());
        dto.setUpdatedAt(alertLog.getUpdatedAt());
        
        return dto;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class AlertDTO {
        private Long id;
        private String type;
        private String vid;
        private String pid;
        private String message;
        private String level;
        private Boolean resolved;
        private String resolvedBy;
        private LocalDateTime resolvedAt;
        private String resolvedNote;
        private Double triggerValue;
        private Double thresholdValue;
        private Integer positionX;
        private Integer positionY;
        private LocalDateTime timestamp;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class AlertListResponse {
        private List<AlertDTO> content;
        private Long totalElements;
        private Integer totalPages;
        private Integer size;
        private Integer number;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class AlertDetailResponse {
        private Long id;
        private String type;
        private String vid;
        private String pid;
        private String message;
        private String level;
        private Boolean resolved;
        private String resolvedBy;
        private LocalDateTime resolvedAt;
        private String resolvedNote;
        private Double triggerValue;
        private Double thresholdValue;
        private Integer positionX;
        private Integer positionY;
        private LocalDateTime timestamp;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class AlertStatistics {
        @JsonProperty("total")
        private Long total;
        @JsonProperty("active")
        private Long active;
        @JsonProperty("resolved")
        private Long resolved;
        @JsonProperty("critical")
        private Long critical;
        @JsonProperty("high")
        private Long high;
        @JsonProperty("medium")
        private Long medium;
        @JsonProperty("low")
        private Long low;
        @JsonProperty("byType")
        private Map<String, Long> byType;
        @JsonProperty("today")
        private Long today;
        @JsonProperty("week")
        private Long week;
        @JsonProperty("month")
        private Long month;
        
        public AlertStatistics(Long total, Long active, Long resolved, Long critical, Long high, 
                              Long medium, Long low, Map<String, Long> byType, Long today, 
                              Long week, Long month) {
            this.total = total;
            this.active = active;
            this.resolved = resolved;
            this.critical = critical;
            this.high = high;
            this.medium = medium;
            this.low = low;
            this.byType = byType;
            this.today = today;
            this.week = week;
            this.month = month;
        }
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class ResolveAlarmRequest {
        private String resolvedBy;
        private String resolvedNote;
    }
}
