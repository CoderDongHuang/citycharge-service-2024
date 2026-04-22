package com.citycharge.service;

import com.citycharge.dto.*;
import com.citycharge.entity.Message;
import com.citycharge.entity.UserVehicle;
import com.citycharge.repository.MessageRepository;
import com.citycharge.repository.UserVehicleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
public class UserMessageService {
    
    private final MessageRepository messageRepository;
    private final UserVehicleRepository userVehicleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public UserMessageListResponseDTO getMessages(Long userId, Integer page, Integer size, 
                                                    String category, String source, Boolean isRead, String sort) {
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 10,
                Sort.by("priority").descending().and(Sort.by("createTime").descending()));
        
        Page<Message> messagePage;
        
        if (category != null && !category.isEmpty()) {
            Message.MessageCategory cat = Message.MessageCategory.valueOf(category);
            if (isRead != null) {
                messagePage = messageRepository.findByUserIdAndCategoryAndIsRead(userId, cat, isRead, pageable);
            } else {
                messagePage = messageRepository.findByUserIdAndCategory(userId, cat, pageable);
            }
        } else if (source != null && !source.isEmpty()) {
            Message.MessageSource src = Message.MessageSource.valueOf(source);
            messagePage = messageRepository.findByUserIdAndSource(userId, src, pageable);
        } else if (isRead != null) {
            messagePage = messageRepository.findByUserIdAndIsRead(userId, isRead, pageable);
        } else {
            messagePage = messageRepository.findByUserId(userId, pageable);
        }
        
        List<UserMessageDTO> content = messagePage.getContent().stream()
                .map(this::toUserMessageDTO)
                .collect(Collectors.toList());
        
        UserMessageListResponseDTO response = new UserMessageListResponseDTO();
        response.setContent(content);
        
        UserMessageListResponseDTO.Pagination pagination = new UserMessageListResponseDTO.Pagination();
        pagination.setCurrentPage(page != null ? page : 1);
        pagination.setPageSize(size != null ? size : 10);
        pagination.setTotalElements(messagePage.getTotalElements());
        pagination.setTotalPages(messagePage.getTotalPages());
        response.setPagination(pagination);
        
        UserMessageListResponseDTO.Statistics statistics = buildStatistics(userId);
        response.setStatistics(statistics);
        
