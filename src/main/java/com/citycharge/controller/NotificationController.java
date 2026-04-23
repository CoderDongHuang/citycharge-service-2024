package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.DingtalkRequestDTO;
import com.citycharge.dto.EmailRequestDTO;
import com.citycharge.service.DingtalkService;
import com.citycharge.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final EmailService emailService;
    private final DingtalkService dingtalkService;
    
    @PostMapping("/email/send")
    public ApiResponse<Map<String, Object>> sendEmail(@RequestBody EmailRequestDTO request) {
        log.info("收到邮件发送请求 - 收件人: {}, 主题: {}", request.getTo(), request.getSubject());
        
        if (request.getTo() == null || request.getTo().isEmpty()) {
            return ApiResponse.error("收件人地址不能为空");
        }
        if (request.getSubject() == null || request.getSubject().isEmpty()) {
            return ApiResponse.error("邮件主题不能为空");
        }
        if (request.getContent() == null || request.getContent().isEmpty()) {
            return ApiResponse.error("邮件内容不能为空");
        }
        
        boolean success;
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            success = emailService.sendEmailWithCategory(
                request.getTo(),
                request.getSubject(),
                request.getContent(),
                request.getCategory()
            );
        } else {
            success = emailService.sendEmail(
                request.getTo(),
                request.getSubject(),
                request.getContent()
            );
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "邮件发送成功" : "邮件发送失败");
        
        if (success) {
            return ApiResponse.success("邮件发送成功", result);
        } else {
            return ApiResponse.error("邮件发送失败");
        }
    }
    
    @PostMapping("/dingtalk/send")
    public ApiResponse<Map<String, Object>> sendDingtalk(@RequestBody DingtalkRequestDTO request) {
        log.info("收到钉钉消息发送请求");
        
        if (request.getWebhook() == null || request.getWebhook().isEmpty()) {
            return ApiResponse.error("webhook地址不能为空");
        }
        if (request.getPayload() == null || request.getPayload().isEmpty()) {
            return ApiResponse.error("消息内容不能为空");
        }
        
        boolean success = dingtalkService.sendDingtalk(request.getWebhook(), request.getPayload());
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("errcode", success ? 0 : -1);
        result.put("errmsg", success ? "ok" : "发送失败");
        
        if (success) {
            return ApiResponse.success("钉钉消息发送成功", result);
        } else {
            return ApiResponse.error("钉钉消息发送失败");
        }
    }
    
    @PostMapping("/dingtalk/text")
    public ApiResponse<Map<String, Object>> sendDingtalkText(
            @RequestParam String webhook,
            @RequestParam String content) {
        
        log.info("收到钉钉文本消息发送请求");
        
        if (webhook == null || webhook.isEmpty()) {
            return ApiResponse.error("webhook地址不能为空");
        }
        if (content == null || content.isEmpty()) {
            return ApiResponse.error("消息内容不能为空");
        }
        
        boolean success = dingtalkService.sendTextMessage(webhook, content);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("errcode", success ? 0 : -1);
        result.put("errmsg", success ? "ok" : "发送失败");
        
        if (success) {
            return ApiResponse.success("钉钉消息发送成功", result);
        } else {
            return ApiResponse.error("钉钉消息发送失败");
        }
    }
    
    @PostMapping("/dingtalk/markdown")
    public ApiResponse<Map<String, Object>> sendDingtalkMarkdown(
            @RequestParam String webhook,
            @RequestParam String title,
            @RequestParam String text) {
        
        log.info("收到钉钉Markdown消息发送请求 - 标题: {}", title);
        
        if (webhook == null || webhook.isEmpty()) {
            return ApiResponse.error("webhook地址不能为空");
        }
        if (title == null || title.isEmpty()) {
            return ApiResponse.error("消息标题不能为空");
        }
        if (text == null || text.isEmpty()) {
            return ApiResponse.error("消息内容不能为空");
        }
        
        boolean success = dingtalkService.sendMarkdownMessage(webhook, title, text);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("errcode", success ? 0 : -1);
        result.put("errmsg", success ? "ok" : "发送失败");
        
        if (success) {
            return ApiResponse.success("钉钉消息发送成功", result);
        } else {
            return ApiResponse.error("钉钉消息发送失败");
        }
    }
}
