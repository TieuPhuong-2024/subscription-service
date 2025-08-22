package org.crochet.subscription.service;

import org.crochet.subscription.dto.PaymentDTO;
import org.crochet.subscription.dto.request.CreatePaymentRequest;
import org.crochet.subscription.dto.request.UpdatePaymentRequest;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {
    
    /**
     * Create a new payment
     * 
     * @param request the request containing payment details
     * @return the created payment
     */
    PaymentDTO createPayment(CreatePaymentRequest request);
    
    /**
     * Update an existing payment
     * 
     * @param id the payment ID
     * @param request the request containing updated payment details
     * @return the updated payment
     */
    PaymentDTO updatePayment(Long id, UpdatePaymentRequest request);
    
    /**
     * Get a payment by ID
     * 
     * @param id the payment ID
     * @return the payment
     */
    PaymentDTO getPaymentById(Long id);
    
    /**
     * Get payments by subscription ID
     * 
     * @param subscriptionId the subscription ID
     * @return list of payments for the subscription
     */
    List<PaymentDTO> getPaymentsBySubscriptionId(Long subscriptionId);
    
    /**
     * Get payments by subscription ID with pagination
     * 
     * @param subscriptionId the subscription ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of payments for the subscription
     */
    PageResponse<PaymentDTO> getPaymentsBySubscriptionId(Long subscriptionId, int page, int size);
    
    /**
     * Get payments by user ID
     * 
     * @param userId the user ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of payments for the user
     */
    PageResponse<PaymentDTO> getPaymentsByUserId(Long userId, int page, int size);
    
    /**
     * Get payment by transaction ID
     * 
     * @param transactionId the transaction ID
     * @return the payment with the specified transaction ID
     */
    PaymentDTO getPaymentByTransactionId(String transactionId);
    
    /**
     * Process a successful payment
     * 
     * @param id the payment ID
     * @return the processed payment
     */
    PaymentDTO processSuccessfulPayment(Long id);
    
    /**
     * Process a failed payment
     * 
     * @param id the payment ID
     * @param failureReason the reason for payment failure
     * @return the processed payment
     */
    PaymentDTO processFailedPayment(Long id, String failureReason);
    
    /**
     * Get payments by status
     * 
     * @param status the payment status
     * @param page the page number
     * @param size the page size
     * @return paginated list of payments with the specified status
     */
    PageResponse<PaymentDTO> getPaymentsByStatus(PaymentStatus status, int page, int size);
}