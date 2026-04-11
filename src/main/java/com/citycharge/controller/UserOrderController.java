package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.dto.OrderStatsDTO;
import com.citycharge.dto.UserOrderDTO;
import com.citycharge.entity.UserOrder;
import com.citycharge.service.UserOrderService;
import com.citycharge.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/orders")
@RequiredArgsConstructor
public class UserOrderController {
    
    private final UserOrderService userOrderService;
    private final JwtUtil jwtUtil;
    
    @GetMapping
    public ApiResponse<List<UserOrderDTO>> getOrders(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        List<UserOrder> orders = userOrderService.findByUserId(userId);
        List<UserOrderDTO> dtos = orders.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        return ApiResponse.success(dtos);
    }
    
    @GetMapping("/{orderId}")
    public ApiResponse<UserOrderDTO> getOrder(
            @PathVariable String orderId,
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        Optional<UserOrder> order = userOrderService.findByIdAndUserId(orderId, userId);
        if (!order.isPresent()) {
            return ApiResponse.error(404, "订单不存在");
        }
        
        return ApiResponse.success(toDTO(order.get()));
    }
    
    @GetMapping("/stats")
    public ApiResponse<OrderStatsDTO> getStats(
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        OrderStatsDTO stats = userOrderService.getStats(userId);
        return ApiResponse.success(stats);
    }
    
    @PostMapping("/{orderId}/pay")
    public ApiResponse<UserOrderDTO> payOrder(
            @PathVariable String orderId,
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        try {
            UserOrder order = userOrderService.payOrder(orderId, userId);
            return ApiResponse.success("支付成功", toDTO(order));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<UserOrderDTO> cancelOrder(
            @PathVariable String orderId,
            @RequestHeader(value = "X-User-ID", required = false) Long xUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(xUserId, authHeader);
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        
        try {
            UserOrder order = userOrderService.cancelOrder(orderId, userId);
            return ApiResponse.success("订单已取消", toDTO(order));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    private Long extractUserId(Long xUserId, String authHeader) {
        if (xUserId != null) {
            return xUserId;
        }
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            return null;
        }
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        }
        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        return null;
    }
    
    private UserOrderDTO toDTO(UserOrder order) {
        UserOrderDTO dto = new UserOrderDTO();
        dto.setId(order.getId());
        dto.setCreateTime(order.getCreateTime());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : "pending");
        dto.setVehicleName(order.getVehicleName());
        dto.setStationName(order.getStationName());
        dto.setBatteryInfo(order.getBatteryInfo());
        dto.setAmount(order.getAmount());
        dto.setPayTime(order.getPayTime());
        dto.setCompleteTime(order.getCompleteTime());
        dto.setNotes(order.getNotes());
        return dto;
    }
}
