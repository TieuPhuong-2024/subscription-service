package org.crochet.subscription.mapper;

import org.crochet.subscription.dto.PaymentDTO;
import org.crochet.subscription.dto.request.CreatePaymentRequest;
import org.crochet.subscription.dto.request.UpdatePaymentRequest;
import org.crochet.subscription.enums.PaymentStatus;
import org.crochet.subscription.model.Payment;
import org.crochet.subscription.model.Subscription;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {
    
    @Mapping(source = "subscription.id", target = "subscriptionId")
    PaymentDTO toDto(Payment payment);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscription", source = "subscription")
    @Mapping(target = "paymentStatus", constant = "PENDING")
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(CreatePaymentRequest request, Subscription subscription);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscription", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdatePaymentRequest request, @MappingTarget Payment payment);
    
    @AfterMapping
    default void setPaymentDate(@MappingTarget Payment payment, UpdatePaymentRequest request) {
        if (request.getPaymentStatus() == PaymentStatus.COMPLETED && payment.getPaymentDate() == null) {
            payment.setPaymentDate(java.time.LocalDateTime.now());
        }
    }
}