package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.*;
import com.citycharge.service.AdminMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
public class AdminMessageController {
    
    private final AdminMessageService adminMessageService;
    
    @PostMapping("/send")
    public ApiResponse<SendMessageResponseDTO> sendMessage(
            @RequestBody SendMessageRequestDTO request,
            @RequestParam Long adminId,
            @RequestParam String adminName) {
        
        SendMessageResponseDTO result = adminMessageService.sendMessage(request, adminId, adminName);
        return ApiResponse.success("消息发送成功", result);
    }
    
    @GetMapping
    public ApiResponse<Map<String, Object>> getMessages(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String sendStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        Map<String, Object> result = adminMessageService.getMessages(page, size, category, source, sendStatus, startTime, endTime);
        return ApiResponse.success("success", result);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<AdminMessageDetailDTO> getMessageDetail(@PathVariable Long id) {
        AdminMessageDetailDTO result = adminMessageService.getMessageDetail(id);
        return ApiResponse.success("success", result);
    }
    
    @PutMapping("/{id}/cancel")
    public ApiResponse<Map<String, Object>> cancelMessage(@PathVariable Long id) {
        Map<String, Object> result = adminMessageService.cancelMessage(id);
        return ApiResponse.success("已取消发送", result);
    }
    
    @PostMapping("/{id}/resend")
    public ApiResponse<SendMessageResponseDTO> resendMessage(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestParam String adminName) {
        
        SendMessageResponseDTO result = adminMessageService.resendMessage(id, adminId, adminName);
        return ApiResponse.success("重新发送成功", result);
    }
}
