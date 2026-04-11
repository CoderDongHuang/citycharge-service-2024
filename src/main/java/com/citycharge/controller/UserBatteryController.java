package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.UserBatteryDTO;
import com.citycharge.entity.UserBattery;
import com.citycharge.service.UserBatteryService;
import com.citycharge.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/batteries")
@RequiredArgsConstructor
public class UserBatteryController {
    
    private final UserBatteryService userBatteryService;
    private final JwtUtil jwtUtil;
    
    @GetMapping
    public ApiResponse<List<UserBatteryDTO>> getBatteries(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        List<UserBattery> batteries = userBatteryService.findByUserId(userId);
        List<UserBatteryDTO> dtos = batteries.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        return ApiResponse.success(dtos);
    }
    
    @GetMapping("/{batteryId}")
    public ApiResponse<UserBatteryDTO> getBattery(
            @PathVariable Long batteryId,
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        Optional<UserBattery> battery = userBatteryService.findByIdAndUserId(batteryId, userId);
        if (!battery.isPresent()) {
            return ApiResponse.error(404, "电池不存在");
        }
        
        return ApiResponse.success(toDTO(battery.get()));
    }
    
    @PostMapping
    public ApiResponse<UserBatteryDTO> addBattery(
            @RequestBody UserBatteryDTO dto,
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        if (dto.getName() == null || dto.getName().isEmpty()) {
            return ApiResponse.error(400, "电池名称不能为空");
        }
        if (dto.getModel() == null || dto.getModel().isEmpty()) {
            return ApiResponse.error(400, "电池型号不能为空");
        }
        if (dto.getCode() == null || dto.getCode().isEmpty()) {
            return ApiResponse.error(400, "电池编码不能为空");
        }
        
        try {
            UserBattery battery = new UserBattery();
            battery.setUserId(userId);
            battery.setName(dto.getName());
            battery.setModel(dto.getModel());
            battery.setCode(dto.getCode());
            battery.setCapacity(dto.getCapacity());
            battery.setPurchaseDate(dto.getPurchaseDate());
            battery.setNotes(dto.getNotes());
            
            UserBattery saved = userBatteryService.create(battery);
            return ApiResponse.success("添加成功", toDTO(saved));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @PutMapping("/{batteryId}")
    public ApiResponse<UserBatteryDTO> updateBattery(
            @PathVariable Long batteryId,
            @RequestBody UserBatteryDTO dto,
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        try {
            UserBattery batteryData = new UserBattery();
            batteryData.setName(dto.getName());
            batteryData.setModel(dto.getModel());
            batteryData.setCode(dto.getCode());
            batteryData.setCapacity(dto.getCapacity());
            batteryData.setPurchaseDate(dto.getPurchaseDate());
            batteryData.setNotes(dto.getNotes());
            
            UserBattery updated = userBatteryService.update(batteryId, userId, batteryData);
            return ApiResponse.success("更新成功", toDTO(updated));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{batteryId}")
    public ApiResponse<Void> deleteBattery(
            @PathVariable Long batteryId,
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        try {
            userBatteryService.delete(batteryId, userId);
            return ApiResponse.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    private Long extractUserId(Long xUserId, String authHeader) {
        if (xUserId != null) {
            return xUserId;
        }
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            return null;
        }
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        }
        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        return null;
    }
    
    private UserBatteryDTO toDTO(UserBattery battery) {
        UserBatteryDTO dto = new UserBatteryDTO();
        dto.setId(battery.getId());
        dto.setName(battery.getName());
        dto.setModel(battery.getModel());
        dto.setCode(battery.getCode());
        dto.setCapacity(battery.getCapacity());
        dto.setPurchaseDate(battery.getPurchaseDate());
        dto.setNotes(battery.getNotes());
        dto.setStatus(battery.getStatus() != null ? battery.getStatus().name() : "offline");
        dto.setCreatedAt(battery.getCreatedAt());
        dto.setUpdatedAt(battery.getUpdatedAt());
        return dto;
    }
}
