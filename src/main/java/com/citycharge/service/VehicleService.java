package com.citycharge.service;

import com.citycharge.entity.Vehicle;
import com.citycharge.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }
    
    public Vehicle findByVid(String vid) {
        Optional<Vehicle> vehicle = vehicleRepository.findByVid(vid);
        return vehicle.orElse(null);
    }
    
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
    
    public Vehicle update(String vid, Vehicle vehicle) {
        Vehicle existingVehicle = findByVid(vid);
        if (existingVehicle != null) {
            vehicle.setId(existingVehicle.getId());
            return vehicleRepository.save(vehicle);
        }
        return null;
    }
    
    public void deleteByVid(String vid) {
        Vehicle vehicle = findByVid(vid);
        if (vehicle != null) {
            vehicleRepository.delete(vehicle);
        }
    }
    
    public List<Vehicle> findByOnlineStatus(Boolean online) {
        if (online != null && online) {
            return vehicleRepository.findByOnlineStatusTrue();
        }
        return vehicleRepository.findAll();
    }
}