package com.citycharge.config;

import com.citycharge.entity.AlarmRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 原生 WebSocket 配置
 * 端点：ws://localhost:8080/websocket
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final ObjectMapper objectMapper;
    
    public WebSocketConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(alarmWebSocketHandler(), "/websocket")
                .setAllowedOriginPatterns("*");
    }
    
    @Bean
    public WebSocketHandler alarmWebSocketHandler() {
        return new AlarmWebSocketHandler();
    }
    
    /**
     * WebSocket 处理器
     */
    private class AlarmWebSocketHandler extends TextWebSocketHandler {
        
        private final Map<String, org.springframework.web.socket.WebSocketSession> sessions = new ConcurrentHashMap<>();
        
        @Override
        public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) {
            sessions.put(session.getId(), session);
            log.info("WebSocket 连接建立：{}", session.getId());
        }
        
        @Override
        public void afterConnectionClosed(org.springframework.web.socket.WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
            sessions.remove(session.getId());
            log.info("WebSocket 连接关闭：{}", session.getId());
        }
        
        @Override
        protected void handleTextMessage(org.springframework.web.socket.WebSocketSession session, org.springframework.web.socket.TextMessage message) {
            log.info("收到 WebSocket 消息：{}", message.getPayload());
        }
        
        @Override
        public void handleTransportError(org.springframework.web.socket.WebSocketSession session, Throwable exception) {
            log.error("WebSocket 错误：{}", exception.getMessage());
        }
        
        /**
         * 推送新报警
         */
        public void sendNewAlarm(AlarmRecord alarmRecord) {
            try {
                Map<String, Object> msg = objectMapper.convertValue(alarmRecord, Map.class);
                msg.put("type", "new_alarm");
                org.springframework.web.socket.TextMessage textMessage = new org.springframework.web.socket.TextMessage(objectMapper.writeValueAsString(msg));
                
                for (org.springframework.web.socket.WebSocketSession session : sessions.values()) {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                }
                
                log.info("推送新报警到 WebSocket: {}", alarmRecord.getId());
                
            } catch (IOException e) {
                log.error("推送报警到 WebSocket 失败：{}", e.getMessage(), e);
            }
        }
        
        /**
         * 推送报警处理状态更新
         */
        public void sendAlarmHandled(AlarmRecord alarmRecord) {
            try {
                Map<String, Object> msg = objectMapper.convertValue(alarmRecord, Map.class);
                msg.put("type", "alarm_handled");
                org.springframework.web.socket.TextMessage textMessage = new org.springframework.web.socket.TextMessage(objectMapper.writeValueAsString(msg));
                
                for (org.springframework.web.socket.WebSocketSession session : sessions.values()) {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                }
                
                log.info("推送报警处理状态更新：{}", alarmRecord.getId());
                
            } catch (IOException e) {
                log.error("推送报警处理状态更新失败：{}", e.getMessage(), e);
            }
        }
    }
}
