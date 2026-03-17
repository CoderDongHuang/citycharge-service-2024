package com.citycharge.controller;

import com.citycharge.dto.MapPositionDTO;
import com.citycharge.service.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
public class MapController {
    
    private final MapService mapService;
    
    public MapController(MapService mapService) {
        this.mapService = mapService;
    }
    
    @GetMapping("/stations/nearest")
    public ResponseEntity<List<MapPositionDTO>> getNearestStations(@RequestParam Integer x, 
                                                                   @RequestParam Integer y,
                                                                   @RequestParam Double battery) {
        List<MapPositionDTO> stations = mapService.findNearestChargingStations(x, y, battery);
        return ResponseEntity.ok(stations);
    }
    
    @GetMapping("/path")
    public ResponseEntity<List<int[]>> getShortestPath(@RequestParam Integer startX,
                                                       @RequestParam Integer startY,
                                                       @RequestParam Integer endX,
                                                       @RequestParam Integer endY) {
        List<int[]> path = mapService.findShortestPath(startX, startY, endX, endY);
        return ResponseEntity.ok(path);
    }
    
    @GetMapping("/reachable")
    public ResponseEntity<Boolean> checkReachable(@RequestParam Integer currentX,
                                                  @RequestParam Integer currentY,
                                                  @RequestParam Integer stationX,
                                                  @RequestParam Integer stationY,
                                                  @RequestParam Double battery) {
        boolean reachable = mapService.canReachStation(currentX, currentY, stationX, stationY, battery);
        return ResponseEntity.ok(reachable);
    }
}