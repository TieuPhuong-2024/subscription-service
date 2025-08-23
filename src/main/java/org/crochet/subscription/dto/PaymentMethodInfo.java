package org.crochet.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.PaymentMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodInfo {
    private PaymentMethod method;
    private String displayName;
    private String description;
    private boolean payOSSupported;
}
