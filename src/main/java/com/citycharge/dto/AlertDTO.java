package com.citycharge.dto;

import com.citycharge.entity.AlarmRecord;
import java.time.LocalDateTime;

public class AlertDTO {
    private Long id;
    private String type;
    private String vid;
    private LocalDateTime timestamp;
    private Boolean resolved;
    private String message;
    
    public static AlertDTO fromEntity(AlarmRecord alarmRecord) {
        AlertDTO dto = new AlertDTO();
        dto.setId(alarmRecord.getId());
        dto.setType(alarmRecord.getAlarmType());
        dto.setVid(alarmRecord.getVehicleVid());
        dto.setTimestamp(alarmRecord.getAlarmTime());
        dto.setResolved(alarmRecord.getIsResolved());
        dto.setMessage(alarmRecord.getAlarmMessage());
        return dto;
    }
    
    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getVid() { return vid; }
    public void setVid(String vid) { this.vid = vid; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Boolean getResolved() { return resolved; }
    public void setResolved(Boolean resolved) { this.resolved = resolved; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}