package com.citycharge.repository;

import com.citycharge.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByUserId(Long userId, Pageable pageable);
    
    Page<Message> findByUserIdAndCategory(Long userId, Message.MessageCategory category, Pageable pageable);
    
    Page<Message> findByUserIdAndSource(Long userId, Message.MessageSource source, Pageable pageable);
    
    Page<Message> findByUserIdAndIsRead(Long userId, Boolean isRead, Pageable pageable);
    
    Page<Message> findByUserIdAndCategoryAndIsRead(Long userId, Message.MessageCategory category, Boolean isRead, Pageable pageable);
    
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
    
    long countByUserIdAndCategoryAndIsRead(Long userId, Message.MessageCategory category, Boolean isRead);
    
    long countByUserIdAndCategory(Long userId, Message.MessageCategory category);
    
    long countByUserIdAndSourceAndIsRead(Long userId, Message.MessageSource source, Boolean isRead);
    
    @Query("SELECT m.category, COUNT(m), SUM(CASE WHEN m.isRead = false THEN 1 ELSE 0 END) FROM Message m WHERE m.userId = :userId GROUP BY m.category")
    List<Object[]> countByCategoryForUser(@Param("userId") Long userId);
    
    @Query("SELECT m.source, COUNT(m) FROM Message m WHERE m.userId = :userId AND m.isRead = false GROUP BY m.source")
    List<Object[]> countUnreadBySourceForUser(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readTime = :readTime WHERE m.id = :id AND m.userId = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);
    
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readTime = :readTime WHERE m.id IN :ids AND m.userId = :userId")
    int markAsReadBatch(@Param("ids") List<Long> ids, @Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);
    
    @Modifying
    @Query("DELETE FROM Message m WHERE m.id = :id AND m.userId = :userId")
    int deleteByUser(@Param("id") Long id, @Param("userId") Long userId);
    
    @Modifying
    @Query("DELETE FROM Message m WHERE m.id IN :ids AND m.userId = :userId")
    int deleteBatchByUser(@Param("ids") List<Long> ids, @Param("userId") Long userId);
    
    Page<Message> findBySource(Message.MessageSource source, Pageable pageable);
    
    Page<Message> findBySendStatus(Message.SendStatus sendStatus, Pageable pageable);
    
    Page<Message> findByCategory(Message.MessageCategory category, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE (:category IS NULL OR m.category = :category) " +
           "AND (:source IS NULL OR m.source = :source) " +
           "AND (:sendStatus IS NULL OR m.sendStatus = :sendStatus) " +
           "AND (:startTime IS NULL OR m.createTime >= :startTime) " +
           "AND (:endTime IS NULL OR m.createTime <= :endTime)")
    Page<Message> findAdminMessages(
            @Param("category") Message.MessageCategory category,
            @Param("source") Message.MessageSource source,
            @Param("sendStatus") Message.SendStatus sendStatus,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.userId IN :userIds")
    List<Message> findByUserIds(@Param("userIds") List<Long> userIds);
    
    @Modifying
    @Query("UPDATE Message m SET m.sendStatus = :status WHERE m.id = :id")
    int updateSendStatus(@Param("id") Long id, @Param("status") Message.SendStatus status);
    
    @Query("SELECT COUNT(DISTINCT m.userId) FROM Message m WHERE m.id = :messageId")
    long countRecipientsByMessageId(@Param("messageId") Long messageId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.id = :messageId AND m.isRead = true")
    long countReadByMessageId(@Param("messageId") Long messageId);
}
