package com.citycharge.service;

import com.citycharge.dto.AlertNotifyMessage;
import com.citycharge.dto.SwapCompleteMessage;
import com.citycharge.entity.Message;
import com.citycharge.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageMqttService {
    
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    @Transactional
    public void handleSwapCompleteMessage(String topic, String payload) {
        try {
            SwapCompleteMessage swapMsg = objectMapper.readValue(payload, SwapCompleteMessage.class);
            log.info("处理换电完成消息 - 用户ID: {}, 站点: {}", swapMsg.getUserId(), swapMsg.getStationName());
            
            Message message = createSwapMessage(swapMsg);
            messageRepository.save(message);
            
            log.info("换电消息已保存到数据库 - 消息ID: {}", message.getId());
            
        } catch (Exception e) {
            log.error("处理换电完成消息失败: {}", e.getMessage(), e);
        }
    }
    
    @Transactional
    public void handleAlertNotifyMessage(String topic, String payload) {
        try {
            AlertNotifyMessage alertMsg = objectMapper.readValue(payload, AlertNotifyMessage.class);
            log.info("处理报警通知消息 - 用户ID: {}, 报警类型: {}", alertMsg.getUserId(), alertMsg.getAlertType());
            
            Message message = createAlertMessage(alertMsg);
            messageRepository.save(message);
            
            log.info("报警消息已保存到数据库 - 消息ID: {}", message.getId());
            
        } catch (Exception e) {
            log.error("处理报警通知消息失败: {}", e.getMessage(), e);
        }
    }
    
    private Message createSwapMessage(SwapCompleteMessage swapMsg) {
        Message message = new Message();
        
        message.setTitle("换电完成提醒");
        
        String content = String.format(
            "您的车辆（%s）已在%s完成换电，新电池电量%d%%，续航约%d公里。",
            swapMsg.getVehiclePlate() != null ? swapMsg.getVehiclePlate() : "未知车牌",
            swapMsg.getStationName() != null ? swapMsg.getStationName() : "未知站点",
            swapMsg.getNewBatteryLevel() != null ? swapMsg.getNewBatteryLevel() : 0,
            swapMsg.getEstimatedRange() != null ? swapMsg.getEstimatedRange() : 0
        );
        message.setContent(content);
        
        message.setCategory(Message.MessageCategory.swap);
        message.setSource(Message.MessageSource.hardware);
        message.setSourceType("换电站");
        
        int priority = determineSwapPriority(swapMsg);
        message.setPriority(priority);
        
        message.setUserId(swapMsg.getUserId());
        message.setVehicleId(swapMsg.getVehicleId());
        message.setBatteryId(swapMsg.getBatteryId());
        message.setStationId(swapMsg.getStationId());
        
        String extraDataJson = buildSwapExtraData(swapMsg);
        message.setExtraData(extraDataJson);
        
        message.setIsRead(false);
        message.setSendStatus(Message.SendStatus.sent);
        message.setSentTime(LocalDateTime.now());
        
        return message;
    }
    
    private Message createAlertMessage(AlertNotifyMessage alertMsg) {
        Message message = new Message();
        
        String title = alertMsg.getTitle();
        if (title == null || title.isEmpty()) {
            title = getAlertTitle(alertMsg.getAlertType());
        }
        message.setTitle(title);
        
        String content = alertMsg.getMessage();
        if (content == null || content.isEmpty()) {
            content = buildAlertContent(alertMsg);
        }
        message.setContent(content);
        
        message.setCategory(Message.MessageCategory.alert);
        message.setSource(Message.MessageSource.hardware);
        message.setSourceType(getAlertSourceType(alertMsg.getAlertType()));
        
        int priority = determineAlertPriority(alertMsg.getAlertLevel());
        message.setPriority(priority);
        
        message.setUserId(alertMsg.getUserId());
        message.setVehicleId(alertMsg.getVehicleId());
        message.setBatteryId(alertMsg.getBatteryId());
        
        String extraDataJson = buildAlertExtraData(alertMsg);
        message.setExtraData(extraDataJson);
        
        message.setIsRead(false);
        message.setSendStatus(Message.SendStatus.sent);
        message.setSentTime(LocalDateTime.now());
        
        return message;
    }
    
    private String buildSwapExtraData(SwapCompleteMessage swapMsg) {
        try {
            Map<String, Object> extraData = new HashMap<>();
            
            if (swapMsg.getStationName() != null) {
                extraData.put("stationName", swapMsg.getStationName());
            }
            if (swapMsg.getNewBatteryLevel() != null) {
                extraData.put("batteryLevel", swapMsg.getNewBatteryLevel());
            }
            if (swapMsg.getEstimatedRange() != null) {
                extraData.put("range", swapMsg.getEstimatedRange());
            }
            if (swapMsg.getSwapTime() != null) {
                extraData.put("swapTime", swapMsg.getSwapTime());
            }
            if (swapMsg.getOldBatteryLevel() != null) {
                extraData.put("oldBatteryLevel", swapMsg.getOldBatteryLevel());
            }
            if (swapMsg.getAmount() != null) {
                extraData.put("amount", swapMsg.getAmount());
            }
            if (swapMsg.getOrderId() != null) {
                extraData.put("orderId", swapMsg.getOrderId());
            }
            if (swapMsg.getBatteryModel() != null) {
                extraData.put("batteryModel", swapMsg.getBatteryModel());
            }
            
            return objectMapper.writeValueAsString(extraData);
        } catch (JsonProcessingException e) {
            log.error("构建换电消息扩展数据失败: {}", e.getMessage());
            return null;
        }
    }
    
    private String buildAlertExtraData(AlertNotifyMessage alertMsg) {
        try {
            Map<String, Object> extraData = new HashMap<>();
            
            extraData.put("alertType", alertMsg.getAlertType());
            
            if (alertMsg.getAlertLevel() != null) {
                extraData.put("alertLevel", alertMsg.getAlertLevel());
            }
            if (alertMsg.getTriggerValue() != null) {
                extraData.put("triggerValue", alertMsg.getTriggerValue());
            }
            if (alertMsg.getThresholdValue() != null) {
                extraData.put("thresholdValue", alertMsg.getThresholdValue());
            }
            if (alertMsg.getVoltage() != null) {
                extraData.put("voltage", alertMsg.getVoltage());
            }
            if (alertMsg.getTemperature() != null) {
                extraData.put("temperature", alertMsg.getTemperature());
            }
            if (alertMsg.getBatteryLevel() != null) {
                extraData.put("batteryLevel", alertMsg.getBatteryLevel());
            }
            if (alertMsg.getLatitude() != null && alertMsg.getLongitude() != null) {
                extraData.put("location", alertMsg.getLocationName());
            }
            if (alertMsg.getVehiclePlate() != null) {
                extraData.put("vehiclePlate", alertMsg.getVehiclePlate());
            }
            if (alertMsg.getBatteryModel() != null) {
                extraData.put("batteryModel", alertMsg.getBatteryModel());
            }
            
            return objectMapper.writeValueAsString(extraData);
        } catch (JsonProcessingException e) {
            log.error("构建报警消息扩展数据失败: {}", e.getMessage());
            return null;
        }
    }
    
    private int determineSwapPriority(SwapCompleteMessage swapMsg) {
        return 3;
    }
    
    private int determineAlertPriority(String alertLevel) {
        if (alertLevel == null) {
            return 2;
        }
        
        switch (alertLevel.toLowerCase()) {
            case "critical":
                return 4;
            case "high":
                return 3;
            case "medium":
                return 2;
            case "low":
                return 1;
            default:
                return 2;
        }
    }
    
    private String getAlertTitle(String alertType) {
        if (alertType == null) {
            return "设备报警";
        }
        
        switch (alertType.toLowerCase()) {
            case "lowbattery":
            case "low_battery":
                return "电池电量过低警告";
            case "temperature":
            case "overheat":
                return "电池温度过高警告";
            case "lowvoltage":
            case "low_voltage":
                return "电池电压过低警告";
            case "unreachable":
                return "车辆离线警告";
            default:
                return "设备报警";
        }
    }
    
    private String getAlertSourceType(String alertType) {
        if (alertType == null) {
            return "硬件设备";
        }
        
        switch (alertType.toLowerCase()) {
            case "lowbattery":
            case "low_battery":
            case "lowvoltage":
            case "low_voltage":
                return "BMS";
            case "temperature":
            case "overheat":
                return "温度传感器";
            case "unreachable":
                return "通信模块";
            default:
                return "硬件设备";
        }
    }
    
    private String buildAlertContent(AlertNotifyMessage alertMsg) {
        StringBuilder content = new StringBuilder();
        
        String vehiclePlate = alertMsg.getVehiclePlate() != null ? 
            "（" + alertMsg.getVehiclePlate() + "）" : "";
        
        switch (alertMsg.getAlertType() != null ? alertMsg.getAlertType().toLowerCase() : "") {
            case "lowbattery":
            case "low_battery":
                content.append(String.format("您的车辆%s电池电量已低于%d%%，请及时充电或前往换电站。", 
                    vehiclePlate, 
                    alertMsg.getTriggerValue() != null ? alertMsg.getTriggerValue().intValue() : 20));
                break;
            case "temperature":
            case "overheat":
                content.append(String.format("您的车辆%s电池温度已达到%.1f℃，超过安全阈值%.1f℃，请立即处理。", 
                    vehiclePlate,
                    alertMsg.getTriggerValue() != null ? alertMsg.getTriggerValue() : 0,
                    alertMsg.getThresholdValue() != null ? alertMsg.getThresholdValue() : 60));
                break;
            case "lowvoltage":
            case "low_voltage":
                content.append(String.format("您的车辆%s电池电压已降至%.1fV，低于安全阈值%.1fV，请检查电池状态。", 
                    vehiclePlate,
                    alertMsg.getTriggerValue() != null ? alertMsg.getTriggerValue() : 0,
                    alertMsg.getThresholdValue() != null ? alertMsg.getThresholdValue() : 48));
                break;
            case "unreachable":
                content.append(String.format("您的车辆%s已离线超过3小时，请检查车辆状态。", vehiclePlate));
                break;
            default:
                content.append(String.format("您的车辆%s检测到异常，请及时检查。", vehiclePlate));
        }
        
        if (alertMsg.getLocationName() != null) {
            content.append(" 位置：").append(alertMsg.getLocationName());
        }
        
        return content.toString();
    }
}
