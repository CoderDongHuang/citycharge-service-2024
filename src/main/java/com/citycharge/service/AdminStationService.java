package com.citycharge.service;

import com.citycharge.dto.*;
import com.citycharge.entity.Station;
import com.citycharge.entity.StationPhoto;
import com.citycharge.repository.StationPhotoRepository;
import com.citycharge.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStationService {
    
    private final StationRepository stationRepository;
    private final StationPhotoRepository stationPhotoRepository;
    
    @Value("${station.upload.path}")
    private String stationUploadPath;
    
    @Transactional
    public StationDetailDTO createStation(CreateStationDTO dto) {
        Station station = new Station();
        station.setStationId("station_" + UUID.randomUUID().toString().substring(0, 8));
        station.setName(dto.getName());
        
        if (dto.getType() != null) {
            station.setType(Station.Type.valueOf(dto.getType()));
        }
        
        station.setAddress(dto.getAddress());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());
        station.setContactPhone(dto.getContactPhone());
        station.setManager(dto.getManager());
        station.setServiceTime(dto.getServiceTime());
        station.setAvailableBatteries(dto.getAvailableBatteries() != null ? dto.getAvailableBatteries() : 0);
        station.setAvailableSlots(dto.getAvailableSlots() != null ? dto.getAvailableSlots() : 0);
        
        if (dto.getStatus() != null) {
            station.setStatus(Station.Status.valueOf(dto.getStatus()));
        }
        
        station.setRating(new BigDecimal("5.0"));
        station.setTotalSwaps(0);
        
        Station saved = stationRepository.save(station);
        return toDetailDTO(saved);
    }
    
    @Transactional
    public StationDetailDTO updateStation(String stationId, UpdateStationDTO dto) {
        Optional<Station> stationOpt = stationRepository.findByStationId(stationId);
        if (!stationOpt.isPresent()) {
            return null;
        }
        
        Station station = stationOpt.get();
        
        if (dto.getName() != null) {
            station.setName(dto.getName());
        }
        if (dto.getType() != null) {
            station.setType(Station.Type.valueOf(dto.getType()));
        }
        if (dto.getAddress() != null) {
            station.setAddress(dto.getAddress());
        }
        if (dto.getLatitude() != null) {
            station.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            station.setLongitude(dto.getLongitude());
        }
        if (dto.getContactPhone() != null) {
            station.setContactPhone(dto.getContactPhone());
        }
        if (dto.getManager() != null) {
            station.setManager(dto.getManager());
        }
        if (dto.getServiceTime() != null) {
            station.setServiceTime(dto.getServiceTime());
        }
        if (dto.getAvailableBatteries() != null) {
            station.setAvailableBatteries(dto.getAvailableBatteries());
        }
        if (dto.getAvailableSlots() != null) {
            station.setAvailableSlots(dto.getAvailableSlots());
        }
        if (dto.getStatus() != null) {
            station.setStatus(Station.Status.valueOf(dto.getStatus()));
        }
        
        Station saved = stationRepository.save(station);
        return toDetailDTO(saved);
    }
    
    @Transactional
    public boolean deleteStation(String stationId) {
        Optional<Station> stationOpt = stationRepository.findByStationId(stationId);
        if (!stationOpt.isPresent()) {
            return false;
        }
        
        Station station = stationOpt.get();
        stationPhotoRepository.deleteByStationId(station.getId());
        stationRepository.delete(station);
        
        return true;
    }
    
    @Transactional
    public StationPhotoDTO uploadPhoto(String stationId, MultipartFile file, String type, String description) throws IOException {
        Optional<Station> stationOpt = stationRepository.findByStationId(stationId);
        if (!stationOpt.isPresent()) {
            return null;
        }
        
        Path uploadPath = Paths.get(stationUploadPath, stationId).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;
        
        Path filePath = uploadPath.resolve(newFilename);
        file.transferTo(filePath.toFile());
        
        String url = "/uploads/stations/" + stationId + "/" + newFilename;
        
        StationPhoto photo = new StationPhoto();
        photo.setStationId(stationOpt.get().getId());
        photo.setPhotoId("photo_" + UUID.randomUUID().toString().substring(0, 8));
        photo.setUrl(url);
        if (type != null) {
            photo.setType(StationPhoto.PhotoType.valueOf(type));
        }
        photo.setDescription(description);
        
        StationPhoto saved = stationPhotoRepository.save(photo);
        return toPhotoDTO(saved);
    }
    
    @Transactional
    public boolean deletePhoto(String stationId, String photoId) {
        Optional<Station> stationOpt = stationRepository.findByStationId(stationId);
        if (!stationOpt.isPresent()) {
            return false;
        }
        
        Optional<StationPhoto> photoOpt = stationPhotoRepository.findByPhotoId(photoId);
        if (!photoOpt.isPresent() || !photoOpt.get().getStationId().equals(stationOpt.get().getId())) {
            return false;
        }
        
        StationPhoto photo = photoOpt.get();
        String url = photo.getUrl();
        if (url != null && url.startsWith("/uploads/stations/")) {
            try {
                Path filePath = Paths.get(stationUploadPath).resolve(url.substring("/uploads/stations/".length())).toAbsolutePath();
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
            }
        }
        
        stationPhotoRepository.deleteByPhotoId(photoId);
        return true;
    }
    
    private StationDetailDTO toDetailDTO(Station station) {
        StationDetailDTO dto = new StationDetailDTO();
        dto.setId(station.getStationId());
        dto.setStationId(station.getStationId());
        dto.setName(station.getName());
        dto.setType(station.getType() != null ? station.getType().name() : "battery");
        dto.setAddress(station.getAddress());
        dto.setLatitude(station.getLatitude());
        dto.setLongitude(station.getLongitude());
        dto.setContactPhone(station.getContactPhone());
        dto.setManager(station.getManager());
        dto.setServiceTime(station.getServiceTime());
        dto.setRating(station.getRating());
        dto.setTotalSwaps(station.getTotalSwaps());
        dto.setAvailableBatteries(station.getAvailableBatteries());
        dto.setAvailableSlots(station.getAvailableSlots());
        dto.setStatus(station.getStatus() != null ? station.getStatus().name() : "active");
        
        List<StationPhoto> photos = stationPhotoRepository.findByStationId(station.getId());
        dto.setPhotos(photos.stream().map(this::toPhotoDTO).collect(Collectors.toList()));
        
        return dto;
    }
    
    private StationPhotoDTO toPhotoDTO(StationPhoto photo) {
        StationPhotoDTO dto = new StationPhotoDTO();
        dto.setId(photo.getPhotoId());
        dto.setUrl(photo.getUrl());
        dto.setType(photo.getType() != null ? photo.getType().name() : "main");
        dto.setUploadTime(photo.getUploadTime());
        dto.setDescription(photo.getDescription());
        return dto;
    }
}
