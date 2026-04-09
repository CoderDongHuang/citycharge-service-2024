package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.UserVehicleDTO;
import com.citycharge.dto.UserVehicleStatusDTO;
import com.citycharge.entity.UserVehicle;
import com.citycharge.service.UserVehicleService;
import com.citycharge.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class UserVehicleController {
    
    private final UserVehicleService userVehicleService;
    private final JwtUtil jwtUtil;
    
    @GetMapping
    public ApiResponse<List<UserVehicleDTO>> getVehicles(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        List<UserVehicle> vehicles = userVehicleService.findByUserId(userId);
        List<UserVehicleDTO> dtos = vehicles.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        return ApiResponse.success(dtos);
    }
    
    @GetMapping("/{vehicleId}")
    public ApiResponse<UserVehicleDTO> getVehicle(
            @PathVariable Long vehicleId,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        Optional<UserVehicle> vehicle = userVehicleService.findByIdAndUserId(vehicleId, userId);
        if (!vehicle.isPresent()) {
            return ApiResponse.error(404, "车辆不存在");
        }
        
        return ApiResponse.success(toDTO(vehicle.get()));
    }
    
    @PostMapping
    public ApiResponse<UserVehicleDTO> addVehicle(
            @RequestBody UserVehicleDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        if (dto.getName() == null || dto.getName().isEmpty()) {
            return ApiResponse.error(400, "车辆名称不能为空");
        }
        if (dto.getBrand() == null || dto.getBrand().isEmpty()) {
            return ApiResponse.error(400, "品牌型号不能为空");
        }
        if (dto.getVin() == null || dto.getVin().length() != 17) {
            return ApiResponse.error(400, "车架号必须为17位");
        }
        
        try {
            UserVehicle vehicle = new UserVehicle();
            vehicle.setUserId(userId);
            vehicle.setName(dto.getName());
            vehicle.setBrand(dto.getBrand());
            vehicle.setVin(dto.getVin());
            vehicle.setPlateNumber(dto.getPlateNumber());
            vehicle.setPurchaseDate(dto.getPurchaseDate());
            vehicle.setNotes(dto.getNotes());
            
            UserVehicle saved = userVehicleService.create(vehicle);
            return ApiResponse.success(toDTO(saved));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @PutMapping("/{vehicleId}")
    public ApiResponse<UserVehicleDTO> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody UserVehicleDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        try {
            UserVehicle vehicleData = new UserVehicle();
            vehicleData.setName(dto.getName());
            vehicleData.setBrand(dto.getBrand());
            vehicleData.setVin(dto.getVin());
            vehicleData.setPlateNumber(dto.getPlateNumber());
            vehicleData.setPurchaseDate(dto.getPurchaseDate());
            vehicleData.setNotes(dto.getNotes());
            
            UserVehicle updated = userVehicleService.update(vehicleId, userId, vehicleData);
            return ApiResponse.success(toDTO(updated));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{vehicleId}")
    public ApiResponse<Void> deleteVehicle(
            @PathVariable Long vehicleId,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        try {
            userVehicleService.delete(vehicleId, userId);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @GetMapping("/{vehicleId}/status")
    public ApiResponse<UserVehicleStatusDTO> getVehicleStatus(
            @PathVariable Long vehicleId,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        Optional<UserVehicle> vehicle = userVehicleService.findByIdAndUserId(vehicleId, userId);
        if (!vehicle.isPresent()) {
            return ApiResponse.error(404, "车辆不存在");
        }
        
        UserVehicle v = vehicle.get();
        UserVehicleStatusDTO statusDTO = new UserVehicleStatusDTO();
        statusDTO.setId(v.getId());
        statusDTO.setStatus(v.getStatus() != null ? v.getStatus().name() : "offline");
        statusDTO.setLastOnlineTime(v.getLastOnlineTime());
        statusDTO.setBatteryLevel(v.getBatteryLevel());
        
        if (v.getLatitude() != null && v.getLongitude() != null) {
            UserVehicleStatusDTO.Location location = new UserVehicleStatusDTO.Location();
            location.setLatitude(v.getLatitude());
            location.setLongitude(v.getLongitude());
            statusDTO.setLocation(location);
        }
        
        return ApiResponse.success(statusDTO);
    }
    
    private Long extractUserId(String authHeader) {
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
        return (Long) userIdObj;
    }
    
    private UserVehicleDTO toDTO(UserVehicle vehicle) {
        UserVehicleDTO dto = new UserVehicleDTO();
        dto.setId(vehicle.getId());
        dto.setName(vehicle.getName());
        dto.setBrand(vehicle.getBrand());
        dto.setVin(vehicle.getVin());
        dto.setPlateNumber(vehicle.getPlateNumber());
        dto.setPurchaseDate(vehicle.getPurchaseDate());
        dto.setNotes(vehicle.getNotes());
        dto.setStatus(vehicle.getStatus() != null ? vehicle.getStatus().name() : "offline");
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        return dto;
    }
}
