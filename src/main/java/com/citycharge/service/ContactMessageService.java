package com.citycharge.service;

import com.citycharge.dto.ContactMessageDTO;
import com.citycharge.dto.ContactMessageRequest;
import com.citycharge.dto.SubmitMessageResponse;
import com.citycharge.entity.ContactMessage;
import com.citycharge.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactMessageService {
    
    @Autowired
    private ContactMessageRepository contactMessageRepository;
    
    @Transactional
    public SubmitMessageResponse submitMessage(Long userId, ContactMessageRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
            throw new RuntimeException("主题不能为空");
        }
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new RuntimeException("留言内容不能为空");
        }
        
        ContactMessage message = new ContactMessage();
        message.setUserId(userId);
        message.setName(request.getName().trim());
        message.setEmail(request.getEmail().trim());
        message.setSubject(request.getSubject().trim());
        message.setMessage(request.getMessage().trim());
        message.setStatus(ContactMessage.MessageStatus.pending);
        
        ContactMessage saved = contactMessageRepository.save(message);
        
        SubmitMessageResponse response = new SubmitMessageResponse();
        response.setMessageId(saved.getId());
        response.setStatus(saved.getStatus().name());
        return response;
    }
    
    public Page<ContactMessageDTO> getMessageHistory(Long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ContactMessage> messages = contactMessageRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return messages.map(this::toDTO);
    }
    
    public ContactMessageDTO getMessageDetail(Long userId, Long messageId) {
        Optional<ContactMessage> messageOpt = contactMessageRepository.findByIdAndUserId(messageId, userId);
        return messageOpt.map(this::toDTO).orElse(null);
    }
    
    public ContactMessageDTO getMessageById(Long messageId) {
        Optional<ContactMessage> messageOpt = contactMessageRepository.findById(messageId);
        return messageOpt.map(this::toDTO).orElse(null);
    }
    
    @Transactional
    public ContactMessageDTO replyMessage(Long messageId, String reply) {
        Optional<ContactMessage> messageOpt = contactMessageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            return null;
        }
        ContactMessage message = messageOpt.get();
        message.setReply(reply);
        message.setReplyTime(LocalDateTime.now());
        message.setStatus(ContactMessage.MessageStatus.replied);
        contactMessageRepository.save(message);
        return toDTO(message);
    }
    
    @Transactional
    public ContactMessageDTO updateStatus(Long messageId, String status) {
        Optional<ContactMessage> messageOpt = contactMessageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            return null;
        }
        ContactMessage message = messageOpt.get();
        try {
            ContactMessage.MessageStatus newStatus = ContactMessage.MessageStatus.valueOf(status);
            message.setStatus(newStatus);
            contactMessageRepository.save(message);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的状态值");
        }
        return toDTO(message);
    }
    
    @Transactional
    public boolean deleteMessage(Long messageId) {
        if (contactMessageRepository.existsById(messageId)) {
            contactMessageRepository.deleteById(messageId);
            return true;
        }
        return false;
    }
    
    public Page<ContactMessageDTO> getAllMessages(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ContactMessage> messages = contactMessageRepository.findAllByOrderByCreatedAtDesc(pageable);
        return messages.map(this::toDTO);
    }
    
    private ContactMessageDTO toDTO(ContactMessage message) {
        ContactMessageDTO dto = new ContactMessageDTO();
        dto.setId(message.getId());
        dto.setUserId(message.getUserId());
        dto.setName(message.getName());
        dto.setEmail(message.getEmail());
        dto.setSubject(message.getSubject());
        dto.setMessage(message.getMessage());
        dto.setStatus(message.getStatus() != null ? message.getStatus().name() : "pending");
        dto.setReply(message.getReply());
        dto.setReplyTime(message.getReplyTime());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}
