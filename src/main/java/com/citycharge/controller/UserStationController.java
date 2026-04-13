package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.UserStationDTO;
import com.citycharge.service.UserStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user/stations")
@RequiredArgsConstructor
public class UserStationController {
    
    private final UserStationService userStationService;
    
    @GetMapping
    public ApiResponse<List<UserStationDTO>> getStations(
            @RequestParam(required = false) String status) {
        
        List<UserStationDTO> stations;
        if (status != null && !status.isEmpty()) {
            stations = userStationService.getByStatus(status);
        } else {
            stations = userStationService.getAllStations();
        }
        
        return ApiResponse.success(stations);
    }
    
    @GetMapping("/{stationId}")
    public ApiResponse<UserStationDTO> getStation(@PathVariable String stationId) {
        Optional<UserStationDTO> station = userStationService.getByStationId(stationId);
        if (!station.isPresent()) {
            return ApiResponse.error(404, "站点不存在");
        }
        return ApiResponse.success(station.get());
    }
    
    @GetMapping("/nearby")
    public ApiResponse<List<UserStationDTO>> getNearbyStations(
            @RequestParam Integer x,
            @RequestParam Integer y,
            @RequestParam(required = false, defaultValue = "100") Integer radius) {
        
        List<UserStationDTO> stations = userStationService.getNearbyStations(x, y, radius);
        return ApiResponse.success(stations);
    }
    
    @GetMapping("/available")
    public ApiResponse<List<UserStationDTO>> getAvailableStations(
            @RequestParam(required = false, defaultValue = "1") Integer minBatteries) {
        
        List<UserStationDTO> stations = userStationService.getAvailableStations(minBatteries);
        return ApiResponse.success(stations);
    }
}
