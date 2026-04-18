package com.citycharge.dto;

public class SubmitMessageResponse {
    private Long messageId;
    private String status;

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
