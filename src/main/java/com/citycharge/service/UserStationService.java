package com.citycharge.service;

import com.citycharge.dto.UserStationDTO;
import com.citycharge.entity.Station;
import com.citycharge.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStationService {
    
    private final StationRepository stationRepository;
    
    public List<UserStationDTO> getAllStations() {
        return stationRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<UserStationDTO> getByStationId(String stationId) {
        return stationRepository.findByStationId(stationId)
            .map(this::toDTO);
    }
    
    public List<UserStationDTO> getByStatus(String status) {
        Station.Status stationStatus = Station.Status.valueOf(status);
        return stationRepository.findByStatus(stationStatus).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<UserStationDTO> getNearbyStations(Integer x, Integer y, Integer radius) {
        int finalRadius = radius != null ? radius : 100;
        
        return stationRepository.findAll().stream()
            .filter(s -> calculateDistance(s.getPositionX(), s.getPositionY(), x, y) <= finalRadius)
            .sorted(Comparator.comparingDouble(s -> calculateDistance(s.getPositionX(), s.getPositionY(), x, y)))
            .map(s -> {
                UserStationDTO dto = toDTO(s);
                dto.setDistance((double) calculateDistance(s.getPositionX(), s.getPositionY(), x, y));
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    public List<UserStationDTO> getAvailableStations(Integer minBatteries) {
        int min = minBatteries != null ? minBatteries : 1;
        
        return stationRepository.findAll().stream()
            .filter(s -> s.getStatus() == Station.Status.active)
            .filter(s -> s.getAvailableBatteries() >= min)
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    private double calculateDistance(Integer x1, Integer y1, Integer x2, Integer y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
    
    private UserStationDTO toDTO(Station station) {
        UserStationDTO dto = new UserStationDTO();
        dto.setId(station.getId());
        dto.setStationId(station.getStationId());
        dto.setName(station.getName());
        dto.setPositionX(station.getPositionX());
        dto.setPositionY(station.getPositionY());
        dto.setAddress(station.getAddress());
        dto.setBatteryCapacity(station.getBatteryCapacity());
        dto.setAvailableBatteries(station.getAvailableBatteries());
        dto.setStatus(station.getStatus() != null ? station.getStatus().name() : "active");
        dto.setOperatingHours(station.getOperatingHours());
        dto.setContactPhone(station.getContactPhone());
        dto.setManager(station.getManager());
        return dto;
    }
}
