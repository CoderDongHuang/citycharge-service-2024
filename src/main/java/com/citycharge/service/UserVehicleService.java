package com.citycharge.service;

import com.citycharge.entity.UserVehicle;
import com.citycharge.repository.UserVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserVehicleService {
    
    private final UserVehicleRepository userVehicleRepository;
    
    public List<UserVehicle> findByUserId(Long userId) {
        return userVehicleRepository.findByUserId(userId);
    }
    
    public Optional<UserVehicle> findByIdAndUserId(Long id, Long userId) {
        return userVehicleRepository.findByIdAndUserId(id, userId);
    }
    
    @Transactional
    public UserVehicle create(UserVehicle vehicle) {
        if (vehicle.getVin() == null || vehicle.getVin().length() != 17) {
            throw new RuntimeException("车架号必须为17位");
        }
        
        if (userVehicleRepository.existsByVin(vehicle.getVin())) {
            throw new RuntimeException("车架号已存在");
        }
        
        return userVehicleRepository.save(vehicle);
    }
    
    @Transactional
    public UserVehicle update(Long id, Long userId, UserVehicle vehicleData) {
        UserVehicle vehicle = userVehicleRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new RuntimeException("车辆不存在"));
        
        if (vehicleData.getName() != null) {
            vehicle.setName(vehicleData.getName());
        }
        if (vehicleData.getBrand() != null) {
            vehicle.setBrand(vehicleData.getBrand());
        }
        if (vehicleData.getVin() != null) {
            if (vehicleData.getVin().length() != 17) {
                throw new RuntimeException("车架号必须为17位");
            }
            if (!vehicle.getVin().equals(vehicleData.getVin()) && 
                userVehicleRepository.existsByVin(vehicleData.getVin())) {
                throw new RuntimeException("车架号已存在");
            }
            vehicle.setVin(vehicleData.getVin());
        }
        if (vehicleData.getPlateNumber() != null) {
            vehicle.setPlateNumber(vehicleData.getPlateNumber());
        }
        if (vehicleData.getPurchaseDate() != null) {
            vehicle.setPurchaseDate(vehicleData.getPurchaseDate());
        }
        if (vehicleData.getNotes() != null) {
            vehicle.setNotes(vehicleData.getNotes());
        }
        
        return userVehicleRepository.save(vehicle);
    }
    
    @Transactional
    public void delete(Long id, Long userId) {
        if (!userVehicleRepository.existsById(id)) {
            throw new RuntimeException("车辆不存在");
        }
        userVehicleRepository.deleteByIdAndUserId(id, userId);
    }
    
    public boolean existsByVin(String vin) {
        return userVehicleRepository.existsByVin(vin);
    }
}
