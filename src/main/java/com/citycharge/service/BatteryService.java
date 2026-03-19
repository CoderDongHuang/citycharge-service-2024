package com.citycharge.service;

import com.citycharge.entity.Battery;
import com.citycharge.repository.BatteryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatteryService {
    
    private final BatteryRepository batteryRepository;
    
    public List<Battery> findAll() {
        return batteryRepository.findAll();
    }
    
    public List<Battery> findByStatus(Battery.BatteryStatus status) {
        return batteryRepository.findByStatus(status);
    }
    
    public Battery findByPid(String pid) {
        return batteryRepository.findByPid(pid).orElse(null);
    }
    
    public Battery save(Battery battery) {
        return batteryRepository.save(battery);
    }
    
    public Battery update(String pid, Battery battery) {
        Battery existingBattery = batteryRepository.findByPid(pid).orElse(null);
        if (existingBattery != null) {
            battery.setId(existingBattery.getId());
            return batteryRepository.save(battery);
        }
        return null;
    }
    
    public void deleteByPid(String pid) {
        Battery battery = batteryRepository.findByPid(pid).orElse(null);
        if (battery != null) {
            batteryRepository.delete(battery);
        }
    }
}