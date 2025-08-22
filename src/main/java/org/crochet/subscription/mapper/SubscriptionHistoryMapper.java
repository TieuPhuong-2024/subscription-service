package org.crochet.subscription.mapper;

import org.crochet.subscription.dto.SubscriptionHistoryDTO;
import org.crochet.subscription.enums.SubscriptionAction;
import org.crochet.subscription.enums.SubscriptionStatus;
import org.crochet.subscription.model.Subscription;
import org.crochet.subscription.model.SubscriptionHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubscriptionHistoryMapper {
    
    @Mapping(source = "subscription.id", target = "subscriptionId")
    SubscriptionHistoryDTO toDto(SubscriptionHistory history);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscription", source = "subscription")
    @Mapping(target = "action", source = "action")
    @Mapping(target = "previousStatus", source = "previousStatus")
    @Mapping(target = "newStatus", source = "newStatus")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", ignore = true)
    SubscriptionHistory createHistoryEntry(Subscription subscription, SubscriptionAction action, 
                                          SubscriptionStatus previousStatus, SubscriptionStatus newStatus, 
                                          String description);
    
    default SubscriptionHistory createHistoryEntry(Subscription subscription, SubscriptionAction action, String description) {
        return createHistoryEntry(subscription, action, subscription.getStatus(), subscription.getStatus(), description);
    }
}