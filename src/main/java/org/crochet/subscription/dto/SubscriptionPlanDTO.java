package org.crochet.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.PlanDuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private PlanDuration duration;
    private String features;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}