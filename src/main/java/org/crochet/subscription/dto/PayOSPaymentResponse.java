package org.crochet.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOSPaymentResponse {
    private String paymentLinkId;
    private Long orderCode;
    private BigDecimal amount;
    private String description;
    private String paymentLink;
    private String qrCode;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
}
