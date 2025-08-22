package org.crochet.subscription.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.SubscriptionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionRequest {
    private Long planId;
    private SubscriptionStatus status;
    private Boolean autoRenew;
}