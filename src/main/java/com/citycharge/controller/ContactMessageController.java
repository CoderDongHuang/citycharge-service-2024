package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.ContactMessageDTO;
import com.citycharge.dto.ContactMessageRequest;
import com.citycharge.dto.SubmitMessageResponse;
import com.citycharge.service.ContactMessageService;
import com.citycharge.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {
    
    @Autowired
    private ContactMessageService contactMessageService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/messages")
    public ApiResponse<SubmitMessageResponse> submitMessage(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ContactMessageRequest request) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        try {
            SubmitMessageResponse response = contactMessageService.submitMessage(userId, request);
            return ApiResponse.success("提交成功", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @GetMapping("/messages")
    public ApiResponse<Map<String, Object>> getMessageHistory(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        Page<ContactMessageDTO> messagePage = contactMessageService.getMessageHistory(userId, page, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("content", messagePage.getContent());
        result.put("totalElements", messagePage.getTotalElements());
        result.put("totalPages", messagePage.getTotalPages());
        result.put("currentPage", page);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/messages/{messageId}")
    public ApiResponse<ContactMessageDTO> getMessageDetail(
            @PathVariable Long messageId,
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        ContactMessageDTO message = contactMessageService.getMessageDetail(userId, messageId);
        if (message == null) {
            return ApiResponse.error(404, "留言不存在");
        }
        return ApiResponse.success(message);
    }
    
    @PostMapping("/messages/{messageId}/reply")
    public ApiResponse<ContactMessageDTO> replyMessage(
            @PathVariable Long messageId,
            @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        if (reply == null || reply.trim().isEmpty()) {
            return ApiResponse.error(400, "回复内容不能为空");
        }
        
        ContactMessageDTO message = contactMessageService.replyMessage(messageId, reply);
        if (message == null) {
            return ApiResponse.error(404, "留言不存在");
        }
        return ApiResponse.success("回复成功", message);
    }
    
    @PutMapping("/messages/{messageId}/status")
    public ApiResponse<ContactMessageDTO> updateStatus(
            @PathVariable Long messageId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ApiResponse.error(400, "状态不能为空");
        }
        
        try {
            ContactMessageDTO message = contactMessageService.updateStatus(messageId, status);
            if (message == null) {
                return ApiResponse.error(404, "留言不存在");
            }
            return ApiResponse.success("状态更新成功", message);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<Void> deleteMessage(@PathVariable Long messageId) {
        boolean deleted = contactMessageService.deleteMessage(messageId);
        if (deleted) {
            return ApiResponse.success( "删除成功", null);
        }
        return ApiResponse.error(404, "留言不存在");
    }
    
    @GetMapping("/admin/messages")
    public ApiResponse<Map<String, Object>> getAllMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<ContactMessageDTO> messagePage = contactMessageService.getAllMessages(page, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("content", messagePage.getContent());
        result.put("totalElements", messagePage.getTotalElements());
        result.put("totalPages", messagePage.getTotalPages());
        result.put("currentPage", page);
        return ApiResponse.success(result);
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
