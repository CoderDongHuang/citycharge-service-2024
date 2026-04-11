package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserBatteryDTO {
    private Long id;
    private String name;
    private String model;
    private String code;
    private Integer capacity;
    private LocalDate purchaseDate;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
