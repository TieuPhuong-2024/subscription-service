package org.crochet.subscription.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.PaymentMethod;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    
    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    private String transactionId;
}