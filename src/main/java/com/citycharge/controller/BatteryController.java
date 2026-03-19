package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.BatteryDTO;
import com.citycharge.entity.Battery;
import com.citycharge.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/batteries")
@RequiredArgsConstructor
public class BatteryController {
    
    private final BatteryService batteryService;
    
    @GetMapping("")
    public ApiResponse<List<BatteryDTO>> getAllBatteries(@RequestParam(required = false) String status) {
        List<Battery> batteries;
        if (status != null && !status.isEmpty()) {
            batteries = batteryService.findByStatus(Battery.BatteryStatus.valueOf(status));
        } else {
            batteries = batteryService.findAll();
        }
        
        List<BatteryDTO> batteryDTOs = batteries.stream()
                .map(BatteryDTO::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success(batteryDTOs);
    }
    
    @GetMapping("/{pid}")
    public ApiResponse<BatteryDTO> getBatteryByPid(@PathVariable String pid) {
        Battery battery = batteryService.findByPid(pid);
        if (battery == null) {
            return ApiResponse.error(404, "电池不存在");
        }
        return ApiResponse.success(BatteryDTO.fromEntity(battery));
    }
    
    @GetMapping("/{pid}/history")
    public ApiResponse<List<BatteryDTO.BatteryHistoryDTO>> getBatteryHistory(
            @PathVariable String pid,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        // 这里需要实现电池历史记录的查询逻辑
        // 目前返回空列表作为示例
        return ApiResponse.success(java.util.Collections.emptyList());
    }
    
    @PostMapping("")
    public ApiResponse<BatteryDTO> createBattery(@RequestBody Battery battery) {
        try {
            Battery savedBattery = batteryService.save(battery);
            return ApiResponse.success("电池创建成功", BatteryDTO.fromEntity(savedBattery));
        } catch (Exception e) {
            return ApiResponse.error("电池创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{pid}")
    public ApiResponse<BatteryDTO> updateBattery(@PathVariable String pid, @RequestBody Battery battery) {
        try {
            Battery updatedBattery = batteryService.update(pid, battery);
            return ApiResponse.success("电池更新成功", BatteryDTO.fromEntity(updatedBattery));
        } catch (Exception e) {
            return ApiResponse.error("电池更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{pid}")
    public ApiResponse<String> deleteBattery(@PathVariable String pid) {
        try {
            batteryService.deleteByPid(pid);
            return ApiResponse.success("电池删除成功");
        } catch (Exception e) {
            return ApiResponse.error("电池删除失败: " + e.getMessage());
        }
    }
}