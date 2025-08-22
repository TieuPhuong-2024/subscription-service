package org.crochet.subscription.mapper;

import org.crochet.subscription.dto.SubscriptionDTO;
import org.crochet.subscription.dto.request.CreateSubscriptionRequest;
import org.crochet.subscription.dto.request.UpdateSubscriptionRequest;
import org.crochet.subscription.model.Subscription;
import org.crochet.subscription.model.SubscriptionPlan;
import org.mapstruct.*;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {SubscriptionPlanMapper.class})
public interface SubscriptionMapper {
    
    @Mapping(target = "active", expression = "java(subscription.isActive())")
    @Mapping(target = "expired", expression = "java(subscription.isExpired())")
    @Mapping(source = "plan", target = "plan")
    SubscriptionDTO toDto(Subscription subscription);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan", source = "plan")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "startDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Subscription toEntity(CreateSubscriptionRequest request, SubscriptionPlan plan);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateSubscriptionRequest request, @MappingTarget Subscription subscription);
}