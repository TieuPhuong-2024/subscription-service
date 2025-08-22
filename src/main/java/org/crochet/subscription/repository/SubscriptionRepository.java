package org.crochet.subscription.repository;

import org.crochet.subscription.enums.SubscriptionStatus;
import org.crochet.subscription.model.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findByUserId(Long userId);
    
    Page<Subscription> findByUserId(Long userId, Pageable pageable);
    
    List<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
    
    Page<Subscription> findByStatus(SubscriptionStatus status, Pageable pageable);
    
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.status = 'ACTIVE' AND s.endDate > :now")
    List<Subscription> findActiveSubscriptionsByUserId(Long userId, LocalDateTime now);
    
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.status = 'ACTIVE' AND s.endDate > :now")
    Optional<Subscription> findActiveSubscriptionByUserId(Long userId, LocalDateTime now);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate BETWEEN :startDate AND :endDate AND s.autoRenew = true")
    List<Subscription> findSubscriptionsToRenew(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate < :now")
    List<Subscription> findExpiredSubscriptions(LocalDateTime now);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate BETWEEN :startDate AND :endDate AND s.autoRenew = true")
    List<Subscription> findSubscriptionsForRenewal(LocalDateTime startDate, LocalDateTime endDate);
}