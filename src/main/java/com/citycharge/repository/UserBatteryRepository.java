package com.citycharge.repository;

import com.citycharge.entity.UserBattery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBatteryRepository extends JpaRepository<UserBattery, Long> {
    
    List<UserBattery> findByUserId(Long userId);
    
    Optional<UserBattery> findByIdAndUserId(Long id, Long userId);
    
    Optional<UserBattery> findByCode(String code);
    
    boolean existsByCode(String code);
    
    long countByUserId(Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
    
    Optional<UserBattery> findByCurrentVehicleId(Long currentVehicleId);
    
    List<UserBattery> findByCurrentVehicleIdIn(List<Long> vehicleIds);
}
