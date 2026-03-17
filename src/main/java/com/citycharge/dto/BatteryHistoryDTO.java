package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BatteryHistoryDTO {
    private String pid;
    private String vid;
    private Double voltage;
    private Double temperature;
    private Double capacityPercentage;
    private LocalDateTime recordTime;
}