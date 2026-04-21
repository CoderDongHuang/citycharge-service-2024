package com.citycharge.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UserStationDTO {
    private String id;
    private String stationId;
    private String name;
    private String type;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double distance;
    private BigDecimal rating;
    private Integer totalSwaps;
    private Integer availableBatteries;
    private Integer availableSlots;
    private String serviceTime;
    private String status;
    private String contactPhone;
    private String manager;
    private List<StationPhotoDTO> photos;
}
