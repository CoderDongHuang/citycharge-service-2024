package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.entity.AlertLog;
import com.citycharge.entity.Battery;
import com.citycharge.entity.Vehicle;
import com.citycharge.repository.AlertLogRepository;
import com.citycharge.repository.BatteryRepository;
import com.citycharge.repository.VehicleRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlarmController {
    
    private final AlertLogRepository alertLogRepository;
    private final VehicleRepository vehicleRepository;
    private final BatteryRepository batteryRepository;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 1. 获取报警列表（分页+筛选）
     * GET /alerts
     */
    @GetMapping
    public ApiResponse<AlertListResponse> getAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Integer resolved,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        try {
            List<AlertLog> allAlarms = alertLogRepository.findAll();
            
            if (type != null && !type.isEmpty()) {
                allAlarms = allAlarms.stream()
                        .filter(alarm -> type.equals(alarm.getType()))
                        .collect(Collectors.toList());
            }
            
            if (level != null && !level.isEmpty()) {
                allAlarms = allAlarms.stream()
                        .filter(alarm -> level.equals(alarm.getLevel()))
                        .collect(Collectors.toList());
            }
            
            if (resolved != null) {
                boolean isResolved = resolved == 1;
                allAlarms = allAlarms.stream()
                        .filter(alarm -> isResolved == Boolean.TRUE.equals(alarm.getResolved()))
                        .collect(Collectors.toList());
            }
            
            if (startTime != null && !startTime.isEmpty()) {
                LocalDateTime start = LocalDateTime.parse(startTime.replace(" ", "T"));
                allAlarms = allAlarms.stream()
                        .filter(alarm -> !alarm.getTimestamp().isBefore(start))
                        .collect(Collectors.toList());
            }
            
            if (endTime != null && !endTime.isEmpty()) {
                LocalDateTime end = LocalDateTime.parse(endTime.replace(" ", "T"));
                allAlarms = allAlarms.stream()
                        .filter(alarm -> !alarm.getTimestamp().isAfter(end))
                        .collect(Collectors.toList());
            }
            
            int total = allAlarms.size();
            int validPage = Math.max(0, page);
            int startIndex = validPage * size;
            
            if (startIndex >= total) {
                AlertListResponse response = new AlertListResponse();
                response.setContent(new ArrayList<>());
                response.setTotalElements(0L);
                response.setTotalPages(0);
                response.setSize(size);
                response.setNumber(0);
                return ApiResponse.success(response);
            }
            
            int endIndex = Math.min(startIndex + size, total);
            List<AlertLog> pageAlarms = allAlarms.subList(startIndex, endIndex);
            
            List<AlertDTO> alertDTOs = pageAlarms.stream()
                    .map(this::convertToAlertDTO)
                    .collect(Collectors.toList());
            
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
     * GET /alerts/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<AlertDTO> getAlertDetail(@PathVariable Long id) {
        try {
            AlertLog alertLog = alertLogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("报警记录不存在"));
            
            AlertDTO dto = convertToAlertDTO(alertLog);
            return ApiResponse.success(dto);
            
        } catch (Exception e) {
            return ApiResponse.error("获取报警详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 3. 获取报警统计
     * GET /alerts/statistics
     */
    @GetMapping("/statistics")
    public ApiResponse<AlertStatistics> getAlertStatistics() {
        try {
            List<AlertLog> allAlarms = alertLogRepository.findAll();
            
            long total = allAlarms.size();
            
            long active = allAlarms.stream()
                    .filter(alarm -> Boolean.FALSE.equals(alarm.getResolved()))
                    .count();
            
            long resolved = allAlarms.stream()
                    .filter(alarm -> Boolean.TRUE.equals(alarm.getResolved()))
                    .count();
            
            long critical = allAlarms.stream()
                    .filter(alarm -> Boolean.FALSE.equals(alarm.getResolved()) && "critical".equals(alarm.getLevel()))
                    .count();
            
            long high = allAlarms.stream()
                    .filter(alarm -> Boolean.FALSE.equals(alarm.getResolved()) && "high".equals(alarm.getLevel()))
                    .count();
            
            long medium = allAlarms.stream()
                    .filter(alarm -> Boolean.FALSE.equals(alarm.getResolved()) && "medium".equals(alarm.getLevel()))
                    .count();
            
            long low = allAlarms.stream()
                    .filter(alarm -> Boolean.FALSE.equals(alarm.getResolved()) && "low".equals(alarm.getLevel()))
                    .count();
            
            long urgent = critical;
            
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            long todayCount = allAlarms.stream()
                    .filter(alarm -> !alarm.getTimestamp().isBefore(startOfDay))
                    .count();
            
            LocalDateTime startOfWeek = LocalDate.now().minus(6, ChronoUnit.DAYS).atStartOfDay();
            long weekCount = allAlarms.stream()
                    .filter(alarm -> !alarm.getTimestamp().isBefore(startOfWeek))
                    .count();
            
            return ApiResponse.success(new AlertStatistics(
                total, active, resolved, urgent, critical, high, medium, low, todayCount, weekCount
            ));
            
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 4. 处理单个报警
     * PUT /alerts/{id}/resolve
     */
    @PutMapping("/{id}/resolve")
    public ApiResponse<ResolveResponse> resolveAlarm(@PathVariable Long id, @RequestBody ResolveRequest request) {
        try {
            AlertLog alertLog = alertLogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("报警记录不存在"));
            
            alertLog.setResolved(true);
            alertLog.setResolvedAt(LocalDateTime.now());
            
            if (request != null) {
                if (request.getResolved_by() != null) {
                    alertLog.setResolvedBy(request.getResolved_by());
                }
                if (request.getResolved_note() != null) {
                    alertLog.setResolvedNote(request.getResolved_note());
                }
            }
            
            AlertLog updated = alertLogRepository.save(alertLog);
            
            updateDeviceStatus(alertLog);
            
            ResolveResponse response = new ResolveResponse();
            response.setId(updated.getId());
            response.setResolved(1);
            response.setResolved_by(updated.getResolvedBy());
            response.setResolved_at(updated.getResolvedAt().format(FORMATTER));
            response.setResolved_note(updated.getResolvedNote());
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("处理报警失败：" + e.getMessage());
        }
    }
    
    /**
     * 5. 批量处理报警
     * PUT /alerts/batch-resolve
     */
    @PutMapping("/batch-resolve")
    public ApiResponse<BatchResolveResponse> batchResolveAlarms(@RequestBody BatchResolveRequest request) {
        try {
            List<Long> ids = request.getIds();
            int successCount = 0;
            int failCount = 0;
            List<Long> processedIds = new ArrayList<>();
            
            for (Long id : ids) {
                try {
                    AlertLog alertLog = alertLogRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("报警记录不存在"));
                    
                    alertLog.setResolved(true);
                    alertLog.setResolvedAt(LocalDateTime.now());
                    
                    if (request.getResolved_by() != null) {
                        alertLog.setResolvedBy(request.getResolved_by());
                    }
                    if (request.getResolved_note() != null) {
                        alertLog.setResolvedNote(request.getResolved_note());
                    }
                    
                    alertLogRepository.save(alertLog);
                    
                    updateDeviceStatus(alertLog);
                    
                    successCount++;
                    processedIds.add(id);
                    
                } catch (Exception e) {
                    failCount++;
                }
            }
            
            BatchResolveResponse response = new BatchResolveResponse();
            response.setSuccessCount(successCount);
            response.setFailCount(failCount);
            response.setProcessedIds(processedIds);
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("批量处理失败：" + e.getMessage());
        }
    }
    
    private void updateDeviceStatus(AlertLog alertLog) {
        String type = alertLog.getType();
        String vid = alertLog.getVid();
        String pid = alertLog.getPid();
        
        if ("lowBattery".equals(type) && pid != null) {
            batteryRepository.findByPid(pid).ifPresent(battery -> {
                battery.setStatus("normal");
                batteryRepository.save(battery);
            });
        }
        
        if ("temperature".equals(type)) {
            if (pid != null) {
                batteryRepository.findByPid(pid).ifPresent(battery -> {
                    battery.setStatus("normal");
                    batteryRepository.save(battery);
                });
            }
        }
        
        if ("unreachable".equals(type) && vid != null) {
            vehicleRepository.findByVid(vid).ifPresent(vehicle -> {
                vehicle.setOnlineStatus(true);
                vehicleRepository.save(vehicle);
            });
        }
    }
    
    private AlertDTO convertToAlertDTO(AlertLog alertLog) {
        AlertDTO dto = new AlertDTO();
        dto.setId(alertLog.getId());
        dto.setType(alertLog.getType());
        dto.setVid(alertLog.getVid());
        dto.setPid(alertLog.getPid());
        dto.setMessage(alertLog.getMessage());
        dto.setLevel(alertLog.getLevel());
        dto.setResolved(Boolean.TRUE.equals(alertLog.getResolved()) ? 1 : 0);
        dto.setResolved_by(alertLog.getResolvedBy());
        dto.setResolved_at(alertLog.getResolvedAt() != null ? alertLog.getResolvedAt().format(FORMATTER) : null);
        dto.setResolved_note(alertLog.getResolvedNote());
        dto.setTrigger_value(alertLog.getTriggerValue());
        dto.setThreshold_value(alertLog.getThresholdValue());
        dto.setPosition_x(alertLog.getPositionX());
        dto.setPosition_y(alertLog.getPositionY());
        dto.setTimestamp(alertLog.getTimestamp() != null ? alertLog.getTimestamp().format(FORMATTER) : null);
        dto.setCreated_at(alertLog.getCreatedTime() != null ? alertLog.getCreatedTime().format(FORMATTER) : null);
        dto.setUpdated_at(alertLog.getUpdatedAt() != null ? alertLog.getUpdatedAt().format(FORMATTER) : null);
        
        return dto;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class AlertDTO {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("type")
        private String type;
        @JsonProperty("vid")
        private String vid;
        @JsonProperty("pid")
        private String pid;
        @JsonProperty("message")
        private String message;
        @JsonProperty("level")
        private String level;
        @JsonProperty("resolved")
        private Integer resolved;
        @JsonProperty("resolved_by")
        private String resolved_by;
        @JsonProperty("resolved_at")
        private String resolved_at;
        @JsonProperty("resolved_note")
        private String resolved_note;
        @JsonProperty("trigger_value")
        private Double trigger_value;
        @JsonProperty("threshold_value")
        private Double threshold_value;
        @JsonProperty("position_x")
        private Integer position_x;
        @JsonProperty("position_y")
        private Integer position_y;
        @JsonProperty("timestamp")
        private String timestamp;
        @JsonProperty("created_at")
        private String created_at;
        @JsonProperty("updated_at")
        private String updated_at;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class AlertListResponse {
        @JsonProperty("content")
        private List<AlertDTO> content;
        @JsonProperty("totalElements")
        private Long totalElements;
        @JsonProperty("totalPages")
        private Integer totalPages;
        @JsonProperty("size")
        private Integer size;
        @JsonProperty("number")
        private Integer number;
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
        @JsonProperty("urgent")
        private Long urgent;
        @JsonProperty("critical")
        private Long critical;
        @JsonProperty("high")
        private Long high;
        @JsonProperty("medium")
        private Long medium;
        @JsonProperty("low")
        private Long low;
        @JsonProperty("todayCount")
        private Long todayCount;
        @JsonProperty("weekCount")
        private Long weekCount;
        
        public AlertStatistics(Long total, Long active, Long resolved, Long urgent, 
                              Long critical, Long high, Long medium, Long low, Long todayCount, Long weekCount) {
            this.total = total;
            this.active = active;
            this.resolved = resolved;
            this.urgent = urgent;
            this.critical = critical;
            this.high = high;
            this.medium = medium;
            this.low = low;
            this.todayCount = todayCount;
            this.weekCount = weekCount;
        }
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class ResolveRequest {
        @JsonProperty("resolved_by")
        private String resolved_by;
        @JsonProperty("resolved_note")
        private String resolved_note;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class ResolveResponse {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("resolved")
        private Integer resolved;
        @JsonProperty("resolved_by")
        private String resolved_by;
        @JsonProperty("resolved_at")
        private String resolved_at;
        @JsonProperty("resolved_note")
        private String resolved_note;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class BatchResolveRequest {
        @JsonProperty("ids")
        private List<Long> ids;
        @JsonProperty("resolved_by")
        private String resolved_by;
        @JsonProperty("resolved_note")
        private String resolved_note;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class BatchResolveResponse {
        @JsonProperty("successCount")
        private Integer successCount;
        @JsonProperty("failCount")
        private Integer failCount;
        @JsonProperty("processedIds")
        private List<Long> processedIds;
    }
}
