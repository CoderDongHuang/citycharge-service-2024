package com.citycharge.service;

import com.citycharge.dto.ChangePasswordRequest;
import com.citycharge.dto.UpdateProfileRequest;
import com.citycharge.dto.UserProfileDTO;
import com.citycharge.dto.UserSettingsDTO;
import com.citycharge.entity.User;
import com.citycharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${avatar.upload.path:uploads/avatars}")
    private String avatarUploadPath;
    
    public UserProfileDTO getProfile(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return null;
        }
        User user = userOpt.get();
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
    
    public UserProfileDTO updateProfile(Long userId, UpdateProfileRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return null;
        }
        User user = userOpt.get();
        
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            if (!request.getUsername().equals(user.getUsername()) && 
                userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
            user.setUsername(request.getUsername());
        }
        
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!request.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        userRepository.save(user);
        
        return getProfile(userId);
    }
    
    public boolean changePassword(Long userId, ChangePasswordRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        User user = userOpt.get();
        
        String currentPasswordMd5 = md5(request.getCurrentPassword());
        if (!currentPasswordMd5.equals(user.getPassword())) {
            throw new RuntimeException("当前密码错误");
        }
        
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new RuntimeException("新密码长度至少6位");
        }
        
        user.setPassword(md5(request.getNewPassword()));
        userRepository.save(user);
        return true;
    }
    
    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return null;
        }
        User user = userOpt.get();
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String newFilename = "avatar_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        
        Path uploadPath = Paths.get(avatarUploadPath).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(newFilename);
        file.transferTo(filePath.toFile());
        
        String avatarUrl = "/avatars/" + newFilename;
        user.setAvatar(avatarUrl);
        userRepository.save(user);
        
        return avatarUrl;
    }
    
    public UserSettingsDTO getSettings(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return null;
        }
        User user = userOpt.get();
        UserSettingsDTO dto = new UserSettingsDTO();
        dto.setNotifications(user.getNotifications());
        dto.setDarkMode(user.getDarkMode());
        return dto;
    }
    
    public UserSettingsDTO updateSettings(Long userId, UserSettingsDTO settings) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return null;
        }
        User user = userOpt.get();
        
        if (settings.getNotifications() != null) {
            user.setNotifications(settings.getNotifications());
        }
        if (settings.getDarkMode() != null) {
            user.setDarkMode(settings.getDarkMode());
        }
        
        userRepository.save(user);
        return getSettings(userId);
    }
    
    private String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
