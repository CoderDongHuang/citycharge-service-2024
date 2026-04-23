package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.*;
import com.citycharge.service.UserMessageService;
import com.citycharge.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/messages")
@RequiredArgsConstructor
public class UserMessageController {
    
    private final UserMessageService userMessageService;
    
    @GetMapping
    public ApiResponse<UserMessageListResponseDTO> getMessages(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String sort,
            @RequestHeader(value = "X-User-ID", required = false) Long headerUserId) {
        
        Long userId = headerUserId;
        if (userId == null) {
            userId = AuthUtil.getCurrentUserId();
        }
        
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        if (!validateUserAccess(userId)) {
            return ApiResponse.error(403, "无权访问其他用户的消息");
        }
        
        UserMessageListResponseDTO result = userMessageService.getMessages(userId, page, size, category, source, isRead, sort);
        return ApiResponse.success("success", result);
    }
    
    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountDTO> getUnreadCount(
            @RequestHeader(value = "X-User-ID", required = false) Long headerUserId) {
        
        Long userId = headerUserId;
        if (userId == null) {
            userId = AuthUtil.getCurrentUserId();
        }
        
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        if (!validateUserAccess(userId)) {
            return ApiResponse.error(403, "无权访问其他用户的消息");
        }
        
        UnreadCountDTO result = userMessageService.getUnreadCount(userId);
        return ApiResponse.success("success", result);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<UserMessageDetailDTO> getMessageDetail(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-ID", required = false) Long headerUserId) {
        
        Long userId = headerUserId;
        if (userId == null) {
            userId = AuthUtil.getCurrentUserId();
        }
        
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        if (!validateUserAccess(userId)) {
            return ApiResponse.error(403, "无权访问其他用户的消息");
        }
        
        UserMessageDetailDTO result = userMessageService.getMessageDetail(userId, id);
        return ApiResponse.success("success", result);
    }
    
    @PutMapping("/{id}/read")
    public ApiResponse<Map<String, Object>> markAsRead(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-ID", required = false) Long headerUserId) {
        
        Long userId = headerUserId;
        if (userId == null) {
            userId = AuthUtil.getCurrentUserId();
        }
        
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        if (!validateUserAccess(userId)) {
            return ApiResponse.error(403, "无权操作其他用户的消息");
        }
        
        Map<String, Object> result = userMessageService.markAsRead(userId, id);
        return ApiResponse.success("操作成功", result);
    }
    
    @PutMapping("/read-batch")
    public ApiResponse<Map<String, Object>> markAsReadBatch(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-ID", required = false) Long headerUserId) {
        
        Long userId = headerUserId;
        if (userId == null) {
            userId = getLongFromMap(request, "userId");
        }
        if (userId == null) {
            userId = AuthUtil.getCurrentUserId();
        }
        
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        if (!validateUserAccess(userId)) {
            return ApiResponse.error(403, "无权操作其他用户的消息");
        }
        
        @SuppressWarnings("unchecked")
        List<Long> messageIds = (List<Long>) request.get("messageIds");
        Map<String, Object> result = userMessageService.markAsReadBatch(userId, messageIds);
        return ApiResponse.success("操作成功", result);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Object>> deleteMessage(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-ID", required = false) Long headerUserId) {
        
        Long userId = headerUserId;
        if (userId == null) {
            userId = AuthUtil.getCurrentUserId();
        }
        
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        if (!validateUserAccess(userId)) {
            return ApiResponse.error(403, "无权删除其他用户的消息");
        }
        
        Map<String, Object> result = userMessageService.deleteMessage(userId, id);
        return ApiResponse.success("删除成功", result);
    }
    
    @DeleteMapping("/batch")
    public ApiResponse<Map<String, Object>> deleteMessageBatch(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-ID", required = false) Long headerUserId) {
        
        Long userId = headerUserId;
        if (userId == null) {
            userId = getLongFromMap(request, "userId");
        }
        if (userId == null) {
            userId = AuthUtil.getCurrentUserId();
        }
        
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        if (!validateUserAccess(userId)) {
            return ApiResponse.error(403, "无权删除其他用户的消息");
        }
        
        @SuppressWarnings("unchecked")
        List<Long> messageIds = (List<Long>) request.get("messageIds");
        Map<String, Object> result = userMessageService.deleteMessageBatch(userId, messageIds);
        return ApiResponse.success("删除成功", result);
    }
    
    private boolean validateUserAccess(Long requestUserId) {
        Long currentUserId = AuthUtil.getCurrentUserId();
        if (currentUserId == null) {
            return true;
        }
        return currentUserId.equals(requestUserId);
    }
    
    private Long getLongFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
}
