package com.citycharge.dto;

import lombok.Data;

@Data
public class CountDTO {
    private long count;
    
    public CountDTO(long count) {
        this.count = count;
    }
}
