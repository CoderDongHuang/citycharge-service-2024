package com.citycharge.dto;

import lombok.Data;
import java.util.Map;

@Data
public class UnreadCountDTO {
    private Long totalCount;
    private Map<String, Long> byCategory;
    private Map<String, Long> bySource;
}
