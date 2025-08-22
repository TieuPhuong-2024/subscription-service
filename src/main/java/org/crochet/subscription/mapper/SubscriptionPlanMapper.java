package org.crochet.subscription.mapper;

import org.crochet.subscription.dto.SubscriptionPlanDTO;
import org.crochet.subscription.dto.request.CreateSubscriptionPlanRequest;
import org.crochet.subscription.dto.request.UpdateSubscriptionPlanRequest;
import org.crochet.subscription.model.SubscriptionPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubscriptionPlanMapper {
    
    SubscriptionPlanDTO toDto(SubscriptionPlan subscriptionPlan);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SubscriptionPlan toEntity(CreateSubscriptionPlanRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateSubscriptionPlanRequest request, @MappingTarget SubscriptionPlan subscriptionPlan);
}