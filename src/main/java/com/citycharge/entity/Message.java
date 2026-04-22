package com.citycharge.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageSource source;
    
    @Column(name = "source_type", length = 50)
    private String sourceType;
    
    @Column(nullable = false)
    private Integer priority = 2;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "vehicle_id")
    private Long vehicleId;
    
    @Column(name = "battery_id")
    private Long batteryId;
    
    @Column(name = "station_id")
    private Long stationId;
    
    @Column(name = "extra_data", columnDefinition = "JSON")
    private String extraData;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "read_time")
    private LocalDateTime readTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "send_status")
    private SendStatus sendStatus = SendStatus.sent;
    
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    
    @Column(name = "sent_time")
    private LocalDateTime sentTime;
    
    @Column(name = "admin_id")
    private Long adminId;
    
    @Column(name = "admin_name", length = 100)
    private String adminName;
    
    @Column(name = "target_type", length = 20)
    private String targetType;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (sentTime == null && sendStatus == SendStatus.sent) {
            sentTime = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
    
    public enum MessageCategory {
        system, swap, alert, activity
    }
    
    public enum MessageSource {
        admin, hardware
    }
    
    public enum SendStatus {
        draft, scheduled, sent, cancelled
    }
}
