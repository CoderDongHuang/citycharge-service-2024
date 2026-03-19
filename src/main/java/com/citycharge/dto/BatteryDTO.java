package com.citycharge.dto;

import com.citycharge.entity.Battery;
import java.time.LocalDateTime;
import java.util.List;

public class BatteryDTO {
    private String pid;
    private String status;
    private String currentVehicle;
    private Double voltage;
    private Double temperature;
    private Double remainingCapacity;
    private Double health;
    private List<BatteryHistoryDTO> history;
    
    public static BatteryDTO fromEntity(Battery battery) {
        BatteryDTO dto = new BatteryDTO();
        dto.setPid(battery.getPid());
        dto.setStatus(battery.getStatus() != null ? battery.getStatus().name() : "available");
        dto.setCurrentVehicle(battery.getCurrentVehicle());
        dto.setVoltage(battery.getVoltage());
        dto.setTemperature(battery.getTemperature());
        dto.setRemainingCapacity(battery.getRemainingCapacity());
        dto.setHealth(battery.getHealth());
        return dto;
    }
    
    public static class BatteryHistoryDTO {
        private LocalDateTime timestamp;
        private String vid;
        private Double capacity;
        private Double voltage;
        private Double temperature;
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public String getVid() { return vid; }
        public void setVid(String vid) { this.vid = vid; }
        
        public Double getCapacity() { return capacity; }
        public void setCapacity(Double capacity) { this.capacity = capacity; }
        
        public Double getVoltage() { return voltage; }
        public void setVoltage(Double voltage) { this.voltage = voltage; }
        
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
    }
    
    // Getter and Setter methods
    public String getPid() { return pid; }
    public void setPid(String pid) { this.pid = pid; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCurrentVehicle() { return currentVehicle; }
    public void setCurrentVehicle(String currentVehicle) { this.currentVehicle = currentVehicle; }
    
    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getRemainingCapacity() { return remainingCapacity; }
    public void setRemainingCapacity(Double remainingCapacity) { this.remainingCapacity = remainingCapacity; }
    
    public Double getHealth() { return health; }
    public void setHealth(Double health) { this.health = health; }
    
    public List<BatteryHistoryDTO> getHistory() { return history; }
    public void setHistory(List<BatteryHistoryDTO> history) { this.history = history; }
}