        return response;
    }
    
    public UnreadCountDTO getUnreadCount(Long userId) {
        UnreadCountDTO dto = new UnreadCountDTO();
        
        long totalCount = messageRepository.countByUserIdAndIsRead(userId, false);
        dto.setTotalCount(totalCount);
        
        Map<String, Long> byCategory = new HashMap<>();
        for (Message.MessageCategory cat : Message.MessageCategory.values()) {
            long count = messageRepository.countByUserIdAndCategoryAndIsRead(userId, cat, false);
            byCategory.put(cat.name(), count);
        }
        dto.setByCategory(byCategory);
        
        Map<String, Long> bySource = new HashMap<>();
        List<Object[]> sourceCounts = messageRepository.countUnreadBySourceForUser(userId);
        for (Object[] row : sourceCounts) {
            Message.MessageSource src = (Message.MessageSource) row[0];
            Long count = (Long) row[1];
            bySource.put(src.name(), count);
        }
        for (Message.MessageSource src : Message.MessageSource.values()) {
            if (!bySource.containsKey(src.name())) {
                bySource.put(src.name(), 0L);
            }
        }
        dto.setBySource(bySource);
        
        return dto;
    }
    
    public UserMessageDetailDTO getMessageDetail(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        if (!message.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此消息");
        }
        
        return toUserMessageDetailDTO(message);
    }
    
    @Transactional
    public Map<String, Object> markAsRead(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        if (!message.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此消息");
        }
        
        LocalDateTime readTime = LocalDateTime.now();
        int updated = messageRepository.markAsRead(messageId, userId, readTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", messageId);
        result.put("isRead", updated > 0);
        result.put("readTime", readTime);
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> markAsReadBatch(Long userId, List<Long> messageIds) {
        LocalDateTime readTime = LocalDateTime.now();
        int updated = messageRepository.markAsReadBatch(messageIds, userId, readTime);
        
        List<Long> failedIds = new ArrayList<>(messageIds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", updated);
        result.put("failedIds", failedIds.subList(updated, failedIds.size()));
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> deleteMessage(Long userId, Long messageId) {
        int deleted = messageRepository.deleteByUser(messageId, userId);
        
        if (deleted == 0) {
            throw new RuntimeException("消息不存在或无权删除");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", messageId);
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> deleteMessageBatch(Long userId, List<Long> messageIds) {
        int deleted = messageRepository.deleteBatchByUser(messageIds, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", deleted);
        result.put("failedIds", messageIds.subList(deleted, messageIds.size()));
        
        return result;
    }
    
    private UserMessageDTO toUserMessageDTO(Message message) {
        UserMessageDTO dto = new UserMessageDTO();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setContent(message.getContent());
        dto.setCategory(message.getCategory() != null ? message.getCategory().name() : null);
        dto.setSource(message.getSource() != null ? message.getSource().name() : null);
        dto.setSourceType(message.getSourceType());
        dto.setPriority(message.getPriority());
        dto.setIsRead(message.getIsRead());
        dto.setReadTime(message.getReadTime());
        dto.setCreateTime(message.getCreateTime());
        dto.setVehicleId(message.getVehicleId());
        dto.setBatteryId(message.getBatteryId());
        dto.setStationId(message.getStationId());
        dto.setExtraData(message.getExtraData());
        dto.setAdminName(message.getAdminName());
        
        if (message.getVehicleId() != null) {
            Optional<UserVehicle> vehicle = userVehicleRepository.findById(message.getVehicleId());
            vehicle.ifPresent(v -> dto.setVehicleNo(v.getPlateNumber()));
        }
        
        return dto;
    }
    
    private UserMessageDetailDTO toUserMessageDetailDTO(Message message) {
        UserMessageDetailDTO dto = new UserMessageDetailDTO();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setContent(message.getContent());
        dto.setCategory(message.getCategory() != null ? message.getCategory().name() : null);
        dto.setSource(message.getSource() != null ? message.getSource().name() : null);
        dto.setPriority(message.getPriority());
        dto.setIsRead(message.getIsRead());
        dto.setCreateTime(message.getCreateTime());
        dto.setExtraData(message.getExtraData());
        
        UserMessageDetailDTO.DisplayData displayData = buildDisplayData(message);
        dto.setDisplayData(displayData);
        
        return dto;
    }
    
    private UserMessageDetailDTO.DisplayData buildDisplayData(Message message) {
        UserMessageDetailDTO.DisplayData displayData = new UserMessageDetailDTO.DisplayData();
        
        String category = message.getCategory() != null ? message.getCategory().name() : "system";
        
        Map<String, String> categoryConfig = new HashMap<>();
        categoryConfig.put("system", "📋");
        categoryConfig.put("swap", "⚡");
        categoryConfig.put("alert", "⚠️");
        categoryConfig.put("activity", "🎉");
        displayData.setIcon(categoryConfig.getOrDefault(category, "📢"));
        
        Map<String, String> categoryTextMap = new HashMap<>();
        categoryTextMap.put("system", "系统通知");
        categoryTextMap.put("swap", "换电提醒");
        categoryTextMap.put("alert", "报警通知");
        categoryTextMap.put("activity", "活动公告");
        displayData.setCategoryText(categoryTextMap.getOrDefault(category, "消息通知"));
        
        List<UserMessageDetailDTO.Highlight> highlights = new ArrayList<>();
        
        if (message.getExtraData() != null && !message.getExtraData().isEmpty()) {
            try {
                Map<String, Object> extraMap = objectMapper.readValue(message.getExtraData(), 
                        new TypeReference<Map<String, Object>>() {});
                
                switch (category) {
                    case "swap":
                        if (extraMap.containsKey("stationName")) {
                            addHighlight(highlights, "换电站", String.valueOf(extraMap.get("stationName")), "#1890ff");
                        }
                        if (extraMap.containsKey("batteryLevel")) {
                            addHighlight(highlights, "电池电量", extraMap.get("batteryLevel") + "%", "#52c41a");
                        }
                        if (extraMap.containsKey("range")) {
                            addHighlight(highlights, "续航里程", extraMap.get("range") + "km", "#13c2c2");
                        }
                        break;
                    case "alert":
                        if (extraMap.containsKey("alertType")) {
                            String alertType = String.valueOf(extraMap.get("alertType"));
                            Map<String, String> alertTypeMap = new HashMap<>();
                            alertTypeMap.put("low_battery", "低电量报警");
                            alertTypeMap.put("overheat", "温度过高");
                            alertTypeMap.put("voltage_abnormal", "电压异常");
                            addHighlight(highlights, "报警类型", alertTypeMap.getOrDefault(alertType, alertType), "#ff4d4f");
                        }
                        if (extraMap.containsKey("batteryLevel")) {
                            addHighlight(highlights, "电池电量", extraMap.get("batteryLevel") + "%", "#faad14");
                        }
                        if (extraMap.containsKey("temperature")) {
                            addHighlight(highlights, "温度", extraMap.get("temperature") + "℃", "#ff4d4f");
                        }
                        break;
                    case "activity":
                        if (extraMap.containsKey("discount")) {
                            double discount = ((Number) extraMap.get("discount")).doubleValue();
                            addHighlight(highlights, "优惠折扣", (discount * 10) + "折", "#f5222d");
                        }
                        if (extraMap.containsKey("startTime") && extraMap.containsKey("endTime")) {
                            addHighlight(highlights, "活动时间", extraMap.get("startTime") + " ~ " + extraMap.get("endTime"), "#666666");
                        }
                        break;
                    case "system":
                        if (extraMap.containsKey("maintenanceTime")) {
                            addHighlight(highlights, "维护时间", String.valueOf(extraMap.get("maintenanceTime")), "#1890ff");
                        }
                        break;
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        
        displayData.setHighlights(highlights);
        
        List<UserMessageDetailDTO.Action> actions = new ArrayList<>();
        switch (category) {
            case "swap":
                UserMessageDetailDTO.Action viewHistory = new UserMessageDetailDTO.Action();
                viewHistory.setText("查看换电记录");
                viewHistory.setType("link");
                viewHistory.setUrl("/swap/history");
                actions.add(viewHistory);
                
                if (message.getStationId() != null) {
                    UserMessageDetailDTO.Action navigate = new UserMessageDetailDTO.Action();
                    navigate.setText("导航到换电站");
                    navigate.setType("navigate");
                    navigate.setStationId(message.getStationId());
                    actions.add(navigate);
                }
                break;
            case "alert":
                UserMessageDetailDTO.Action viewDetail = new UserMessageDetailDTO.Action();
                viewDetail.setText("查看详情");
                viewDetail.setType("link");
                viewDetail.setUrl("/alert/detail?id=" + message.getId());
                actions.add(viewDetail);
                break;
            case "activity":
                UserMessageDetailDTO.Action viewActivity = new UserMessageDetailDTO.Action();
                viewActivity.setText("查看活动");
                viewActivity.setType("link");
                viewActivity.setUrl("/activity/detail?id=" + message.getId());
                actions.add(viewActivity);
                break;
        }
        displayData.setActions(actions);
        
        return displayData;
    }
    
    private void addHighlight(List<UserMessageDetailDTO.Highlight> highlights, String label, String value, String color) {
        UserMessageDetailDTO.Highlight highlight = new UserMessageDetailDTO.Highlight();
        highlight.setLabel(label);
        highlight.setValue(value);
        highlight.setColor(color);
        highlights.add(highlight);
    }
    
    private UserMessageListResponseDTO.Statistics buildStatistics(Long userId) {
        UserMessageListResponseDTO.Statistics statistics = new UserMessageListResponseDTO.Statistics();
        
        long totalCount = messageRepository.countByUserIdAndIsRead(userId, null);
        long unreadCount = messageRepository.countByUserIdAndIsRead(userId, false);
        statistics.setTotalCount(totalCount);
        statistics.setUnreadCount(unreadCount);
        
        Map<String, UserMessageListResponseDTO.CategoryStats> byCategory = new HashMap<>();
        List<Object[]> categoryCounts = messageRepository.countByCategoryForUser(userId);
        
        for (Object[] row : categoryCounts) {
            Message.MessageCategory cat = (Message.MessageCategory) row[0];
            Long total = (Long) row[1];
            Long unread = (Long) row[2];
            
            UserMessageListResponseDTO.CategoryStats stats = new UserMessageListResponseDTO.CategoryStats();
            stats.setTotal(total);
            stats.setUnread(unread);
            byCategory.put(cat.name(), stats);
        }
        
        for (Message.MessageCategory cat : Message.MessageCategory.values()) {
            if (!byCategory.containsKey(cat.name())) {
                UserMessageListResponseDTO.CategoryStats stats = new UserMessageListResponseDTO.CategoryStats();
                stats.setTotal(0L);
                stats.setUnread(0L);
                byCategory.put(cat.name(), stats);
            }
        }
        
        statistics.setByCategory(byCategory);
        
        return statistics;
    }
}
