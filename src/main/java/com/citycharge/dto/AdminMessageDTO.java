package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminMessageDTO {
    private Long id;
    private String title;
    private String category;
    private String source;
    private String targetType;
    private Long recipientCount;
    private Integer priority;
    private String sendStatus;
    private String adminName;
    private LocalDateTime createTime;
    private LocalDateTime sentTime;
}
