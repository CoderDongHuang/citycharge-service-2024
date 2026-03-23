package com.citycharge.config;

import com.citycharge.service.MqttVehicleStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import javax.annotation.PreDestroy;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MqttMessageListenerConfig {
    
    private final MqttVehicleStatusService vehicleStatusService;
    private MqttClient mqttClient;
    
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            // 直接创建MQTT客户端，不使用@Bean注入
            String clientId = "citycharge-backend-" + System.currentTimeMillis();
            MemoryPersistence persistence = new MemoryPersistence();
            
            this.mqttClient = new MqttClient("tcp://localhost:1883", clientId, persistence);
            
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(60);
            options.setAutomaticReconnect(true);
            
            mqttClient.connect(options);
            log.info("MQTT客户端连接成功，Client ID: {}", clientId);
            
            // 订阅车辆状态主题
            String vehicleStatusTopic = "vehicle/+/status";
            mqttClient.subscribe(vehicleStatusTopic, 1, (topic, message) -> {
                handleVehicleStatusMessage(topic, message);
            });
            
            log.info("MQTT订阅成功，主题: {}", vehicleStatusTopic);
            
        } catch (Exception e) {
            log.error("MQTT初始化失败: {}", e.getMessage(), e);
        }
    }
    
    private void handleVehicleStatusMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            log.info("收到MQTT消息，主题: {}, 内容: {}", topic, payload);
            vehicleStatusService.handleVehicleStatusMessage(topic, payload);
        } catch (Exception e) {
            log.error("处理MQTT消息失败，主题: {}, 错误: {}", topic, e.getMessage(), e);
        }
    }
    
    /**
     * 获取MQTT客户端实例
     */
    public MqttClient getMqttClient() {
        return mqttClient;
    }
    
    @PreDestroy
    public void destroy() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                log.info("MQTT客户端已断开连接");
            } catch (Exception e) {
                log.error("MQTT客户端断开连接失败: {}", e.getMessage());
            }
        }
    }
}