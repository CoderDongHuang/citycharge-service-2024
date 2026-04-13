package com.citycharge.dto;

import lombok.Data;

@Data
public class UserStationDTO {
    private Long id;
    private String stationId;
    private String name;
    private Integer positionX;
    private Integer positionY;
    private String address;
    private Integer batteryCapacity;
    private Integer availableBatteries;
    private String status;
    private String operatingHours;
    private String contactPhone;
    private String manager;
    private Double distance;
}
