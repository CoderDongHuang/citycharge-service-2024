package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.ControlCommandDTO;
import com.citycharge.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/control")
@RequiredArgsConstructor
public class ControlController {
    
    private final WebSocketService webSocketService;
    
    @PostMapping("/vehicles/{vid}/lights")
    public ApiResponse<String> controlHeadlight(@PathVariable String vid, @RequestBody ControlCommandDTO command) {
        try {
            command.setVid(vid);
            webSocketService.sendControlCommand(vid, command);
            return ApiResponse.success("灯光控制指令发送成功");
        } catch (Exception e) {
            return ApiResponse.error("灯光控制失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/vehicles/{vid}/horn")
    public ApiResponse<String> controlHorn(@PathVariable String vid, @RequestBody ControlCommandDTO command) {
        try {
            command.setVid(vid);
            webSocketService.sendControlCommand(vid, command);
            return ApiResponse.success("喇叭控制指令发送成功");
        } catch (Exception e) {
            return ApiResponse.error("喇叭控制失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/vehicles/{vid}/notification")
    public ApiResponse<String> sendNotification(@PathVariable String vid, @RequestBody ControlCommandDTO command) {
        try {
            command.setVid(vid);
            webSocketService.sendVehicleStatusUpdate(vid, command);
            return ApiResponse.success("通知发送成功");
        } catch (Exception e) {
            return ApiResponse.error("通知发送失败: " + e.getMessage());
        }
    }
}