package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.*;
import com.citycharge.service.UserStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class UserStationController {
    
    private final UserStationService userStationService;
    
    @GetMapping
    public ApiResponse<StationListResponseDTO> getStationsList(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false, defaultValue = "10") Integer radius) {
        
        StationListResponseDTO response = userStationService.getStationsList(page, pageSize, type, latitude, longitude, radius);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/{stationId}")
    public ApiResponse<StationDetailDTO> getStationDetail(@PathVariable String stationId) {
        StationDetailDTO station = userStationService.getStationDetail(stationId);
        if (station == null) {
            return ApiResponse.error(404, "站点不存在");
        }
        return ApiResponse.success(station);
    }
    
    @GetMapping("/search")
    public ApiResponse<StationListResponseDTO> searchStations(
            @RequestParam String keyword,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        
        StationListResponseDTO response = userStationService.searchStations(keyword, latitude, longitude, page, pageSize);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/{stationId}/photos")
    public ApiResponse<List<StationPhotoDTO>> getStationPhotos(@PathVariable String stationId) {
        List<StationPhotoDTO> photos = userStationService.getStationPhotos(stationId);
        return ApiResponse.success(photos);
    }
    
    @PostMapping("/{stationId}/photos")
    public ApiResponse<StationPhotoDTO> uploadStationPhoto(
            @PathVariable String stationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "main") String type,
            @RequestParam(required = false) String description) {
        
        String url = "/uploads/stations/" + stationId + "/" + file.getOriginalFilename();
        StationPhotoDTO photo = userStationService.uploadStationPhoto(stationId, url, type, description);
        if (photo == null) {
            return ApiResponse.error(404, "站点不存在");
        }
        return ApiResponse.success("上传成功", photo);
    }
    
    @DeleteMapping("/{stationId}/photos/{photoId}")
    public ApiResponse<Void> deleteStationPhoto(
            @PathVariable String stationId,
            @PathVariable String photoId) {
        
        boolean success = userStationService.deleteStationPhoto(stationId, photoId);
        if (!success) {
            return ApiResponse.error(404, "照片不存在或无权删除");
        }
        return ApiResponse.success("删除成功", null);
    }
    
    @PostMapping("/{stationId}/status-report")
    public ApiResponse<Void> reportStationStatus(
            @PathVariable String stationId,
            @RequestBody StationStatusReportDTO reportDTO) {
        
        boolean success = userStationService.reportStationStatus(stationId, null, reportDTO.getType(), reportDTO.getDescription());
        if (!success) {
            return ApiResponse.error(404, "站点不存在");
        }
        return ApiResponse.success("上报成功", null);
    }
    
    @GetMapping("/nearby")
    public ApiResponse<List<UserStationDTO>> getNearbyStations(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(required = false, defaultValue = "10") Integer radius) {
        
        List<UserStationDTO> stations = userStationService.getNearbyStations(latitude, longitude, radius);
        return ApiResponse.success(stations);
    }
    
    @GetMapping("/available")
    public ApiResponse<List<UserStationDTO>> getAvailableStations(
            @RequestParam(required = false, defaultValue = "1") Integer minBatteries) {
        
        List<UserStationDTO> stations = userStationService.getAvailableStations(minBatteries);
        return ApiResponse.success(stations);
    }
}
