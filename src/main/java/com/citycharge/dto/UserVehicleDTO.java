package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserVehicleDTO {
    private Long id;
    private String name;
    private String brand;
    private String vin;
    private String plateNumber;
    private LocalDate purchaseDate;
    private String notes;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
