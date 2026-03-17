package com.citycharge.controller;

import com.citycharge.dto.ControlCommandDTO;
import com.citycharge.service.WebSocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/control")
public class ControlController {
    
    private final WebSocketService webSocketService;
    
    public ControlController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }
    
    @PostMapping("/headlight")
    public ResponseEntity<Void> controlHeadlight(@RequestBody ControlCommandDTO command) {
        webSocketService.sendControlCommand(command.getVid(), command);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/horn")
    public ResponseEntity<Void> controlHorn(@RequestBody ControlCommandDTO command) {
        webSocketService.sendControlCommand(command.getVid(), command);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/notification")
    public ResponseEntity<Void> sendNotification(@RequestBody ControlCommandDTO command) {
        webSocketService.sendVehicleStatusUpdate(command.getVid(), command);
        return ResponseEntity.ok().build();
    }
}