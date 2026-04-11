package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.CountDTO;
import com.citycharge.dto.UserStatsDTO;
import com.citycharge.repository.UserVehicleRepository;
import com.citycharge.repository.UserBatteryRepository;
import com.citycharge.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/stats")
@RequiredArgsConstructor
public class UserStatsController {
    
    private final UserVehicleRepository userVehicleRepository;
    private final UserBatteryRepository userBatteryRepository;
    private final JwtUtil jwtUtil;
    
    @GetMapping("/summary")
    public ApiResponse<UserStatsDTO> getSummary(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        UserStatsDTO stats = new UserStatsDTO();
        stats.setVehicles(userVehicleRepository.findByUserId(userId).size());
        stats.setBatteries(userBatteryRepository.countByUserId(userId));
        
        return ApiResponse.success(stats);
    }
    
    @GetMapping("/vehicles/count")
    public ApiResponse<CountDTO> getVehiclesCount(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        long count = userVehicleRepository.findByUserId(userId).size();
        return ApiResponse.success(new CountDTO(count));
    }
    
    @GetMapping("/batteries/count")
    public ApiResponse<CountDTO> getBatteriesCount(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        long count = userBatteryRepository.countByUserId(userId);
        return ApiResponse.success(new CountDTO(count));
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
}
