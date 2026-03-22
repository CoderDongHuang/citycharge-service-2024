package com.citycharge.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        
        // MQTT Broker配置
        options.setServerURIs(new String[]{"tcp://localhost:1883"});
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
        
        // 认证配置（如果需要）
        // options.setUserName("username");
        // options.setPassword("password".toCharArray());
        
        return options;
    }
}