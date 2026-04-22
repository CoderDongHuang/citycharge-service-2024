package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminMessageDetailDTO {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String source;
    private String targetType;
    private java.util.List<Long> targetIds;
    private Integer priority;
    private String sendStatus;
    private LocalDateTime scheduledTime;
    private LocalDateTime sentTime;
    private Long adminId;
    private String adminName;
    private Long recipientCount;
    private Long readCount;
    private String extraData;
    private LocalDateTime createTime;
}
