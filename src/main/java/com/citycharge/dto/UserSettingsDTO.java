package com.citycharge.dto;

public class UserSettingsDTO {
    private Boolean notifications;
    private Boolean darkMode;

    public Boolean getNotifications() { return notifications; }
    public void setNotifications(Boolean notifications) { this.notifications = notifications; }
    
    public Boolean getDarkMode() { return darkMode; }
    public void setDarkMode(Boolean darkMode) { this.darkMode = darkMode; }
}
