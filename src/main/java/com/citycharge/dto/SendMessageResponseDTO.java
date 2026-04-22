package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SendMessageResponseDTO {
    private Long messageId;
    private Long recipientCount;
    private String status;
    private LocalDateTime estimatedDeliveryTime;
}
