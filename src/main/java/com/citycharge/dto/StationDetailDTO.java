package com.citycharge.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class StationDetailDTO {
    private String id;
    private String stationId;
    private String name;
    private String type;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phone;
    private String serviceTime;
    private BigDecimal rating;
    private Integer totalSwaps;
    private Integer availableBatteries;
    private Integer availableSlots;
    private String status;
    private List<String> facilities;
    private List<StationPhotoDTO> photos;
    private List<StationServiceDTO> services;
}
