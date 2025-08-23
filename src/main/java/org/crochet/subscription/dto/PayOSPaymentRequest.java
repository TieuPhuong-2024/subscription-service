package org.crochet.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOSPaymentRequest {
    private String orderCode;
    private BigDecimal amount;
    private String description;
    private String returnUrl;
    private String cancelUrl;
}
