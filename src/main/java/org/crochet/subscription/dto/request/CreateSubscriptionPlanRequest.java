package org.crochet.subscription.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateSubscriptionPlanRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Duration is required")
    private PlanDuration duration;
    
    private String features;
    
    @Builder.Default
    private boolean active = true;
}