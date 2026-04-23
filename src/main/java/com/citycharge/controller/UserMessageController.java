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
            @RequestParam(required = false) String sort) {
        
        Long userId = AuthUtil.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        UserMessageListResponseDTO result = userMessageService.getMessages(userId, page, size, category, source, isRead, sort);
        return ApiResponse.success("success", result);
    }
    
    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountDTO> getUnreadCount() {
        Long userId = AuthUtil.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        UnreadCountDTO result = userMessageService.getUnreadCount(userId);
        return ApiResponse.success("success", result);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<UserMessageDetailDTO> getMessageDetail(@PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        UserMessageDetailDTO result = userMessageService.getMessageDetail(userId, id);
        return ApiResponse.success("success", result);
    }
    
    @PutMapping("/{id}/read")
    public ApiResponse<Map<String, Object>> markAsRead(@PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        Map<String, Object> result = userMessageService.markAsRead(userId, id);
        return ApiResponse.success("操作成功", result);
    }
    
    @PutMapping("/read-batch")
    public ApiResponse<Map<String, Object>> markAsReadBatch(@RequestBody Map<String, List<Long>> request) {
        Long userId = AuthUtil.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        List<Long> messageIds = request.get("messageIds");
        Map<String, Object> result = userMessageService.markAsReadBatch(userId, messageIds);
        return ApiResponse.success("操作成功", result);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Object>> deleteMessage(@PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        Map<String, Object> result = userMessageService.deleteMessage(userId, id);
        return ApiResponse.success("删除成功", result);
    }
    
    @DeleteMapping("/batch")
    public ApiResponse<Map<String, Object>> deleteMessageBatch(@RequestBody Map<String, List<Long>> request) {
        Long userId = AuthUtil.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        List<Long> messageIds = request.get("messageIds");
        Map<String, Object> result = userMessageService.deleteMessageBatch(userId, messageIds);
        return ApiResponse.success("删除成功", result);
    }
}
