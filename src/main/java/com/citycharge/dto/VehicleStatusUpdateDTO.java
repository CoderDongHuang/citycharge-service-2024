package com.citycharge.dto;

public class VehicleStatusUpdateDTO {
    private Double voltage;
    private Double temperature;
    private Double batteryLevel;
    private String lightStatus;
    private Position position;
    private Boolean online;
    
    public static class Position {
        private Integer x;
        private Integer y;
        
        public Integer getX() { return x; }
        public void setX(Integer x) { this.x = x; }
        
        public Integer getY() { return y; }
        public void setY(Integer y) { this.y = y; }
    }
    
    // Getter and Setter methods
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
    public void setOnline(Boolean online) { this. online = online; }
}