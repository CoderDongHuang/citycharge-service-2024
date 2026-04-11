package com.citycharge.repository;

import com.citycharge.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, String> {
    
    List<UserOrder> findByUserId(Long userId);
    
    Optional<UserOrder> findByIdAndUserId(String id, Long userId);
    
    long countByUserId(Long userId);
    
    long countByUserIdAndStatus(Long userId, UserOrder.OrderStatus status);
    
    List<UserOrder> findByUserIdOrderByCreateTimeDesc(Long userId);
}
