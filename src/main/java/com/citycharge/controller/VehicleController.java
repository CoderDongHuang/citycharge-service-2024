package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.VehicleDTO;
import com.citycharge.dto.VehicleStatusUpdateDTO;
import com.citycharge.entity.Vehicle;
import com.citycharge.service.VehicleService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    @GetMapping("")
    public ApiResponse<List<VehicleDTO>> getAllVehicles() {
        List<VehicleDTO> vehicles = vehicleService.findAll().stream()
                .map(VehicleDTO::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success(vehicles);
    }
    
    @GetMapping("/{vid}")
    public ApiResponse<VehicleDTO> getVehicleByVid(@PathVariable String vid) {
        Vehicle vehicle = vehicleService.findByVid(vid);
        if (vehicle == null) {
            return ApiResponse.error(404, "车辆不存在");
        }
        return ApiResponse.success(VehicleDTO.fromEntity(vehicle));
    }
    
    @PutMapping("/vehicles/{vid}/status")
    public ApiResponse<String> updateVehicleStatus(@PathVariable String vid, @RequestBody VehicleStatusUpdateDTO statusUpdate) {
        try {
            Vehicle vehicle = vehicleService.findByVid(vid);
            if (vehicle == null) {
                return ApiResponse.error(404, "车辆不存在");
            }
            
            // 更新车辆状态
            if (statusUpdate.getVoltage() != null) {
                vehicle.setVoltage(statusUpdate.getVoltage());
            }
            if (statusUpdate.getTemperature() != null) {
                vehicle.setTemperature(statusUpdate.getTemperature());
            }
            if (statusUpdate.getBatteryLevel() != null) {
                vehicle.setBatteryLevel(statusUpdate.getBatteryLevel());
            }
            if (statusUpdate.getLightStatus() != null) {
                vehicle.setLightStatus(Vehicle.LightStatus.valueOf(statusUpdate.getLightStatus()));
            }
            if (statusUpdate.getPosition() != null) {
                vehicle.setPositionX(statusUpdate.getPosition().getX());
                vehicle.setPositionY(statusUpdate.getPosition().getY());
            }
            if (statusUpdate.getOnline() != null) {
                vehicle.setOnlineStatus(statusUpdate.getOnline());
            }
            
            vehicleService.save(vehicle);
            return ApiResponse.success("状态更新成功");
        } catch (Exception e) {
            return ApiResponse.error("状态更新失败: " + e.getMessage());
        }
    }
    

    
    @PostMapping("/vehicles")
    public ApiResponse<VehicleDTO> createVehicle(@RequestBody Vehicle vehicle) {
        try {
            Vehicle savedVehicle = vehicleService.save(vehicle);
            return ApiResponse.success("车辆创建成功", VehicleDTO.fromEntity(savedVehicle));
        } catch (Exception e) {
            return ApiResponse.error("车辆创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/vehicles/{vid}")
    public ApiResponse<VehicleDTO> updateVehicle(@PathVariable String vid, @RequestBody Vehicle vehicle) {
        try {
            Vehicle updatedVehicle = vehicleService.update(vid, vehicle);
            return ApiResponse.success("车辆更新成功", VehicleDTO.fromEntity(updatedVehicle));
        } catch (Exception e) {
            return ApiResponse.error("车辆更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/vehicles/{vid}")
    public ApiResponse<String> deleteVehicle(@PathVariable String vid) {
        try {
            vehicleService.deleteByVid(vid);
            return ApiResponse.success("车辆删除成功");
        } catch (Exception e) {
            return ApiResponse.error("车辆删除失败: " + e.getMessage());
        }
    }
    
}