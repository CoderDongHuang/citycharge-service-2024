package com.citycharge.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StationServiceDTO {
    private String name;
    private BigDecimal price;
    private String duration;
}
