package com.citycharge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SwapCompleteMessage {
    
    @JsonProperty("user_id")
    private Long userId;
    
    @JsonProperty("vehicle_id")
    private Long vehicleId;
    
    @JsonProperty("battery_id")
    private Long batteryId;
    
    @JsonProperty("station_id")
    private Long stationId;
    
    @JsonProperty("station_name")
    private String stationName;
    
    @JsonProperty("old_battery_level")
    private Integer oldBatteryLevel;
    
    @JsonProperty("new_battery_level")
    private Integer newBatteryLevel;
    
    @JsonProperty("swap_time")
    private String swapTime;
    
    @JsonProperty("estimated_range")
    private Integer estimatedRange;
    
    @JsonProperty("amount")
    private Double amount;
    
    @JsonProperty("order_id")
    private String orderId;
    
    @JsonProperty("vehicle_plate")
    private String vehiclePlate;
    
    @JsonProperty("battery_model")
    private String batteryModel;
}
