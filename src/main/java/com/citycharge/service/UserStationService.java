package com.citycharge.service;

import com.citycharge.dto.*;
import com.citycharge.entity.Station;
import com.citycharge.entity.StationPhoto;
import com.citycharge.entity.StationServiceInfo;
import com.citycharge.entity.StationStatusReport;
import com.citycharge.repository.StationPhotoRepository;
import com.citycharge.repository.StationRepository;
import com.citycharge.repository.StationServiceInfoRepository;
import com.citycharge.repository.StationStatusReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStationService {
    
    private final StationRepository stationRepository;
    private final StationPhotoRepository stationPhotoRepository;
    private final StationServiceInfoRepository stationServiceInfoRepository;
    private final StationStatusReportRepository stationStatusReportRepository;
    private final ObjectMapper objectMapper;
    
    public StationListResponseDTO getStationsList(Integer page, Integer pageSize, String type,
            BigDecimal latitude, BigDecimal longitude, Integer radius) {
        int currentPage = page != null ? page : 1;
        int currentSize = pageSize != null ? pageSize : 20;
        int currentRadius = radius != null ? radius : 10;
        
        Pageable pageable = PageRequest.of(currentPage - 1, currentSize);
        Page<Station> stationPage;
        
        if (type != null && !type.equals("all")) {
            Station.Type stationType = Station.Type.valueOf(type);
            stationPage = stationRepository.findByType(stationType, pageable);
        } else {
            stationPage = stationRepository.findAll(pageable);
        }
        
        List<UserStationDTO> content = stationPage.getContent().stream()
            .map(station -> {
                UserStationDTO dto = toListDTO(station);
                if (latitude != null && longitude != null && station.getLatitude() != null && station.getLongitude() != null) {
                    double distance = calculateDistanceKm(
                        latitude.doubleValue(), longitude.doubleValue(),
                        station.getLatitude().doubleValue(), station.getLongitude().doubleValue()
                    );
                    dto.setDistance(distance);
                    if (distance > currentRadius) {
                        return null;
                    }
                }
                return dto;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        StationListResponseDTO response = new StationListResponseDTO();
        response.setContent(content);
        response.setTotal(stationPage.getTotalElements());
        response.setPage(currentPage);
        response.setPageSize(currentSize);
        response.setTotalPages(stationPage.getTotalPages());
        
        return response;
    }
    
    public StationDetailDTO getStationDetail(String stationId) {
        Optional<Station> stationOpt = stationRepository.findByStationId(stationId);
        if (!stationOpt.isPresent()) {
            return null;
        }
        
        Station station = stationOpt.get();
        StationDetailDTO dto = toDetailDTO(station);
        
        List<StationPhoto> photos = stationPhotoRepository.findByStationId(station.getId());
        dto.setPhotos(photos.stream().map(this::toPhotoDTO).collect(Collectors.toList()));
        
        List<StationServiceInfo> services = stationServiceInfoRepository.findByStationId(station.getId());
        dto.setServices(services.stream().map(this::toServiceDTO).collect(Collectors.toList()));
        
        if (station.getFacilities() != null) {
            try {
                List<String> facilities = objectMapper.readValue(station.getFacilities(), new TypeReference<List<String>>() {});
                dto.setFacilities(facilities);
            } catch (JsonProcessingException e) {
                dto.setFacilities(new ArrayList<>());
            }
        }
        
        return dto;
    }
    
    public StationListResponseDTO searchStations(String keyword, BigDecimal latitude, BigDecimal longitude,
            Integer page, Integer pageSize) {
        int currentPage = page != null ? page : 1;
        int currentSize = pageSize != null ? pageSize : 20;
        
        List<Station> stations = stationRepository.findByKeyword(keyword);
        
        List<UserStationDTO> content = stations.stream()
            .map(station -> {
                UserStationDTO dto = toListDTO(station);
                if (latitude != null && longitude != null && station.getLatitude() != null && station.getLongitude() != null) {
                    double distance = calculateDistanceKm(
                        latitude.doubleValue(), longitude.doubleValue(),
                        station.getLatitude().doubleValue(), station.getLongitude().doubleValue()
                    );
                    dto.setDistance(distance);
                }
                return dto;
            })
            .collect(Collectors.toList());
        
        int start = (currentPage - 1) * currentSize;
        int end = Math.min(start + currentSize, content.size());
        List<UserStationDTO> pagedContent = content.subList(start, end);
        
        StationListResponseDTO response = new StationListResponseDTO();
        response.setContent(pagedContent);
        response.setTotal((long) content.size());
        response.setPage(currentPage);
        response.setPageSize(currentSize);
        response.setTotalPages((int) Math.ceil((double) content.size() / currentSize));
        
        return response;
    }
    
    public List<StationPhotoDTO> getStationPhotos(String stationId) {
        Optional<Station> stationOpt = stationRepository.findByStationId(stationId);
        if (!stationOpt.isPresent()) {
            return new ArrayList<>();
        }
        
        List<StationPhoto> photos = stationPhotoRepository.findByStationId(stationOpt.get().getId());
        return photos.stream().map(this::toPhotoDTO).collect(Collectors.toList());
    }
    
    @Transactional
    public StationPhotoDTO uploadStationPhoto(String stationId, String url, String type, String description) {
        Optional<Station> stationOpt = stationRepository.findByStationId(stationId);
        if (!stationOpt.isPresent()) {
            return null;
        }
        
        StationPhoto photo = new StationPhoto();
        photo.setStationId(stationOpt.get().getId());
        photo.setPhotoId("photo_" + UUID.randomUUID().toString().substring(0, 8));
        photo.setUrl(url);
        photo.setType(StationPhoto.PhotoType.valueOf(type));
        photo.setDescription(description);
        
        StationPhoto saved = stationPhotoRepository.save(photo);
        return toPhotoDTO(saved);
    }
    
    @Transactional
    public boolean deleteStationPhoto(String stationId, String photoId) {
        Optional<Station> stationOpt = stationRepository.findByStationId(stationId);
        if (!stationOpt.isPresent()) {
            return false;
        }
        
        Optional<StationPhoto> photoOpt = stationPhotoRepository.findByPhotoId(photoId);
        if (!photoOpt.isPresent() || !photoOpt.get().getStationId().equals(stationOpt.get().getId())) {
            return false;
        }
        
        stationPhotoRepository.deleteByPhotoId(photoId);
        return true;
    }
    
    @Transactional
    public boolean reportStationStatus(String stationId, Long userId, String type, String description) {
        Optional<Station> stationOpt = stationRepository.findByStationId(stationId);
        if (!stationOpt.isPresent()) {
            return false;
        }
        
        StationStatusReport report = new StationStatusReport();
        report.setStationId(stationOpt.get().getId());
        report.setUserId(userId);
        report.setType(StationStatusReport.ReportType.valueOf(type));
        report.setDescription(description);
        
        stationStatusReportRepository.save(report);
        return true;
    }
    
    public List<UserStationDTO> getAllStations() {
        return stationRepository.findAll().stream()
            .map(this::toListDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<UserStationDTO> getByStationId(String stationId) {
        return stationRepository.findByStationId(stationId)
            .map(this::toListDTO);
    }
    
    public List<UserStationDTO> getByStatus(String status) {
        Station.Status stationStatus = Station.Status.valueOf(status);
        return stationRepository.findByStatus(stationStatus).stream()
            .map(this::toListDTO)
            .collect(Collectors.toList());
    }
    
    public List<UserStationDTO> getNearbyStations(BigDecimal latitude, BigDecimal longitude, Integer radiusKm) {
        int radius = radiusKm != null ? radiusKm : 10;
        
        return stationRepository.findAll().stream()
            .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
            .filter(s -> calculateDistanceKm(
                latitude.doubleValue(), longitude.doubleValue(),
                s.getLatitude().doubleValue(), s.getLongitude().doubleValue()) <= radius)
            .sorted(Comparator.comparingDouble(s -> calculateDistanceKm(
                latitude.doubleValue(), longitude.doubleValue(),
                s.getLatitude().doubleValue(), s.getLongitude().doubleValue())))
            .map(s -> {
                UserStationDTO dto = toListDTO(s);
                dto.setDistance(calculateDistanceKm(
                    latitude.doubleValue(), longitude.doubleValue(),
                    s.getLatitude().doubleValue(), s.getLongitude().doubleValue()));
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    public List<UserStationDTO> getAvailableStations(Integer minBatteries) {
        int min = minBatteries != null ? minBatteries : 1;
        
        return stationRepository.findAll().stream()
            .filter(s -> s.getStatus() == Station.Status.active || s.getStatus() == Station.Status.online)
            .filter(s -> s.getAvailableBatteries() >= min)
            .map(this::toListDTO)
            .collect(Collectors.toList());
    }
    
    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
    
    private UserStationDTO toListDTO(Station station) {
        UserStationDTO dto = new UserStationDTO();
        dto.setId(station.getStationId());
        dto.setStationId(station.getStationId());
        dto.setName(station.getName());
        dto.setType(station.getType() != null ? station.getType().name() : "battery");
        dto.setAddress(station.getAddress());
        dto.setLatitude(station.getLatitude());
        dto.setLongitude(station.getLongitude());
        dto.setRating(station.getRating());
        dto.setTotalSwaps(station.getTotalSwaps());
        dto.setAvailableBatteries(station.getAvailableBatteries());
        dto.setAvailableSlots(station.getAvailableSlots());
        dto.setServiceTime(station.getServiceTime());
        dto.setStatus(station.getStatus() != null ? station.getStatus().name() : "active");
        dto.setContactPhone(station.getContactPhone());
        dto.setManager(station.getManager());
        
        List<StationPhoto> photos = stationPhotoRepository.findByStationIdAndType(station.getId(), StationPhoto.PhotoType.main);
        if (!photos.isEmpty()) {
            dto.setPhotos(photos.stream().map(this::toPhotoDTO).collect(Collectors.toList()));
        }
        
        return dto;
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
        dto.setFacilities(new ArrayList<>());
        dto.setPhotos(new ArrayList<>());
        dto.setServices(new ArrayList<>());
        
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
    
    private StationServiceDTO toServiceDTO(StationServiceInfo service) {
        StationServiceDTO dto = new StationServiceDTO();
        dto.setName(service.getName());
        dto.setPrice(service.getPrice());
        dto.setDuration(service.getDuration());
        return dto;
    }
}
