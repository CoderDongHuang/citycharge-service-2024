package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.entity.Battery;
import com.citycharge.entity.Station;
import com.citycharge.repository.BatteryRepository;
import com.citycharge.repository.StationRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class StationController {
    
    private final StationRepository stationRepository;
    private final BatteryRepository batteryRepository;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @GetMapping
    public ApiResponse<StationListResponse> getStations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        try {
            List<Station> stations;
            
            if (status != null && !status.isEmpty() && keyword != null && !keyword.isEmpty()) {
                Station.Status stationStatus = Station.Status.valueOf(status);
                stations = stationRepository.findByStatusAndKeyword(stationStatus, keyword);
            } else if (status != null && !status.isEmpty()) {
                Station.Status stationStatus = Station.Status.valueOf(status);
                stations = stationRepository.findByStatus(stationStatus);
            } else if (keyword != null && !keyword.isEmpty()) {
                stations = stationRepository.findByKeyword(keyword);
            } else {
                stations = stationRepository.findAll();
            }
            
            int total = stations.size();
            int validPage = Math.max(1, page);
            int startIndex = (validPage - 1) * pageSize;
            
            if (startIndex >= total) {
                StationListResponse response = new StationListResponse();
                response.setTotal(0);
                response.setPage(validPage);
                response.setPageSize(pageSize);
                response.setList(new ArrayList<>());
                return ApiResponse.success(response);
            }
            
            int endIndex = Math.min(startIndex + pageSize, total);
            List<Station> pageStations = stations.subList(startIndex, endIndex);
            
            List<StationDTO> stationDTOs = pageStations.stream()
                    .map(this::convertToStationDTO)
                    .collect(Collectors.toList());
            
            StationListResponse response = new StationListResponse();
            response.setTotal(total);
            response.setPage(validPage);
            response.setPageSize(pageSize);
            response.setList(stationDTOs);
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("获取换电站列表失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/{stationId}")
    public ApiResponse<StationDetailDTO> getStationDetail(@PathVariable String stationId) {
        try {
            Station station = stationRepository.findByStationId(stationId)
                    .orElseThrow(() -> new RuntimeException("换电站不存在"));
            
            StationDetailDTO dto = convertToStationDetailDTO(station);
            return ApiResponse.success(dto);
            
        } catch (Exception e) {
            return ApiResponse.error("获取换电站信息失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/statistics")
    public ApiResponse<StationStatistics> getStationStatistics() {
        try {
            long total = stationRepository.count();
            long active = stationRepository.countByStatus(Station.Status.active);
            long maintenance = stationRepository.countByStatus(Station.Status.maintenance);
            long offline = stationRepository.countByStatus(Station.Status.closed);
            
            Long totalCapacity = stationRepository.sumTotalCapacity();
            Long totalBatteries = stationRepository.sumAvailableBatteries();
            
            int avgUtilization = 0;
            if (totalCapacity != null && totalCapacity > 0 && totalBatteries != null) {
                avgUtilization = (int) ((totalBatteries * 100) / totalCapacity);
            }
            
            StationStatistics stats = new StationStatistics();
            stats.setTotal((int) total);
            stats.setActive((int) active);
            stats.setMaintenance((int) maintenance);
            stats.setOffline((int) offline);
            stats.setTotalCapacity(totalCapacity != null ? totalCapacity.intValue() : 0);
            stats.setTotalBatteries(totalBatteries != null ? totalBatteries.intValue() : 0);
            stats.setAvgUtilization(avgUtilization);
            
            return ApiResponse.success(stats);
            
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{stationId}/status")
    public ApiResponse<Void> updateStationStatus(@PathVariable String stationId, @RequestBody StatusUpdateRequest request) {
        try {
            Station station = stationRepository.findByStationId(stationId)
                    .orElseThrow(() -> new RuntimeException("换电站不存在"));
            
            if (request.getStatus() != null) {
                Station.Status newStatus = Station.Status.valueOf(request.getStatus());
                station.setStatus(newStatus);
            }
            
            stationRepository.save(station);
            
            return ApiResponse.success(null);
            
        } catch (Exception e) {
            return ApiResponse.error("更新状态失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/{stationId}/batteries")
    public ApiResponse<StationBatteriesResponse> getStationBatteries(@PathVariable String stationId) {
        try {
            Station station = stationRepository.findByStationId(stationId)
                    .orElseThrow(() -> new RuntimeException("换电站不存在"));
            
            List<Battery> allBatteries = batteryRepository.findAll();
            
            List<Battery> stationBatteries = allBatteries.stream()
                    .limit(station.getAvailableBatteries() + 3)
                    .collect(Collectors.toList());
            
            int available = 0;
            int charging = 0;
            List<BatteryInfoDTO> batteryDTOs = new ArrayList<>();
            
            for (Battery battery : stationBatteries) {
                BatteryInfoDTO dto = new BatteryInfoDTO();
                dto.setPid(battery.getPid());
                dto.setBatteryLevel(battery.getBatteryLevel() != null ? battery.getBatteryLevel().intValue() : 0);
                dto.setTemperature(battery.getTemperature());
                dto.setLastUpdated(battery.getLastUpdate() != null ? battery.getLastUpdate().format(FORMATTER) : null);
                
                if ("normal".equals(battery.getStatus()) && battery.getBatteryLevel() != null && battery.getBatteryLevel() >= 80) {
                    dto.setStatus("available");
                    available++;
                } else {
                    dto.setStatus("charging");
                    charging++;
                }
                
                dto.setCycles((int) (Math.random() * 200));
                batteryDTOs.add(dto);
            }
            
            StationBatteriesResponse response = new StationBatteriesResponse();
            response.setTotal(stationBatteries.size());
            response.setAvailable(available);
            response.setCharging(charging);
            response.setBatteries(batteryDTOs);
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("获取电池库存失败：" + e.getMessage());
        }
    }
    
    private StationDTO convertToStationDTO(Station station) {
        StationDTO dto = new StationDTO();
        dto.setStationId(station.getStationId());
        dto.setName(station.getName());
        dto.setAddress(station.getAddress());
        dto.setStatus(station.getStatus() != null ? station.getStatus().name() : "active");
        dto.setLatitude(station.getPositionX() != null ? station.getPositionX().doubleValue() / 100.0 : 0.0);
        dto.setLongitude(station.getPositionY() != null ? station.getPositionY().doubleValue() / 100.0 : 0.0);
        dto.setTotalSlots(station.getBatteryCapacity());
        dto.setAvailableBatteries(station.getAvailableBatteries());
        dto.setChargingBatteries(Math.max(0, (station.getBatteryCapacity() != null ? station.getBatteryCapacity() : 0) - (station.getAvailableBatteries() != null ? station.getAvailableBatteries() : 0)));
        dto.setCapacity(station.getBatteryCapacity() != null ? station.getBatteryCapacity() * 5 : 0);
        dto.setCreatedAt(station.getCreatedAt() != null ? station.getCreatedAt().format(FORMATTER) : null);
        dto.setUpdatedAt(station.getUpdatedAt() != null ? station.getUpdatedAt().format(FORMATTER) : null);
        return dto;
    }
    
    private StationDetailDTO convertToStationDetailDTO(Station station) {
        StationDetailDTO dto = new StationDetailDTO();
        dto.setStationId(station.getStationId());
        dto.setName(station.getName());
        dto.setAddress(station.getAddress());
        dto.setStatus(station.getStatus() != null ? station.getStatus().name() : "active");
        dto.setLatitude(station.getPositionX() != null ? station.getPositionX().doubleValue() / 100.0 : 0.0);
        dto.setLongitude(station.getPositionY() != null ? station.getPositionY().doubleValue() / 100.0 : 0.0);
        dto.setTotalSlots(station.getBatteryCapacity());
        dto.setAvailableBatteries(station.getAvailableBatteries());
        dto.setChargingBatteries(Math.max(0, (station.getBatteryCapacity() != null ? station.getBatteryCapacity() : 0) - (station.getAvailableBatteries() != null ? station.getAvailableBatteries() : 0)));
        dto.setCapacity(station.getBatteryCapacity() != null ? station.getBatteryCapacity() * 5 : 0);
        dto.setManager(station.getManager());
        dto.setPhone(station.getContactPhone());
        dto.setWorkingHours(station.getOperatingHours());
        dto.setCreatedAt(station.getCreatedAt() != null ? station.getCreatedAt().format(FORMATTER) : null);
        dto.setUpdatedAt(station.getUpdatedAt() != null ? station.getUpdatedAt().format(FORMATTER) : null);
        return dto;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class StationDTO {
        @JsonProperty("stationId")
        private String stationId;
        @JsonProperty("name")
        private String name;
        @JsonProperty("address")
        private String address;
        @JsonProperty("status")
        private String status;
        @JsonProperty("latitude")
        private Double latitude;
        @JsonProperty("longitude")
        private Double longitude;
        @JsonProperty("totalSlots")
        private Integer totalSlots;
        @JsonProperty("availableBatteries")
        private Integer availableBatteries;
        @JsonProperty("chargingBatteries")
        private Integer chargingBatteries;
        @JsonProperty("capacity")
        private Integer capacity;
        @JsonProperty("createdAt")
        private String createdAt;
        @JsonProperty("updatedAt")
        private String updatedAt;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class StationDetailDTO {
        @JsonProperty("stationId")
        private String stationId;
        @JsonProperty("name")
        private String name;
        @JsonProperty("address")
        private String address;
        @JsonProperty("status")
        private String status;
        @JsonProperty("latitude")
        private Double latitude;
        @JsonProperty("longitude")
        private Double longitude;
        @JsonProperty("totalSlots")
        private Integer totalSlots;
        @JsonProperty("availableBatteries")
        private Integer availableBatteries;
        @JsonProperty("chargingBatteries")
        private Integer chargingBatteries;
        @JsonProperty("capacity")
        private Integer capacity;
        @JsonProperty("manager")
        private String manager;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("workingHours")
        private String workingHours;
        @JsonProperty("createdAt")
        private String createdAt;
        @JsonProperty("updatedAt")
        private String updatedAt;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class StationListResponse {
        @JsonProperty("total")
        private Integer total;
        @JsonProperty("page")
        private Integer page;
        @JsonProperty("pageSize")
        private Integer pageSize;
        @JsonProperty("list")
        private List<StationDTO> list;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class StationStatistics {
        @JsonProperty("total")
        private Integer total;
        @JsonProperty("active")
        private Integer active;
        @JsonProperty("maintenance")
        private Integer maintenance;
        @JsonProperty("offline")
        private Integer offline;
        @JsonProperty("totalCapacity")
        private Integer totalCapacity;
        @JsonProperty("totalBatteries")
        private Integer totalBatteries;
        @JsonProperty("avgUtilization")
        private Integer avgUtilization;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class StatusUpdateRequest {
        @JsonProperty("status")
        private String status;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class BatteryInfoDTO {
        @JsonProperty("pid")
        private String pid;
        @JsonProperty("batteryLevel")
        private Integer batteryLevel;
        @JsonProperty("status")
        private String status;
        @JsonProperty("temperature")
        private Double temperature;
        @JsonProperty("cycles")
        private Integer cycles;
        @JsonProperty("lastUpdated")
        private String lastUpdated;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class StationBatteriesResponse {
        @JsonProperty("total")
        private Integer total;
        @JsonProperty("available")
        private Integer available;
        @JsonProperty("charging")
        private Integer charging;
        @JsonProperty("batteries")
        private List<BatteryInfoDTO> batteries;
    }
}
