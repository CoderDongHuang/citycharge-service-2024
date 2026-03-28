package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.ControlCommandDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/control")
@RequiredArgsConstructor
@Slf4j
public class ControlController {
    
    @PostMapping("/vehicles/{vid}/lights")
    public ApiResponse<String> controlHeadlight(@PathVariable String vid, @RequestBody ControlCommandDTO command) {
        try {
            command.setVid(vid);
            log.info("灯光控制指令 - 车辆：{}, 类型：{}", vid, command.getCommandType());
            return ApiResponse.success("灯光控制指令发送成功");
        } catch (Exception e) {
            return ApiResponse.error("灯光控制失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/vehicles/{vid}/horn")
    public ApiResponse<String> controlHorn(@PathVariable String vid, @RequestBody ControlCommandDTO command) {
        try {
            command.setVid(vid);
            log.info("喇叭控制指令 - 车辆：{}, 类型：{}", vid, command.getCommandType());
            return ApiResponse.success("喇叭控制指令发送成功");
        } catch (Exception e) {
            return ApiResponse.error("喇叭控制失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/vehicles/{vid}/notification")
    public ApiResponse<String> sendNotification(@PathVariable String vid, @RequestBody ControlCommandDTO command) {
        try {
            command.setVid(vid);
            log.info("发送通知 - 车辆：{}, 类型：{}", vid, command.getCommandType());
            return ApiResponse.success("通知发送成功");
        } catch (Exception e) {
            return ApiResponse.error("通知发送失败：" + e.getMessage());
        }
    }
}
