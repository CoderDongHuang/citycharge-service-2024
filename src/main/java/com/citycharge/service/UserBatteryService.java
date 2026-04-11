package com.citycharge.service;

import com.citycharge.entity.UserBattery;
import com.citycharge.repository.UserBatteryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBatteryService {
    
    private final UserBatteryRepository userBatteryRepository;
    
    public List<UserBattery> findByUserId(Long userId) {
        return userBatteryRepository.findByUserId(userId);
    }
    
    public Optional<UserBattery> findByIdAndUserId(Long id, Long userId) {
        return userBatteryRepository.findByIdAndUserId(id, userId);
    }
    
    public long countByUserId(Long userId) {
        return userBatteryRepository.countByUserId(userId);
    }
    
    @Transactional
    public UserBattery create(UserBattery battery) {
        if (battery.getCode() == null || battery.getCode().isEmpty()) {
            throw new RuntimeException("电池编码不能为空");
        }
        
        if (userBatteryRepository.existsByCode(battery.getCode())) {
            throw new RuntimeException("电池编码已存在");
        }
        
        return userBatteryRepository.save(battery);
    }
    
    @Transactional
    public UserBattery update(Long id, Long userId, UserBattery batteryData) {
        UserBattery battery = userBatteryRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new RuntimeException("电池不存在"));
        
        if (batteryData.getName() != null) {
            battery.setName(batteryData.getName());
        }
        if (batteryData.getModel() != null) {
            battery.setModel(batteryData.getModel());
        }
        if (batteryData.getCode() != null) {
            if (!battery.getCode().equals(batteryData.getCode()) && 
                userBatteryRepository.existsByCode(batteryData.getCode())) {
                throw new RuntimeException("电池编码已存在");
            }
            battery.setCode(batteryData.getCode());
        }
        if (batteryData.getCapacity() != null) {
            battery.setCapacity(batteryData.getCapacity());
        }
        if (batteryData.getPurchaseDate() != null) {
            battery.setPurchaseDate(batteryData.getPurchaseDate());
        }
        if (batteryData.getNotes() != null) {
            battery.setNotes(batteryData.getNotes());
        }
        
        return userBatteryRepository.save(battery);
    }
    
    @Transactional
    public void delete(Long id, Long userId) {
        if (!userBatteryRepository.existsById(id)) {
            throw new RuntimeException("电池不存在");
        }
        userBatteryRepository.deleteByIdAndUserId(id, userId);
    }
}
