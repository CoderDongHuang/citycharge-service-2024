package com.citycharge.dto;

public class ControlCommandDTO {
    private String vid;
    private String commandType; // HEADLIGHT_FLASH, HORN_BEEP
    private Integer duration;
    private Integer times;
    
    // Getter and Setter methods
    public String getVid() { return vid; }
    public void setVid(String vid) { this.vid = vid; }
    
    public String getCommandType() { return commandType; }
    public void setCommandType(String commandType) { this.commandType = commandType; }
    
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public Integer getTimes() { return times; }
    public void setTimes(Integer times) { this.times = times; }
}