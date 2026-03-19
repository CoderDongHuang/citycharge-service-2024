package com.citycharge.dto;

import com.citycharge.entity.Vehicle;
import java.time.LocalDateTime;

public class VehicleDTO {
    private String vid;
    private String pid;
    private Double voltage;
    private Double temperature;
    private Double batteryLevel;
    private String lightStatus;
    private Position position;
    private Boolean online;
    private LocalDateTime lastUpdate;
    
    public static class Position {
        private Integer x;
        private Integer y;
        
        public Integer getX() { return x; }
        public void setX(Integer x) { this.x = x; }
        
        public Integer getY() { return y; }
        public void setY(Integer y) { this.y = y; }
    }
    
    public static VehicleDTO fromEntity(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setVid(vehicle.getVid());
        dto.setPid(vehicle.getPid());
        dto.setVoltage(vehicle.getVoltage());
        dto.setTemperature(vehicle.getTemperature());
        dto.setBatteryLevel(vehicle.getBatteryLevel());
        dto.setLightStatus(vehicle.getLightStatus() != null ? vehicle.getLightStatus().name() : "off");
        
        Position position = new Position();
        position.setX(vehicle.getPositionX());
        position.setY(vehicle.getPositionY());
        dto.setPosition(position);
        
        dto.setOnline(vehicle.getOnlineStatus());
        dto.setLastUpdate(vehicle.getLastUpdate());
        
        return dto;
    }

    // Getter and Setter methods
    public String getVid() { return vid; }
    public void setVid(String vid) { this.vid = vid; }
    
    public String getPid() { return pid; }
    public void setPid(String pid) { this.pid = pid; }
    
    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Double batteryLevel) { this.batteryLevel = batteryLevel; }
    
    public String getLightStatus() { return lightStatus; }
    public void setLightStatus(String lightStatus) { this.lightStatus = lightStatus; }
    
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    
    public Boolean getOnline() { return online; }
    public void setOnline(Boolean online) { this.online = online; }
    
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
}