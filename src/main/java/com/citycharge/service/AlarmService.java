package com.citycharge.service;

import com.citycharge.entity.AlarmRecord;
import com.citycharge.repository.AlarmRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {
    
    private final AlarmRecordRepository alarmRecordRepository;
    
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
        return alarmRecordRepository.save(alarmRecord);
    }
    
    public List<AlarmRecord> findUnresolvedAlarms() {
        return alarmRecordRepository.findByIsResolvedFalse();
    }
    
    public Long countAll() {
        return alarmRecordRepository.count();
    }
    
    public Long countByResolved(Boolean resolved) {
        return alarmRecordRepository.countByIsResolved(resolved);
    }
    
    public void deleteById(Long id) {
        alarmRecordRepository.deleteById(id);
    }
}