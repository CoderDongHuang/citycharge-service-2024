package com.citycharge.dto;

import lombok.Data;
import java.util.Map;

@Data
public class DingtalkRequestDTO {
    private String webhook;
    private Map<String, Object> payload;
}
