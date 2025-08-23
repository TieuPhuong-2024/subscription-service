package org.crochet.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.PaymentMethod;
import org.crochet.subscription.enums.PaymentStatus;
import org.crochet.subscription.util.PaymentMethodUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long subscriptionId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // PayOS specific fields
    private String paymentLinkId;
    private String qrCode;
    private String paymentUrl;

    // Helper methods for payment method info (computed properties)
    public String getPaymentMethodDisplayName() {
        return paymentMethod != null ?
            PaymentMethodUtil.getDisplayName(paymentMethod) : null;
    }

    public String getPaymentMethodDescription() {
        return paymentMethod != null ?
            PaymentMethodUtil.getDescription(paymentMethod) : null;
    }

    public boolean isPayOSSupported() {
        return PaymentMethodUtil.isPayOSSupported(paymentMethod);
    }
}