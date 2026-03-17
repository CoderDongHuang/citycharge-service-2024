package com.citycharge.controller;

import com.citycharge.dto.VehicleStatusDTO;
import com.citycharge.entity.Vehicle;
import com.citycharge.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }
    
    @PostMapping("/{vid}/register")
    public ResponseEntity<Vehicle> registerVehicle(@PathVariable String vid) {
        Vehicle vehicle = vehicleService.registerVehicle(vid);
        return ResponseEntity.ok(vehicle);
    }
    
    @PostMapping("/{vid}/status")
    public ResponseEntity<Vehicle> updateVehicleStatus(@PathVariable String vid, @RequestBody VehicleStatusDTO statusDTO) {
        Vehicle vehicle = vehicleService.updateVehicleStatus(statusDTO);
        return ResponseEntity.ok(vehicle);
    }
    
    @GetMapping("/online")
    public ResponseEntity<List<Vehicle>> getOnlineVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllOnlineVehicles();
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/{vid}")
    public ResponseEntity<Vehicle> getVehicle(@PathVariable String vid) {
        Vehicle vehicle = vehicleService.getVehicleByVid(vid);
        return ResponseEntity.ok(vehicle);
    }
    
    @PostMapping("/{vid}/position")
    public ResponseEntity<Boolean> updatePosition(@PathVariable String vid, 
                                                 @RequestParam Integer x, 
                                                 @RequestParam Integer y) {
        boolean success = vehicleService.updateVehiclePosition(vid, x, y);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{vid}/heartbeat")
    public ResponseEntity<Void> heartbeat(@PathVariable String vid) {
        vehicleService.handleVehicleHeartbeat(vid);
        return ResponseEntity.ok().build();
    }
}