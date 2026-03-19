package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.AlertDTO;
import com.citycharge.entity.AlarmRecord;
import com.citycharge.service.AlarmService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {
    
    private final AlarmService alarmService;
    
    @GetMapping("")
    public ApiResponse<List<AlertDTO>> getAlerts(
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(required = false) String type) {
        
        List<AlarmRecord> alerts;
        if (resolved != null) {
            alerts = alarmService.findByResolved(resolved);
        } else if (type != null && !type.isEmpty()) {
            alerts = alarmService.findByAlarmType(type);
        } else {
            alerts = alarmService.findAll();
        }
        
        List<AlertDTO> alertDTOs = alerts.stream()
                .map(AlertDTO::fromEntity)
                .collect(Collectors.toList());
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