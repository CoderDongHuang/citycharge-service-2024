package com.citycharge.service;

import com.citycharge.dto.AdminStatisticsDTO;
import com.citycharge.dto.AdminUserBatteryDTO;
import com.citycharge.dto.AdminUserVehicleDTO;
import com.citycharge.entity.User;
import com.citycharge.entity.UserBattery;
import com.citycharge.entity.UserVehicle;
import com.citycharge.repository.UserBatteryRepository;
import com.citycharge.repository.UserRepository;
import com.citycharge.repository.UserVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserDataService {
    
    private final UserVehicleRepository userVehicleRepository;
    private final UserBatteryRepository userBatteryRepository;
    private final UserRepository userRepository;
    
    public Page<AdminUserVehicleDTO> getUserVehicles(Integer page, Integer size, Long userId, String status, String keyword) {
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 20);
        
        List<UserVehicle> allVehicles = userVehicleRepository.findAll();
        
        List<UserVehicle> filteredVehicles = allVehicles.stream()
                .filter(v -> userId == null || v.getUserId().equals(userId))
                .filter(v -> status == null || status.isEmpty() || v.getStatus().name().equals(status))
                .filter(v -> {
                    if (keyword == null || keyword.isEmpty()) return true;
                    User user = userRepository.findById(v.getUserId()).orElse(null);
                    String userName = user != null ? user.getUsername() : "";
                    String plateNumber = v.getPlateNumber() != null ? v.getPlateNumber() : "";
                    return userName.contains(keyword) || plateNumber.contains(keyword);
                })
                .collect(Collectors.toList());
        
        Map<Long, User> userMap = getUserMap(filteredVehicles.stream().map(UserVehicle::getUserId).collect(Collectors.toList()));
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredVehicles.size());
        List<UserVehicle> pagedVehicles = filteredVehicles.subList(start, end);
        
        List<AdminUserVehicleDTO> dtoList = pagedVehicles.stream()
                .map(v -> toVehicleDTO(v, userMap.get(v.getUserId())))
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, filteredVehicles.size());
    }
    
    public Page<AdminUserBatteryDTO> getUserBatteries(Integer page, Integer size, Long userId, String status, String model, String keyword) {
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 20);
        
        List<UserBattery> allBatteries = userBatteryRepository.findAll();
        
        List<UserBattery> filteredBatteries = allBatteries.stream()
                .filter(b -> userId == null || b.getUserId().equals(userId))
                .filter(b -> status == null || status.isEmpty() || b.getStatus().name().equals(status))
                .filter(b -> model == null || model.isEmpty() || b.getModel().contains(model))
                .filter(b -> {
                    if (keyword == null || keyword.isEmpty()) return true;
                    User user = userRepository.findById(b.getUserId()).orElse(null);
                    String userName = user != null ? user.getUsername() : "";
                    String batteryName = b.getName() != null ? b.getName() : "";
                    return userName.contains(keyword) || batteryName.contains(keyword);
                })
                .collect(Collectors.toList());
        
        Map<Long, User> userMap = getUserMap(filteredBatteries.stream().map(UserBattery::getUserId).collect(Collectors.toList()));
        
        Map<Long, UserVehicle> vehicleMap = getVehicleMapForBatteries(filteredBatteries);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredBatteries.size());
        List<UserBattery> pagedBatteries = filteredBatteries.subList(start, end);
        
        List<AdminUserBatteryDTO> dtoList = pagedBatteries.stream()
                .map(b -> toBatteryDTO(b, userMap.get(b.getUserId()), vehicleMap))
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, filteredBatteries.size());
    }
    
    public AdminStatisticsDTO getStatistics() {
        AdminStatisticsDTO dto = new AdminStatisticsDTO();
        
        AdminStatisticsDTO.VehicleStatistics vehicleStats = new AdminStatisticsDTO.VehicleStatistics();
        List<UserVehicle> allVehicles = userVehicleRepository.findAll();
        vehicleStats.setTotal(allVehicles.size());
        vehicleStats.setOnline(allVehicles.stream().filter(v -> v.getStatus() == UserVehicle.VehicleStatus.online).count());
        vehicleStats.setOffline(allVehicles.stream().filter(v -> v.getStatus() == UserVehicle.VehicleStatus.offline).count());
        vehicleStats.setActive(allVehicles.size());
        vehicleStats.setMaintenance(0);
        dto.setVehicles(vehicleStats);
        
        AdminStatisticsDTO.BatteryStatistics batteryStats = new AdminStatisticsDTO.BatteryStatistics();
        List<UserBattery> allBatteries = userBatteryRepository.findAll();
        batteryStats.setTotal(allBatteries.size());
        batteryStats.setOnline(allBatteries.stream().filter(b -> b.getStatus() == UserBattery.BatteryStatus.online).count());
        batteryStats.setOffline(allBatteries.stream().filter(b -> b.getStatus() == UserBattery.BatteryStatus.offline).count());
        batteryStats.setInUse(allBatteries.stream().filter(b -> b.getStatus() == UserBattery.BatteryStatus.online).count());
        batteryStats.setAvailable(allBatteries.stream().filter(b -> b.getStatus() == UserBattery.BatteryStatus.offline).count());
        batteryStats.setMaintenance(0);
        batteryStats.setLowVoltage(allBatteries.stream().filter(b -> b.getVoltage() != null && b.getVoltage() < 3.0).count());
        batteryStats.setOverheat(allBatteries.stream().filter(b -> b.getTemperature() != null && b.getTemperature() > 45).count());
        dto.setBatteries(batteryStats);
        
        return dto;
    }
    
    private Map<Long, User> getUserMap(List<Long> userIds) {
        Map<Long, User> userMap = new HashMap<>();
        if (userIds != null && !userIds.isEmpty()) {
            List<User> users = userRepository.findAllById(userIds);
            users.forEach(u -> userMap.put(u.getId(), u));
        }
        return userMap;
    }
    
    private Map<Long, UserVehicle> getVehicleMapForBatteries(List<UserBattery> batteries) {
        Map<Long, UserVehicle> vehicleMap = new HashMap<>();
        return vehicleMap;
    }
    
    private AdminUserVehicleDTO toVehicleDTO(UserVehicle vehicle, User user) {
        AdminUserVehicleDTO dto = new AdminUserVehicleDTO();
        dto.setId(vehicle.getId());
        dto.setUserId(vehicle.getUserId());
        dto.setUserName(user != null ? user.getUsername() : null);
        dto.setUserPhone(user != null ? user.getPhone() : null);
        dto.setName(vehicle.getName());
        dto.setBrand(vehicle.getBrand());
        dto.setVin(vehicle.getVin());
        dto.setPlateNumber(vehicle.getPlateNumber());
        dto.setStatus(vehicle.getStatus() != null ? vehicle.getStatus().name() : "offline");
        dto.setBatteryLevel(vehicle.getBatteryLevel());
        dto.setOnline(vehicle.getStatus() == UserVehicle.VehicleStatus.online);
        dto.setLastUpdateTime(vehicle.getUpdatedAt());
        dto.setCreateTime(vehicle.getCreatedAt());
        dto.setSource("user");
        return dto;
    }
    
    private AdminUserBatteryDTO toBatteryDTO(UserBattery battery, User user, Map<Long, UserVehicle> vehicleMap) {
        AdminUserBatteryDTO dto = new AdminUserBatteryDTO();
        dto.setId(battery.getId());
        dto.setUserId(battery.getUserId());
        dto.setUserName(user != null ? user.getUsername() : null);
        dto.setUserPhone(user != null ? user.getPhone() : null);
        dto.setName(battery.getName());
        dto.setModel(battery.getModel());
        dto.setCode(battery.getCode());
        dto.setCapacity(battery.getCapacity());
        dto.setVoltage(battery.getVoltage());
        dto.setTemperature(battery.getTemperature());
        dto.setBatteryLevel(battery.getCurrentLevel());
        dto.setHealth(100);
        dto.setStatus(battery.getStatus() != null ? battery.getStatus().name() : "offline");
        dto.setOnline(battery.getStatus() == UserBattery.BatteryStatus.online);
        dto.setLastUpdateTime(battery.getUpdatedAt());
        dto.setPurchaseDate(battery.getPurchaseDate());
        dto.setCreateTime(battery.getCreatedAt());
        dto.setSource("user");
        return dto;
    }
}
