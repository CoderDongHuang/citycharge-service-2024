package com.citycharge.dto;

import lombok.Data;

@Data
public class OrderStatsDTO {
    private long total;
    private long completed;
    private long processing;
    private long pending;
}
