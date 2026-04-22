package com.citycharge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SendMessageRequestDTO {
    private String title;
    private String content;
    private String category;
    private String targetType;
    private java.util.List<Long> targetIds;
    private Integer priority;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT+8")
    private LocalDateTime scheduledTime;
    
    private Map<String, Object> extraData;
}
