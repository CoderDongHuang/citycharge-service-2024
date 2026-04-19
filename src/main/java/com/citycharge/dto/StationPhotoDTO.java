package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StationPhotoDTO {
    private String id;
    private String url;
    private String type;
    private LocalDateTime uploadTime;
    private String description;
}
