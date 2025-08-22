package org.crochet.subscription.repository;

import org.crochet.subscription.enums.PlanDuration;
import org.crochet.subscription.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    
    List<SubscriptionPlan> findByIsActiveTrue();
    
    List<SubscriptionPlan> findByIsActiveTrueAndDuration(PlanDuration duration);
    
    SubscriptionPlan findByIdAndIsActiveTrue(Long id);
}