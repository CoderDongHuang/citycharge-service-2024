package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.AiChatRequest;
import com.citycharge.dto.AiChatResponse;
import com.citycharge.service.AiChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiChatController {
    
    private final AiChatService aiChatService;
    
    @PostMapping("/chat")
    public ApiResponse<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        try {
            if (!aiChatService.isConfigured()) {
                return ApiResponse.error(503, "AI 服务未配置，请联系管理员");
            }
            
            if (request.getMessages() == null || request.getMessages().isEmpty()) {
                return ApiResponse.error("消息不能为空");
            }
            
            log.info("AI chat request - messages count: {}", request.getMessages().size());
            
            AiChatResponse response = aiChatService.chat(request);
            
            return ApiResponse.success("success", response);
            
        } catch (RuntimeException e) {
            log.error("AI chat error: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in AI chat: {}", e.getMessage(), e);
            return ApiResponse.error("AI 服务异常，请稍后重试");
        }
    }
    
    @GetMapping("/status")
    public ApiResponse<StatusResponse> status() {
        StatusResponse response = new StatusResponse();
        response.setConfigured(aiChatService.isConfigured());
        response.setService("DeepSeek AI");
        return ApiResponse.success(response);
    }
    
    @lombok.Data
    public static class StatusResponse {
        private Boolean configured;
        private String service;
    }
}
