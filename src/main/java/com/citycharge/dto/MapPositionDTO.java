package com.citycharge.dto;

import lombok.Data;

@Data
public class MapPositionDTO {
    private Integer x;
    private Integer y;
    private Integer type; // 0:道路, 1:换电站, 2:障碍
    private String stationId;
}