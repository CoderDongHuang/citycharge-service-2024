package com.citycharge.dto;

import lombok.Data;

@Data
public class AdminStatisticsDTO {
    private VehicleStatistics vehicles;
    private BatteryStatistics batteries;
    
    @Data
    public static class VehicleStatistics {
        private long total;
        private long online;
        private long offline;
        private long active;
        private long maintenance;
    }
    
    @Data
    public static class BatteryStatistics {
        private long total;
        private long online;
        private long offline;
        private long inUse;
        private long available;
        private long maintenance;
        private long lowVoltage;
        private long overheat;
    }
}
