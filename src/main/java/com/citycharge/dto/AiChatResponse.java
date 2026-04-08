package com.citycharge.dto;

import lombok.Data;

@Data
public class AiChatResponse {
    private String content;
    private String model;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
}
