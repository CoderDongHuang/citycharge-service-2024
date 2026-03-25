package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.VehicleDTO;
import com.citycharge.dto.VehicleStatusUpdateDTO;
import com.citycharge.entity.Vehicle;
import com.citycharge.service.VehicleService;
import com.citycharge.service.VehicleControlService;
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
    private final VehicleControlService vehicleControlService;
    
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
    
    @PutMapping("/{vid}/status")
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
    
    /**
     * 发送车辆控制指令 (REST API方式)
     */
    @PostMapping("/{vid}/control")
    public ApiResponse<String> controlVehicle(@PathVariable String vid, @RequestBody com.citycharge.dto.ControlCommand command) {
        try {
            // 验证指令参数
            if (!vehicleControlService.validateCommand(command)) {
                return ApiResponse.error(400, "控制指令参数无效");
            }
            
            // 发送控制指令
            boolean success = vehicleControlService.sendControlCommand(vid, command);
            
            if (success) {
                return ApiResponse.success("控制指令发送成功");
            } else {
                return ApiResponse.error("控制指令发送失败，车辆可能不在线");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("控制指令发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 灯光控制接口
     */
    @PostMapping("/{vid}/control/lights")
    public ApiResponse<String> controlLights(@PathVariable String vid, @RequestParam String status) {
        try {
            boolean success = vehicleControlService.controlLights(vid, status);
            
            if (success) {
                return ApiResponse.success("灯光控制指令发送成功");
            } else {
                return ApiResponse.error("灯光控制指令发送失败");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("灯光控制失败: " + e.getMessage());
        }
    }
    
    /**
     * 闪烁灯光接口
     */
    @PostMapping("/{vid}/control/flash")
    public ApiResponse<String> flashLights(@PathVariable String vid, 
                                          @RequestParam String pattern,
                                          @RequestParam int duration) {
        try {
            boolean success = vehicleControlService.flashLights(vid, pattern, duration);
            
            if (success) {
                return ApiResponse.success("闪烁灯光指令发送成功");
            } else {
                return ApiResponse.error("闪烁灯光指令发送失败");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("闪烁灯光失败: " + e.getMessage());
        }
    }
    
    /**
     * 喇叭控制接口
     */
    @PostMapping("/{vid}/control/horn")
    public ApiResponse<String> beepHorn(@PathVariable String vid, 
                                       @RequestParam String pattern,
                                       @RequestParam int interval) {
        try {
            boolean success = vehicleControlService.beepHorn(vid, pattern, interval);
            
            if (success) {
                return ApiResponse.success("喇叭控制指令发送成功");
            } else {
                return ApiResponse.error("喇叭控制指令发送失败");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("喇叭控制失败: " + e.getMessage());
        }
    }
    
    /**
     * 位置设置接口
     */
    @PostMapping("/{vid}/control/position")
    public ApiResponse<String> setPosition(@PathVariable String vid, 
                                         @RequestParam double x,
                                         @RequestParam double y) {
        try {
            boolean success = vehicleControlService.setPosition(vid, x, y);
            
            if (success) {
                return ApiResponse.success("位置设置指令发送成功");
            } else {
                return ApiResponse.error("位置设置指令发送失败");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("位置设置失败: " + e.getMessage());
        }
    }
    

    
    @PostMapping("")
    public ApiResponse<VehicleDTO> createVehicle(@RequestBody Vehicle vehicle) {
        try {
            Vehicle savedVehicle = vehicleService.save(vehicle);
            return ApiResponse.success("车辆创建成功", VehicleDTO.fromEntity(savedVehicle));
        } catch (Exception e) {
            return ApiResponse.error("车辆创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{vid}")
    public ApiResponse<VehicleDTO> updateVehicle(@PathVariable String vid, @RequestBody Vehicle vehicle) {
        try {
            Vehicle updatedVehicle = vehicleService.update(vid, vehicle);
            return ApiResponse.success("车辆更新成功", VehicleDTO.fromEntity(updatedVehicle));
        } catch (Exception e) {
            return ApiResponse.error("车辆更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{vid}")
    public ApiResponse<String> deleteVehicle(@PathVariable String vid) {
        try {
            vehicleService.deleteByVid(vid);
            return ApiResponse.success("车辆删除成功");
        } catch (Exception e) {
            return ApiResponse.error("车辆删除失败: " + e.getMessage());
        }
    }
    
}