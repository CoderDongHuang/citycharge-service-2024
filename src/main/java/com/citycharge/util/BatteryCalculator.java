package com.citycharge.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatteryCalculator {
    
    @Value("${citycharge.battery.min-voltage:3.0}")
    private Double minVoltage;
    
    @Value("${citycharge.battery.max-voltage:4.2}")
    private Double maxVoltage;
    
    public Double calculateCapacityPercentage(Double currentVoltage) {
        if (currentVoltage <= minVoltage) {
            return 0.0;
        }
        if (currentVoltage >= maxVoltage) {
            return 1.0;
        }
        return (currentVoltage - minVoltage) / (maxVoltage - minVoltage);
    }
    
    public Double getMinVoltage() {
        return minVoltage;
    }
    
    public Double getMaxVoltage() {
        return maxVoltage;
    }
}