package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.entity.User;
import com.citycharge.repository.UserRepository;
import com.citycharge.util.JwtUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            
            if (username == null || username.isEmpty()) {
                return ApiResponse.error("用户名不能为空");
            }
            
            if (password == null || password.isEmpty()) {
                return ApiResponse.error("密码不能为空");
            }
            
            Optional<User> userOpt = userRepository.findByUsernameOrEmail(username);
            
            if (!userOpt.isPresent()) {
                return ApiResponse.error("用户不存在");
            }
            
            User user = userOpt.get();
            
            if (user.getStatus() != null && user.getStatus() == 0) {
                return ApiResponse.error("账号已被禁用");
            }
            
            if (!verifyPassword(password, user.getPassword())) {
                return ApiResponse.error("密码错误");
            }
            
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
            
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setRefreshToken(refreshToken);
            response.setExpiresIn(jwtUtil.getExpiration() / 1000);
            response.setUser(buildUserInfo(user));
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ApiResponse.success(null);
    }
    
    @GetMapping("/user")
    public ApiResponse<UserInfo> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ApiResponse.error("未授权");
            }
            
            Long userId = jwtUtil.getUserId(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ApiResponse.error("用户不存在");
            }
            
            User user = userOpt.get();
            return ApiResponse.success(buildUserInfo(user));
            
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/refresh")
    public ApiResponse<RefreshResponse> refreshToken(@RequestBody RefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ApiResponse.error("刷新Token不能为空");
            }
            
            Claims claims = jwtUtil.parseToken(refreshToken);
            if (claims == null || !"refresh".equals(claims.get("type"))) {
                return ApiResponse.error("无效的刷新Token");
            }
            
            Long userId = jwtUtil.getUserId(refreshToken);
            String username = jwtUtil.getUsername(refreshToken);
            
            if (userId == null || username == null) {
                return ApiResponse.error("无效的刷新Token");
            }
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ApiResponse.error("用户不存在");
            }
            
            User user = userOpt.get();
            String newToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
            
            RefreshResponse response = new RefreshResponse();
            response.setToken(newToken);
            response.setRefreshToken(newRefreshToken);
            response.setExpiresIn(jwtUtil.getExpiration() / 1000);
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("刷新Token失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@RequestBody RegisterRequest request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String email = request.getEmail();
            
            if (username == null || username.isEmpty()) {
                return ApiResponse.error("用户名不能为空");
            }
            
            if (username.length() < 3 || username.length() > 20) {
                return ApiResponse.error("用户名长度必须在 3-20 之间");
            }
            
            if (password == null || password.isEmpty()) {
                return ApiResponse.error("密码不能为空");
            }
            
            if (password.length() < 6 || password.length() > 20) {
                return ApiResponse.error("密码长度必须在 6-20 之间");
            }
            
            if (email == null || email.isEmpty()) {
                return ApiResponse.error("邮箱不能为空");
            }
            
            if (!isValidEmail(email)) {
                return ApiResponse.error("邮箱格式不正确");
            }
            
            if (userRepository.existsByUsername(username)) {
                return ApiResponse.error("用户名已存在");
            }
            
            if (userRepository.existsByEmail(email)) {
                return ApiResponse.error("邮箱已被注册");
            }
            
            User user = new User();
            user.setUsername(username);
            user.setPassword(hashPassword(password));
            user.setEmail(email);
            user.setRole("user");
            user.setStatus(1);
            
            userRepository.save(user);
            
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
            
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setRefreshToken(refreshToken);
            response.setExpiresIn(jwtUtil.getExpiration() / 1000);
            response.setUser(buildUserInfo(user));
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("注册失败：" + e.getMessage());
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
    
    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        String hashedInput = hashPassword(rawPassword);
        return hashedInput.equals(encodedPassword);
    }
    
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }
    
    private UserInfo buildUserInfo(User user) {
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setEmail(user.getEmail());
        info.setRole(user.getRole());
        info.setAvatar(user.getAvatar());
        info.setStatus(user.getStatus());
        info.setLastLogin(user.getLastLogin() != null ? user.getLastLogin().format(FORMATTER) : null);
        info.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().format(FORMATTER) : null);
        return info;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class LoginRequest {
        @JsonProperty("username")
        private String username;
        @JsonProperty("password")
        private String password;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class RegisterRequest {
        @JsonProperty("username")
        private String username;
        @JsonProperty("password")
        private String password;
        @JsonProperty("email")
        private String email;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class RefreshRequest {
        @JsonProperty("refreshToken")
        private String refreshToken;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class LoginResponse {
        @JsonProperty("token")
        private String token;
        @JsonProperty("refreshToken")
        private String refreshToken;
        @JsonProperty("expiresIn")
        private Long expiresIn;
        @JsonProperty("user")
        private UserInfo user;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class RefreshResponse {
        @JsonProperty("token")
        private String token;
        @JsonProperty("refreshToken")
        private String refreshToken;
        @JsonProperty("expiresIn")
        private Long expiresIn;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class UserInfo {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("username")
        private String username;
        @JsonProperty("email")
        private String email;
        @JsonProperty("role")
        private String role;
        @JsonProperty("avatar")
        private String avatar;
        @JsonProperty("status")
        private Integer status;
        @JsonProperty("lastLogin")
        private String lastLogin;
        @JsonProperty("createdAt")
        private String createdAt;
    }
}
