package org.crochet.subscription.repository;

import org.crochet.subscription.enums.PaymentStatus;
import org.crochet.subscription.model.Payment;
import org.crochet.subscription.model.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findBySubscription(Subscription subscription);
    
    List<Payment> findBySubscriptionId(Long subscriptionId);
    
    Page<Payment> findBySubscriptionId(Long subscriptionId, Pageable pageable);
    
    List<Payment> findBySubscriptionIdAndPaymentStatus(Long subscriptionId, PaymentStatus status);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findBySubscription_UserId(Long userId);
    
    Page<Payment> findBySubscriptionUserId(Long userId, Pageable pageable);
    
    Page<Payment> findByPaymentStatus(PaymentStatus status, Pageable pageable);
}