package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SendMessageRequestDTO {
    private String title;
    private String content;
    private String category;
    private String targetType;
    private java.util.List<Long> targetIds;
    private Integer priority;
    private LocalDateTime scheduledTime;
    private String extraData;
}
