package com.citycharge.dto;

import lombok.Data;
import java.util.Map;

@Data
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String content;
    private String category;
}
