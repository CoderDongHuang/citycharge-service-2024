package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.*;
import com.citycharge.service.AdminStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/stations")
@RequiredArgsConstructor
public class AdminStationController {
    
    private final AdminStationService adminStationService;
    
    @PostMapping
    public ApiResponse<StationDetailDTO> createStation(@RequestBody CreateStationDTO dto) {
        StationDetailDTO station = adminStationService.createStation(dto);
        return ApiResponse.success("创建成功", station);
    }
    
    @PutMapping("/{stationId}")
    public ApiResponse<StationDetailDTO> updateStation(
            @PathVariable String stationId,
            @RequestBody UpdateStationDTO dto) {
        StationDetailDTO station = adminStationService.updateStation(stationId, dto);
        if (station == null) {
            return ApiResponse.error(404, "站点不存在");
        }
        return ApiResponse.success("更新成功", station);
    }
    
    @DeleteMapping("/{stationId}")
    public ApiResponse<Void> deleteStation(@PathVariable String stationId) {
        boolean success = adminStationService.deleteStation(stationId);
        if (!success) {
            return ApiResponse.error(404, "站点不存在");
        }
        return ApiResponse.success("删除成功", null);
    }
    
    @PostMapping("/{stationId}/photos")
    public ApiResponse<StationPhotoDTO> uploadPhoto(
            @PathVariable String stationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "main") String type,
            @RequestParam(required = false) String description) {
        
        try {
            StationPhotoDTO photo = adminStationService.uploadPhoto(stationId, file, type, description);
            if (photo == null) {
                return ApiResponse.error(404, "站点不存在");
            }
            return ApiResponse.success("上传成功", photo);
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{stationId}/photos/{photoId}")
    public ApiResponse<Void> deletePhoto(
            @PathVariable String stationId,
            @PathVariable String photoId) {
        
        boolean success = adminStationService.deletePhoto(stationId, photoId);
        if (!success) {
            return ApiResponse.error(404, "照片不存在或无权删除");
        }
        return ApiResponse.success("删除成功", null);
    }
}
