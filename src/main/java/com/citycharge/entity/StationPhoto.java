package com.citycharge.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "station_photo")
public class StationPhoto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "photo_id", unique = true, nullable = false, length = 50)
    private String photoId;
    
    @Column(name = "station_id", nullable = false)
    private Long stationId;
    
    @Column(name = "url", nullable = false, length = 500)
    private String url;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private PhotoType type = PhotoType.main;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "upload_time")
    private LocalDateTime uploadTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum PhotoType {
        main, slot, battery, environment, guide
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        uploadTime = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPhotoId() { return photoId; }
    public void setPhotoId(String photoId) { this.photoId = photoId; }
    
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public PhotoType getType() { return type; }
    public void setType(PhotoType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
