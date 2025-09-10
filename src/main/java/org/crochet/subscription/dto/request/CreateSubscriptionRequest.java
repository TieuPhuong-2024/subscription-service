package org.crochet.subscription.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Subscription plan ID is required")
    private Long planId;
    
    @Builder.Default
    private boolean autoRenew = true;
}