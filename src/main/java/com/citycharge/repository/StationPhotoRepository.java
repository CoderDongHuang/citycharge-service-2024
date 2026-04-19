package com.citycharge.repository;

import com.citycharge.entity.StationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationPhotoRepository extends JpaRepository<StationPhoto, Long> {
    
    List<StationPhoto> findByStationId(Long stationId);
    
    List<StationPhoto> findByStationIdAndType(Long stationId, StationPhoto.PhotoType type);
    
    Optional<StationPhoto> findByPhotoId(String photoId);
    
    void deleteByStationId(Long stationId);
    
    void deleteByPhotoId(String photoId);
}
