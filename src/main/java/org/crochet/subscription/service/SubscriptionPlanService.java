package org.crochet.subscription.service;

import org.crochet.subscription.dto.SubscriptionPlanDTO;
import org.crochet.subscription.dto.request.CreateSubscriptionPlanRequest;
import org.crochet.subscription.dto.request.UpdateSubscriptionPlanRequest;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.PlanDuration;

import java.util.List;

public interface SubscriptionPlanService {
    
    /**
     * Create a new subscription plan
     * 
     * @param request the request containing plan details
     * @return the created subscription plan
     */
    SubscriptionPlanDTO createPlan(CreateSubscriptionPlanRequest request);
    
    /**
     * Update an existing subscription plan
     * 
     * @param id the plan ID
     * @param request the request containing updated plan details
     * @return the updated subscription plan
     */
    SubscriptionPlanDTO updatePlan(Long id, UpdateSubscriptionPlanRequest request);
    
    /**
     * Get a subscription plan by ID
     * 
     * @param id the plan ID
     * @return the subscription plan
     */
    SubscriptionPlanDTO getPlanById(Long id);
    
    /**
     * Get all subscription plans
     * 
     * @param page the page number
     * @param size the page size
     * @return paginated list of subscription plans
     */
    PageResponse<SubscriptionPlanDTO> getAllPlans(int page, int size);
    
    /**
     * Get all active subscription plans
     * 
     * @return list of active subscription plans
     */
    List<SubscriptionPlanDTO> getAllActivePlans();
    
    /**
     * Get active subscription plans by duration
     * 
     * @param duration the plan duration
     * @return list of active subscription plans with specified duration
     */
    List<SubscriptionPlanDTO> getActivePlansByDuration(PlanDuration duration);
    
    /**
     * Delete a subscription plan
     * 
     * @param id the plan ID
     */
    void deletePlan(Long id);
    
    /**
     * Activate or deactivate a subscription plan
     * 
     * @param id the plan ID
     * @param active true to activate, false to deactivate
     * @return the updated subscription plan
     */
    SubscriptionPlanDTO togglePlanStatus(Long id, boolean active);
}