package com.citycharge.repository;

import com.citycharge.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    
    Page<ContactMessage> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<ContactMessage> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<ContactMessage> findByStatusOrderByCreatedAtDesc(ContactMessage.MessageStatus status);
    
    Page<ContactMessage> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    List<ContactMessage> findAllByOrderByCreatedAtDesc();
    
    Optional<ContactMessage> findByIdAndUserId(Long id, Long userId);
}
