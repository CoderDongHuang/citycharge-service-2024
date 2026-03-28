package com.citycharge.service;

import com.citycharge.config.WebSocketConfig;
import com.citycharge.entity.AlarmRecord;
import com.citycharge.repository.AlarmRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    
    private final AlarmRecordRepository alarmRecordRepository;
    private final WebSocketConfig webSocketConfig;
    
    public List<AlarmRecord> findAll() {
        return alarmRecordRepository.findAll();
    }
    
    public AlarmRecord findById(Long id) {
        Optional<AlarmRecord> alarmRecord = alarmRecordRepository.findById(id);
        return alarmRecord.orElse(null);
    }
    
    public List<AlarmRecord> findByResolved(Boolean resolved) {
        return alarmRecordRepository.findByIsResolved(resolved);
    }
    
    public List<AlarmRecord> findByAlarmType(String alarmType) {
        return alarmRecordRepository.findByAlarmType(alarmType);
    }
    
    public AlarmRecord save(AlarmRecord alarmRecord) {
        boolean isNew = alarmRecord.getId() == null;
        AlarmRecord savedRecord = alarmRecordRepository.save(alarmRecord);
        
        // 推送 WebSocket 消息
        if (isNew) {
            // 新报警，推送新报警消息
            log.info("保存新报警：{}", savedRecord.getId());
        } else if (Boolean.TRUE.equals(savedRecord.getIsResolved())) {
            // 已处理的报警，推送处理状态更新
            log.info("更新已处理报警：{}", savedRecord.getId());
        }
        
        return savedRecord;
    }
    
    public AlarmRecord handleAlarm(Long id, String handledBy) {
        AlarmRecord alarmRecord = findById(id);
        if (alarmRecord != null) {
            alarmRecord.setIsResolved(true);
            alarmRecord.setHandledBy(handledBy);
            alarmRecord.setHandledAt(java.time.LocalDateTime.now());
            return save(alarmRecord);
        }
        return null;
    }
    
    public void deleteById(Long id) {
        alarmRecordRepository.deleteById(id);
    }
    
    public long countAll() {
        return alarmRecordRepository.count();
    }
    
    public long countByResolved(boolean resolved) {
        return alarmRecordRepository.countByIsResolved(resolved);
    }
}
