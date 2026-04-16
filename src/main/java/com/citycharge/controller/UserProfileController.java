package com.citycharge.controller;

import com.citycharge.dto.ChangePasswordRequest;
import com.citycharge.dto.UpdateProfileRequest;
import com.citycharge.dto.UserProfileDTO;
import com.citycharge.dto.UserSettingsDTO;
import com.citycharge.service.UserProfileService;
import com.citycharge.common.ApiResponse;
import com.citycharge.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserProfileController {
    
    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/profile")
    public ApiResponse<UserProfileDTO> getProfile(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        UserProfileDTO profile = userProfileService.getProfile(userId);
        if (profile == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(profile);
    }
    
    @PutMapping("/profile")
    public ApiResponse<UserProfileDTO> updateProfile(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UpdateProfileRequest request) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        try {
            UserProfileDTO profile = userProfileService.updateProfile(userId, request);
            if (profile == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            return ApiResponse.success("更新成功", profile);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PutMapping("/profile/password")
    public ApiResponse<Void> changePassword(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ChangePasswordRequest request) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        try {
            boolean success = userProfileService.changePassword(userId, request);
            if (success) {
                return ApiResponse.success("密码修改成功", null);
            }
            return ApiResponse.error(400, "密码修改失败");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/profile/avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("avatar") MultipartFile file) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        if (file.isEmpty()) {
            return ApiResponse.error(400, "请选择要上传的文件");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && 
            !contentType.equals("image/png") && !contentType.equals("image/gif"))) {
            return ApiResponse.error(400, "只支持 JPG/PNG/GIF 格式的图片");
        }
        
        try {
            String avatarUrl = userProfileService.uploadAvatar(userId, file);
            if (avatarUrl == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            Map<String, String> data = new HashMap<>();
            data.put("avatar", avatarUrl);
            return ApiResponse.success("上传成功", data);
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/settings")
    public ApiResponse<UserSettingsDTO> getSettings(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        UserSettingsDTO settings = userProfileService.getSettings(userId);
        if (settings == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(settings);
    }
    
    @PutMapping("/settings")
    public ApiResponse<UserSettingsDTO> updateSettings(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UserSettingsDTO settings) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        UserSettingsDTO updatedSettings = userProfileService.updateSettings(userId, settings);
        if (updatedSettings == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success("设置已更新", updatedSettings);
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
