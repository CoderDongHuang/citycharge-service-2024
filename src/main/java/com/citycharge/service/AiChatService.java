package com.citycharge.service;

import com.citycharge.dto.AiChatRequest;
import com.citycharge.dto.AiChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiChatService {
    
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String SYSTEM_PROMPT = "你是 CitySwap 智能客服助手，专门解答关于电池换电、会员制度、费用支付等问题。请友好、专业地回答用户问题。";
    
    @Value("${deepseek.api-key:}")
    private String apiKey;
    
    @Value("${deepseek.model:deepseek-chat}")
    private String model;
    
    @Value("${deepseek.max-tokens:2000}")
    private Integer maxTokens;
    
    @Value("${deepseek.temperature:0.7}")
    private Double temperature;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public AiChatService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    public AiChatResponse chat(AiChatRequest request) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                throw new RuntimeException("DeepSeek API Key 未配置");
            }
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", SYSTEM_PROMPT);
            messages.add(systemMessage);
            
            if (request.getMessages() != null) {
                for (AiChatRequest.Message msg : request.getMessages()) {
                    Map<String, String> message = new HashMap<>();
                    message.put("role", msg.getRole());
                    message.put("content", msg.getContent());
                    messages.add(message);
                }
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);
            requestBody.put("stream", false);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            log.info("Sending request to DeepSeek API");
            
            ResponseEntity<String> response = restTemplate.exchange(
                DEEPSEEK_API_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseResponse(response.getBody());
            } else {
                throw new RuntimeException("DeepSeek API 调用失败: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error calling DeepSeek API: {}", e.getMessage(), e);
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage());
        }
    }
    
    private AiChatResponse parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            
            if (choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                String content = firstChoice.path("message").path("content").asText();
                
                JsonNode usage = root.path("usage");
                
                AiChatResponse response = new AiChatResponse();
                response.setContent(content);
                response.setModel(root.path("model").asText());
                response.setPromptTokens(usage.path("prompt_tokens").asInt());
                response.setCompletionTokens(usage.path("completion_tokens").asInt());
                response.setTotalTokens(usage.path("total_tokens").asInt());
                
                log.info("DeepSeek API response - tokens: {}", response.getTotalTokens());
                
                return response;
            } else {
                throw new RuntimeException("DeepSeek API 返回格式错误");
            }
        } catch (Exception e) {
            log.error("Error parsing DeepSeek response: {}", e.getMessage());
            throw new RuntimeException("解析 AI 响应失败: " + e.getMessage());
        }
    }
    
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
