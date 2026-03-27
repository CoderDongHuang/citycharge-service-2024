package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.entity.Battery;
import com.citycharge.entity.BatteryHistory;
import com.citycharge.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/batteries")
@RequiredArgsConstructor
public class BatteryController {
    
    private final BatteryService batteryService;
    
    /**
     * 获取电池列表
     * GET /api/batteries
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Battery>>> getBatteries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Page<Battery> batteries = batteryService.getBatteries(page, size);
            return ResponseEntity.ok(ApiResponse.success("成功", batteries));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取电池列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据电池编号获取电池信息
     * GET /api/batteries/{pid}
     */
    @GetMapping("/{pid}")
    public ResponseEntity<ApiResponse<Battery>> getBattery(@PathVariable String pid) {
        
        try {
            Optional<Battery> batteryOpt = batteryService.getBatteryByPid(pid);
            
            if (batteryOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("成功", batteryOpt.get()));
            } else {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("电池不存在"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取电池信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取电池历史记录
     * GET /api/batteries/{pid}/history
     */
    @GetMapping("/{pid}/history")
    public ResponseEntity<ApiResponse<List<BatteryHistory>>> getBatteryHistory(
            @PathVariable String pid,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Integer limit) {
        
        try {
            // 验证电池是否存在
            Optional<Battery> batteryOpt = batteryService.getBatteryByPid(pid);
            if (!batteryOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("电池不存在"));
            }
            
            // 验证时间范围
            if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("开始时间不能晚于结束时间"));
            }
            
            // 获取历史记录
            List<BatteryHistory> history = batteryService.getBatteryHistory(pid, startTime, endTime, limit);
            return ResponseEntity.ok(ApiResponse.success("成功", history));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取电池历史记录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据车辆编号获取电池列表
     * GET /api/batteries/vehicle/{vid}
     */
    @GetMapping("/vehicle/{vid}")
    public ResponseEntity<ApiResponse<List<Battery>>> getBatteriesByVehicle(@PathVariable String vid) {
        
        try {
            List<Battery> batteries = batteryService.getBatteriesByVid(vid);
            return ResponseEntity.ok(ApiResponse.success("成功", batteries));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取车辆电池列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取电池统计信息
     * GET /api/batteries/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<BatteryService.BatteryStatistics>> getBatteryStatistics() {
        
        try {
            BatteryService.BatteryStatistics statistics = batteryService.getBatteryStatistics();
            return ResponseEntity.ok(ApiResponse.success("成功", statistics));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取电池统计信息失败: " + e.getMessage()));
        }
    }
}