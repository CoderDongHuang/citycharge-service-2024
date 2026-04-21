package com.citycharge.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateStationDTO {
    private String name;
    private String type;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String contactPhone;
    private String manager;
    private String serviceTime;
    private Integer availableBatteries;
    private Integer availableSlots;
    private String status;
    private List<String> photoUrls;
}
