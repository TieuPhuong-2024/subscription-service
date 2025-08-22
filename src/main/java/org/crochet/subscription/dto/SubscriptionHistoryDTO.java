package org.crochet.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.SubscriptionAction;
import org.crochet.subscription.enums.SubscriptionStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHistoryDTO {
    private Long id;
    private Long subscriptionId;
    private SubscriptionAction action;
    private SubscriptionStatus previousStatus;
    private SubscriptionStatus newStatus;
    private String description;
    private LocalDateTime createdAt;
}