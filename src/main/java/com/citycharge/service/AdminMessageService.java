package com.citycharge.service;

import com.citycharge.dto.*;
import com.citycharge.entity.Message;
import com.citycharge.entity.User;
import com.citycharge.entity.UserBattery;
import com.citycharge.entity.UserVehicle;
import com.citycharge.repository.MessageRepository;
import com.citycharge.repository.UserBatteryRepository;
import com.citycharge.repository.UserRepository;
import com.citycharge.repository.UserVehicleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserVehicleRepository userVehicleRepository;
    private final UserBatteryRepository userBatteryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Transactional
    public SendMessageResponseDTO sendMessage(SendMessageRequestDTO request, Long adminId, String adminName) {
        List<Long> targetUserIds = getTargetUserIds(request.getTargetType(), request.getTargetIds());
        
        if (targetUserIds.isEmpty()) {
            throw new RuntimeException("没有找到目标用户");
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sentTime = request.getScheduledTime() != null ? null : now;
        Message.SendStatus sendStatus = request.getScheduledTime() != null ? 
                Message.SendStatus.scheduled : Message.SendStatus.sent;
        
        List<Message> messages = new ArrayList<>();
        for (Long userId : targetUserIds) {
            Message message = new Message();
            message.setTitle(request.getTitle());
            message.setContent(request.getContent());
            message.setCategory(Message.MessageCategory.valueOf(request.getCategory()));
            message.setSource(Message.MessageSource.admin);
            message.setSourceType("管理后台");
            message.setPriority(request.getPriority() != null ? request.getPriority() : 2);
            message.setUserId(userId);
            message.setExtraData(request.getExtraData());
            message.setIsRead(false);
            message.setSendStatus(sendStatus);
            message.setScheduledTime(request.getScheduledTime());
            message.setSentTime(sentTime);
            message.setAdminId(adminId);
            message.setAdminName(adminName);
            message.setTargetType(request.getTargetType());
            messages.add(message);
        }
        
        List<Message> savedMessages = messageRepository.saveAll(messages);
        
        SendMessageResponseDTO response = new SendMessageResponseDTO();
        response.setMessageId(savedMessages.get(0).getId());
        response.setRecipientCount((long) targetUserIds.size());
        response.setStatus(sendStatus.name());
        response.setEstimatedDeliveryTime(request.getScheduledTime() != null ? request.getScheduledTime() : now);
        
        return response;
    }
    
    public Map<String, Object> getMessages(Integer page, Integer size, String category, String source,
                                            String sendStatus, LocalDateTime startTime, LocalDateTime endTime) {
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 10,
                Sort.by("createTime").descending());
        
        Message.MessageCategory cat = category != null && !category.isEmpty() ? 
                Message.MessageCategory.valueOf(category) : null;
        Message.MessageSource src = source != null && !source.isEmpty() ? 
                Message.MessageSource.valueOf(source) : null;
        Message.SendStatus status = sendStatus != null && !sendStatus.isEmpty() ? 
                Message.SendStatus.valueOf(sendStatus) : null;
        
        Page<Message> messagePage = messageRepository.findAdminMessages(cat, src, status, startTime, endTime, pageable);
        
        List<AdminMessageDTO> content = messagePage.getContent().stream()
                .map(this::toAdminMessageDTO)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page != null ? page : 1);
        pagination.put("pageSize", size != null ? size : 10);
        pagination.put("totalElements", messagePage.getTotalElements());
        pagination.put("totalPages", messagePage.getTotalPages());
        result.put("pagination", pagination);
        
        return result;
    }
    
    public AdminMessageDetailDTO getMessageDetail(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        return toAdminMessageDetailDTO(message);
    }
    
    @Transactional
    public Map<String, Object> cancelMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        if (message.getSendStatus() != Message.SendStatus.scheduled) {
            throw new RuntimeException("只能取消定时发送的消息");
        }
        
        messageRepository.updateSendStatus(messageId, Message.SendStatus.cancelled);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", messageId);
        result.put("sendStatus", Message.SendStatus.cancelled.name());
        
        return result;
    }
    
    @Transactional
    public SendMessageResponseDTO resendMessage(Long messageId, Long adminId, String adminName) {
        Message originalMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        SendMessageRequestDTO request = new SendMessageRequestDTO();
        request.setTitle(originalMessage.getTitle());
        request.setContent(originalMessage.getContent());
        request.setCategory(originalMessage.getCategory().name());
        request.setTargetType(originalMessage.getTargetType());
        request.setPriority(originalMessage.getPriority());
        request.setExtraData(originalMessage.getExtraData());
        
        List<Long> targetUserIds = getTargetUserIds(originalMessage.getTargetType(), new ArrayList<>());
        request.setTargetIds(targetUserIds);
        
        return sendMessage(request, adminId, adminName);
    }
    
    private List<Long> getTargetUserIds(String targetType, List<Long> targetIds) {
        List<Long> userIds = new ArrayList<>();
        
        switch (targetType) {
            case "all":
                List<User> allUsers = userRepository.findAll();
                userIds = allUsers.stream().map(User::getId).collect(Collectors.toList());
                break;
            case "user":
                if (targetIds != null && !targetIds.isEmpty()) {
                    userIds.addAll(targetIds);
                }
                break;
            case "vehicle":
                if (targetIds != null && !targetIds.isEmpty()) {
                    List<UserVehicle> vehicles = userVehicleRepository.findAllById(targetIds);
                    userIds = vehicles.stream().map(UserVehicle::getUserId).distinct().collect(Collectors.toList());
                }
                break;
            case "battery":
                if (targetIds != null && !targetIds.isEmpty()) {
                    List<UserBattery> batteries = userBatteryRepository.findAllById(targetIds);
                    userIds = batteries.stream().map(UserBattery::getUserId).distinct().collect(Collectors.toList());
                }
                break;
            default:
                throw new RuntimeException("不支持的目标类型: " + targetType);
        }
        
        return userIds;
    }
    
    private AdminMessageDTO toAdminMessageDTO(Message message) {
        AdminMessageDTO dto = new AdminMessageDTO();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setCategory(message.getCategory() != null ? message.getCategory().name() : null);
        dto.setSource(message.getSource() != null ? message.getSource().name() : null);
        dto.setTargetType(message.getTargetType());
        dto.setPriority(message.getPriority());
        dto.setSendStatus(message.getSendStatus() != null ? message.getSendStatus().name() : null);
        dto.setAdminName(message.getAdminName());
        dto.setCreateTime(message.getCreateTime());
        dto.setSentTime(message.getSentTime());
        
        long recipientCount = messageRepository.countRecipientsByMessageId(message.getId());
        dto.setRecipientCount(recipientCount);
        
        return dto;
    }
    
    private AdminMessageDetailDTO toAdminMessageDetailDTO(Message message) {
        AdminMessageDetailDTO dto = new AdminMessageDetailDTO();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setContent(message.getContent());
        dto.setCategory(message.getCategory() != null ? message.getCategory().name() : null);
        dto.setSource(message.getSource() != null ? message.getSource().name() : null);
        dto.setTargetType(message.getTargetType());
        dto.setPriority(message.getPriority());
        dto.setSendStatus(message.getSendStatus() != null ? message.getSendStatus().name() : null);
        dto.setScheduledTime(message.getScheduledTime());
        dto.setSentTime(message.getSentTime());
        dto.setAdminId(message.getAdminId());
        dto.setAdminName(message.getAdminName());
        dto.setExtraData(message.getExtraData());
        dto.setCreateTime(message.getCreateTime());
        
        long recipientCount = messageRepository.countRecipientsByMessageId(message.getId());
        dto.setRecipientCount(recipientCount);
        
        long readCount = messageRepository.countReadByMessageId(message.getId());
        dto.setReadCount(readCount);
        
        return dto;
    }
}
