package com.citycharge.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserOrderDTO {
    private String id;
    private LocalDateTime createTime;
    private String status;
    private String vehicleName;
    private String stationName;
    private String batteryInfo;
    private BigDecimal amount;
    private LocalDateTime payTime;
    private LocalDateTime completeTime;
    private String notes;
}
