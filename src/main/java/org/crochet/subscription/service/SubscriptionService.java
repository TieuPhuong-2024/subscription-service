package org.crochet.subscription.service;

import org.crochet.subscription.dto.SubscriptionDTO;
import org.crochet.subscription.dto.request.CreateSubscriptionRequest;
import org.crochet.subscription.dto.request.UpdateSubscriptionRequest;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.SubscriptionStatus;

import java.util.List;

public interface SubscriptionService {
    
    /**
     * Create a new subscription
     * 
     * @param request the request containing subscription details
     * @return the created subscription
     */
    SubscriptionDTO createSubscription(CreateSubscriptionRequest request);
    
    /**
     * Update an existing subscription
     * 
     * @param id the subscription ID
     * @param request the request containing updated subscription details
     * @return the updated subscription
     */
    SubscriptionDTO updateSubscription(Long id, UpdateSubscriptionRequest request);
    
    /**
     * Get a subscription by ID
     * 
     * @param id the subscription ID
     * @return the subscription
     */
    SubscriptionDTO getSubscriptionById(Long id);
    
    /**
     * Get all subscriptions for a user
     * 
     * @param userId the user ID
     * @return list of subscriptions for the user
     */
    List<SubscriptionDTO> getSubscriptionsByUserId(Long userId);
    
    /**
     * Get all subscriptions for a user with pagination
     * 
     * @param userId the user ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of subscriptions for the user
     */
    PageResponse<SubscriptionDTO> getSubscriptionsByUserId(Long userId, int page, int size);
    
    /**
     * Get active subscription for a user
     * 
     * @param userId the user ID
     * @return the active subscription for the user, or null if none exists
     */
    SubscriptionDTO getActiveSubscriptionByUserId(Long userId);
    
    /**
     * Get subscriptions by status
     * 
     * @param status the subscription status
     * @param page the page number
     * @param size the page size
     * @return paginated list of subscriptions with the specified status
     */
    PageResponse<SubscriptionDTO> getSubscriptionsByStatus(SubscriptionStatus status, int page, int size);
    
    /**
     * Cancel a subscription
     * 
     * @param id the subscription ID
     * @return the cancelled subscription
     */
    SubscriptionDTO cancelSubscription(Long id);
    
    /**
     * Activate a subscription
     * 
     * @param id the subscription ID
     * @return the activated subscription
     */
    SubscriptionDTO activateSubscription(Long id);
    
    /**
     * Renew a subscription
     * 
     * @param id the subscription ID
     * @return the renewed subscription
     */
    SubscriptionDTO renewSubscription(Long id);
    
    /**
     * Process subscriptions that are eligible for renewal
     * This method is typically called by a scheduled job
     */
    void processRenewals();
    
    /**
     * Process expired subscriptions
     * This method is typically called by a scheduled job
     */
    void processExpirations();
}