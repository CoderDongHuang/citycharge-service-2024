package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserMessageDTO {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String source;
    private String sourceType;
    private Integer priority;
    private Boolean isRead;
    private LocalDateTime readTime;
    private LocalDateTime createTime;
    private Long vehicleId;
    private String vehicleNo;
    private Long batteryId;
    private Long stationId;
    private String extraData;
    private String adminName;
}
