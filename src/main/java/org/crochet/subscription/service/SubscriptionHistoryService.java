package org.crochet.subscription.service;

import org.crochet.subscription.dto.SubscriptionHistoryDTO;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.SubscriptionAction;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionHistoryService {
    
    /**
     * Get subscription history by subscription ID
     * 
     * @param subscriptionId the subscription ID
     * @return list of subscription history entries
     */
    List<SubscriptionHistoryDTO> getHistoryBySubscriptionId(Long subscriptionId);
    
    /**
     * Get subscription history by subscription ID with pagination
     * 
     * @param subscriptionId the subscription ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of subscription history entries
     */
    PageResponse<SubscriptionHistoryDTO> getHistoryBySubscriptionId(Long subscriptionId, int page, int size);
    
    /**
     * Get subscription history by subscription ID and action
     * 
     * @param subscriptionId the subscription ID
     * @param action the subscription action
     * @return list of subscription history entries
     */
    List<SubscriptionHistoryDTO> getHistoryBySubscriptionIdAndAction(Long subscriptionId, SubscriptionAction action);
    
    /**
     * Get subscription history by subscription ID within a date range
     * 
     * @param subscriptionId the subscription ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of subscription history entries
     */
    List<SubscriptionHistoryDTO> getHistoryBySubscriptionIdAndDateRange(Long subscriptionId, LocalDateTime startDate, LocalDateTime endDate);
}