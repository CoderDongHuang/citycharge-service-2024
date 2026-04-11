package com.citycharge.service;

import com.citycharge.dto.OrderStatsDTO;
import com.citycharge.entity.UserOrder;
import com.citycharge.repository.UserOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserOrderService {
    
    private final UserOrderRepository userOrderRepository;
    
    public List<UserOrder> findByUserId(Long userId) {
        return userOrderRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    public Optional<UserOrder> findByIdAndUserId(String id, Long userId) {
        return userOrderRepository.findByIdAndUserId(id, userId);
    }
    
    public OrderStatsDTO getStats(Long userId) {
        OrderStatsDTO stats = new OrderStatsDTO();
        stats.setTotal(userOrderRepository.countByUserId(userId));
        stats.setCompleted(userOrderRepository.countByUserIdAndStatus(userId, UserOrder.OrderStatus.completed));
        stats.setProcessing(userOrderRepository.countByUserIdAndStatus(userId, UserOrder.OrderStatus.processing));
        stats.setPending(userOrderRepository.countByUserIdAndStatus(userId, UserOrder.OrderStatus.pending));
        return stats;
    }
    
    @Transactional
    public UserOrder payOrder(String orderId, Long userId) {
        UserOrder order = userOrderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() != UserOrder.OrderStatus.pending) {
            throw new RuntimeException("订单状态不允许支付");
        }
        
        order.setStatus(UserOrder.OrderStatus.processing);
        order.setPayTime(LocalDateTime.now());
        
        return userOrderRepository.save(order);
    }
    
    @Transactional
    public UserOrder cancelOrder(String orderId, Long userId) {
        UserOrder order = userOrderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() == UserOrder.OrderStatus.completed) {
            throw new RuntimeException("已完成的订单不能取消");
        }
        if (order.getStatus() == UserOrder.OrderStatus.cancelled) {
            throw new RuntimeException("订单已取消");
        }
        
        order.setStatus(UserOrder.OrderStatus.cancelled);
        order.setCancelTime(LocalDateTime.now());
        
        return userOrderRepository.save(order);
    }
    
    @Transactional
    public UserOrder create(UserOrder order) {
        return userOrderRepository.save(order);
    }
}
