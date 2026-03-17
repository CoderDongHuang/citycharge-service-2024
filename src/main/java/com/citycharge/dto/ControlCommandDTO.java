package com.citycharge.dto;

import lombok.Data;

@Data
public class ControlCommandDTO {
    private String vid;
    private String commandType; // HEADLIGHT_FLASH, HORN_BEEP
    private Integer duration;
    private Integer times;
}