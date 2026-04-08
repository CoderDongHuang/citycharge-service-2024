package com.citycharge.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiChatRequest {
    private List<Message> messages;
    private String model = "deepseek-chat";
    private Double temperature = 0.7;
    private Integer maxTokens = 2000;
    
    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
