package org.crochet.subscription.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.PlanDuration;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionPlanRequest {
    private String name;
    private String description;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private PlanDuration duration;
    private String features;
    private Boolean active;
}