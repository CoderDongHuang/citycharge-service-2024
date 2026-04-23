package com.citycharge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DingtalkService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public boolean sendDingtalk(String webhook, String payloadJson) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(webhook);
            
            StringEntity entity = new StringEntity(payloadJson, StandardCharsets.UTF_8);
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");
            
            post.setEntity(entity);
            post.setHeader("Content-Type", "application/json; charset=UTF-8");
            
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("钉钉消息发送响应: {}", result);
                
                return result.contains("\"errcode\":0");
            }
        } catch (Exception e) {
            log.error("钉钉消息发送失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    public boolean sendDingtalk(String webhook, Map<String, Object> payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            return sendDingtalk(webhook, payloadJson);
        } catch (Exception e) {
            log.error("钉钉消息序列化失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    public boolean sendTextMessage(String webhook, String content) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("msgtype", "text");
        
        Map<String, Object> text = new HashMap<>();
        text.put("content", content);
        payload.put("text", text);
        
        return sendDingtalk(webhook, payload);
    }
    
    public boolean sendMarkdownMessage(String webhook, String title, String text) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("msgtype", "markdown");
        
        Map<String, Object> markdown = new HashMap<>();
        markdown.put("title", title);
        markdown.put("text", text);
        payload.put("markdown", markdown);
        
        return sendDingtalk(webhook, payload);
    }
    
    public boolean sendLinkMessage(String webhook, String title, String text, String messageUrl, String picUrl) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("msgtype", "link");
        
        Map<String, Object> link = new HashMap<>();
        link.put("title", title);
        link.put("text", text);
        link.put("messageUrl", messageUrl);
        link.put("picUrl", picUrl != null ? picUrl : "");
        payload.put("link", link);
        
        return sendDingtalk(webhook, payload);
    }
}
