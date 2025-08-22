package org.crochet.subscription.repository;

import org.crochet.subscription.enums.SubscriptionAction;
import org.crochet.subscription.model.Subscription;
import org.crochet.subscription.model.SubscriptionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {
    
    List<SubscriptionHistory> findBySubscription(Subscription subscription);
    
    List<SubscriptionHistory> findBySubscriptionId(Long subscriptionId);
    
    Page<SubscriptionHistory> findBySubscriptionId(Long subscriptionId, Pageable pageable);
    
    List<SubscriptionHistory> findBySubscriptionIdAndAction(Long subscriptionId, SubscriptionAction action);
    
    List<SubscriptionHistory> findBySubscriptionIdAndCreatedAtBetween(Long subscriptionId, LocalDateTime start, LocalDateTime end);
}