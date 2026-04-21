package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.AdminStatisticsDTO;
import com.citycharge.dto.AdminUserBatteryDTO;
import com.citycharge.dto.AdminUserVehicleDTO;
import com.citycharge.service.AdminUserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserDataController {
    
    private final AdminUserDataService adminUserDataService;
    
    @GetMapping("/user-vehicles")
    public ApiResponse<Map<String, Object>> getUserVehicles(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        Page<AdminUserVehicleDTO> result = adminUserDataService.getUserVehicles(page, size, userId, status, keyword);
        
        Map<String, Object> data = new HashMap<>();
        data.put("content", result.getContent());
        data.put("totalElements", result.getTotalElements());
        data.put("totalPages", result.getTotalPages());
        data.put("currentPage", page != null ? page : 1);
        data.put("pageSize", size != null ? size : 20);
        
        return ApiResponse.success("success", data);
    }
    
    @GetMapping("/user-batteries")
    public ApiResponse<Map<String, Object>> getUserBatteries(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String keyword) {
        
        Page<AdminUserBatteryDTO> result = adminUserDataService.getUserBatteries(page, size, userId, status, model, keyword);
        
        Map<String, Object> data = new HashMap<>();
        data.put("content", result.getContent());
        data.put("totalElements", result.getTotalElements());
        data.put("totalPages", result.getTotalPages());
        data.put("currentPage", page != null ? page : 1);
        data.put("pageSize", size != null ? size : 20);
        
        return ApiResponse.success("success", data);
    }
    
    @GetMapping("/user-data/statistics")
    public ApiResponse<AdminStatisticsDTO> getStatistics() {
        AdminStatisticsDTO statistics = adminUserDataService.getStatistics();
        return ApiResponse.success("success", statistics);
    }
}
