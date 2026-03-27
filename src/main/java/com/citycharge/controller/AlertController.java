package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.AlertDTO;
import com.citycharge.entity.AlarmRecord;
import com.citycharge.service.AlarmService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {
    
    private final AlarmService alarmService;

    @GetMapping("/statistics")
    public ApiResponse<AlertStatistics> getAlertStatistics() {
        AlertStatistics statistics = new AlertStatistics();
        statistics.setTotal(alarmService.countAll());
        statistics.setResolved(alarmService.countByResolved(true));
        statistics.setUnresolved(alarmService.countByResolved(false));
        return ApiResponse.success(statistics);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAlert(@PathVariable Long id) {
        try {
            AlarmRecord alarmRecord = alarmService.findById(id);
            if (alarmRecord == null) {
                return ApiResponse.error(404, "报警记录不存在");
            }
            alarmService.deleteById(id);
            return ApiResponse.success("报警删除成功");
        } catch (Exception e) {
            return ApiResponse.error("报警删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<AlertDTO> updateAlert(@PathVariable Long id, @RequestBody AlertUpdateRequest request) {
        try {
            AlarmRecord alarmRecord = alarmService.findById(id);
            if (alarmRecord == null) {
                return ApiResponse.error(404, "报警记录不存在");
            }
            
            if (request.getType() != null) {
                alarmRecord.setAlarmType(request.getType());
            }
            if (request.getMessage() != null) {
                alarmRecord.setAlarmMessage(request.getMessage());
            }
            if (request.getLevel() != null) {
                alarmRecord.setAlarmLevel(request.getLevel());
            }
            
            AlarmRecord updatedAlert = alarmService.save(alarmRecord);
            return ApiResponse.success("报警更新成功", AlertDTO.fromEntity(updatedAlert));
        } catch (Exception e) {
            return ApiResponse.error("报警更新失败: " + e.getMessage());
        }
    }

    @Data
    public static class AlertStatistics {
        private Long total;
        private Long resolved;
        private Long unresolved;
    }

    @Data
    public static class AlertUpdateRequest {
        private String type;
        private String message;
        private String level;
    }
    
    @GetMapping("")
    public ApiResponse<Page<AlertDTO>> getAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(required = false) String type) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AlarmRecord> alerts;
        
        if (resolved != null) {
            // 这里需要实现分页查询，暂时返回所有数据
            List<AlarmRecord> alertList = alarmService.findByResolved(resolved);
            alerts = new org.springframework.data.domain.PageImpl<>(alertList, pageable, alertList.size());
        } else if (type != null && !type.isEmpty()) {
            List<AlarmRecord> alertList = alarmService.findByAlarmType(type);
            alerts = new org.springframework.data.domain.PageImpl<>(alertList, pageable, alertList.size());
        } else {
            List<AlarmRecord> alertList = alarmService.findAll();
            alerts = new org.springframework.data.domain.PageImpl<>(alertList, pageable, alertList.size());
        }
        
        Page<AlertDTO> alertDTOs = alerts.map(AlertDTO::fromEntity);
        return ApiResponse.success(alertDTOs);
    }
    
    @PostMapping("/alerts")
    public ApiResponse<AlertDTO> createAlert(@RequestBody AlertCreateRequest request) {
        try {
            AlarmRecord alarmRecord = new AlarmRecord();
            alarmRecord.setAlarmType(request.getType());
            alarmRecord.setVehicleVid(request.getVid());
            alarmRecord.setBatteryPid(request.getPid());
            alarmRecord.setAlarmMessage(request.getMessage());
            
            // 设置位置信息
            if (request.getPosition() != null) {
                alarmRecord.setPositionX(request.getPosition().getX());
                alarmRecord.setPositionY(request.getPosition().getY());
            }
            
            alarmRecord.setIsResolved(false);
            
            AlarmRecord savedAlert = alarmService.save(alarmRecord);
            return ApiResponse.success("报警创建成功", AlertDTO.fromEntity(savedAlert));
        } catch (Exception e) {
            return ApiResponse.error("报警创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/alerts/{id}/resolve")
    public ApiResponse<String> resolveAlert(@PathVariable Long id, @RequestBody(required = false) AlertResolveRequest request) {
        try {
            AlarmRecord alarmRecord = alarmService.findById(id);
            if (alarmRecord == null) {
                return ApiResponse.error(404, "报警记录不存在");
            }
            
            alarmRecord.setIsResolved(true);
            if (request != null) {
                alarmRecord.setResolveTime(java.time.LocalDateTime.now());
                // 这里可以设置解决人和解决说明，但AlarmRecord实体类需要添加这些字段
            }
            alarmService.save(alarmRecord);
            return ApiResponse.success("报警已标记为已解决");
        } catch (Exception e) {
            return ApiResponse.error("报警解决失败: " + e.getMessage());
        }
    }
    
    @Data
    public static class AlertCreateRequest {
        private String type;
        private String vid;
        private String pid;
        private String message;
        private String level;
        private Double triggerValue;
        private Double thresholdValue;
        private Position position;
        
        @Data
        public static class Position {
            private Integer x;
            private Integer y;
        }
    }
    
    @Data
    public static class AlertResolveRequest {
        private String resolvedBy;
        private String resolvedNote;
    }
}