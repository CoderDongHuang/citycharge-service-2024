package com.citycharge.dto;

import lombok.Data;
import java.util.List;

@Data
public class StationListResponseDTO {
    private List<UserStationDTO> content;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
