package com.citycharge.repository;

import com.citycharge.entity.StationServiceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationServiceInfoRepository extends JpaRepository<StationServiceInfo, Long> {
    
    List<StationServiceInfo> findByStationId(Long stationId);
    
    List<StationServiceInfo> findByStationIdOrderByPriceAsc(Long stationId);
    
    void deleteByStationId(Long stationId);
}
