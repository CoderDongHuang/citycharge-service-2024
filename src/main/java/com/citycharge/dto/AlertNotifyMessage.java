package com.citycharge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlertNotifyMessage {
    
    @JsonProperty("user_id")
    private Long userId;
    
    @JsonProperty("vehicle_id")
    private Long vehicleId;
    
    @JsonProperty("battery_id")
    private Long batteryId;
    
    @JsonProperty("alert_type")
    private String alertType;
    
    @JsonProperty("alert_level")
    private String alertLevel;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("trigger_value")
    private Double triggerValue;
    
    @JsonProperty("threshold_value")
    private Double thresholdValue;
    
    @JsonProperty("voltage")
    private Double voltage;
    
    @JsonProperty("temperature")
    private Double temperature;
    
    @JsonProperty("battery_level")
    private Integer batteryLevel;
    
    @JsonProperty("latitude")
    private Double latitude;
    
    @JsonProperty("longitude")
    private Double longitude;
    
    @JsonProperty("location_name")
    private String locationName;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("vehicle_plate")
    private String vehiclePlate;
    
    @JsonProperty("battery_model")
    private String batteryModel;
}
