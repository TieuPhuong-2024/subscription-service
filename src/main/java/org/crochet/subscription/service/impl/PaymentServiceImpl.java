package org.crochet.subscription.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.subscription.dto.PaymentDTO;
import org.crochet.subscription.dto.request.CreatePaymentRequest;
import org.crochet.subscription.dto.request.UpdatePaymentRequest;
import org.crochet.subscription.dto.request.UpdateSubscriptionRequest;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.PaymentStatus;
import org.crochet.subscription.enums.SubscriptionAction;
import org.crochet.subscription.enums.SubscriptionStatus;
import org.crochet.subscription.exception.ResourceNotFoundException;
import org.crochet.subscription.mapper.PaymentMapper;
import org.crochet.subscription.mapper.SubscriptionHistoryMapper;
import org.crochet.subscription.model.Payment;
import org.crochet.subscription.model.Subscription;
import org.crochet.subscription.model.SubscriptionHistory;
import org.crochet.subscription.repository.PaymentRepository;
import org.crochet.subscription.repository.SubscriptionHistoryRepository;
import org.crochet.subscription.repository.SubscriptionRepository;
import org.crochet.subscription.service.PaymentService;
import org.crochet.subscription.service.SubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final PaymentMapper paymentMapper;
    private final SubscriptionHistoryMapper subscriptionHistoryMapper;
    private final SubscriptionService subscriptionService;

    @Override
    @Transactional
    public PaymentDTO createPayment(CreatePaymentRequest request) {
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + request.getSubscriptionId()));
        
        Payment payment = paymentMapper.toEntity(request, subscription);
        Payment savedPayment = paymentRepository.save(payment);
        
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    @Transactional
    public PaymentDTO updatePayment(Long id, UpdatePaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        
        PaymentStatus previousStatus = payment.getPaymentStatus();
        paymentMapper.updateEntityFromRequest(request, payment);
        
        // If payment status changed to COMPLETED, update payment date
        if (request.getPaymentStatus() == PaymentStatus.COMPLETED && payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        
        // If payment status changed to COMPLETED, update subscription status
        if (previousStatus != PaymentStatus.COMPLETED && request.getPaymentStatus() == PaymentStatus.COMPLETED) {
            processSuccessfulPayment(updatedPayment.getId());
        } else if (previousStatus != PaymentStatus.FAILED && request.getPaymentStatus() == PaymentStatus.FAILED) {
            processFailedPayment(updatedPayment.getId(), "Payment failed");
        }
        
        return paymentMapper.toDto(updatedPayment);
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toDto(payment);
    }

    @Override
    public List<PaymentDTO> getPaymentsBySubscriptionId(Long subscriptionId) {
        List<Payment> payments = paymentRepository.findBySubscriptionId(subscriptionId);
        return payments.stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<PaymentDTO> getPaymentsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Payment> paymentPage = paymentRepository.findBySubscriptionUserId(userId, pageable);
        
        Page<PaymentDTO> dtoPage = paymentPage.map(paymentMapper::toDto);
        return PageResponse.from(dtoPage);
    }

    @Override
    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction id: " + transactionId));
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public PaymentDTO processSuccessfulPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        
        // Update payment status
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Update subscription status
        Subscription subscription = payment.getSubscription();
        SubscriptionStatus previousStatus = subscription.getStatus();
        
        if (previousStatus == SubscriptionStatus.PENDING || previousStatus == SubscriptionStatus.PAYMENT_FAILED) {
            // Activate the subscription
            UpdateSubscriptionRequest updateRequest = new UpdateSubscriptionRequest();
            updateRequest.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionService.updateSubscription(subscription.getId(), updateRequest);
            
            // Create subscription history entry
            SubscriptionHistory history = subscriptionHistoryMapper.createHistoryEntry(
                    subscription,
                    SubscriptionAction.PAYMENT_COMPLETED,
                    previousStatus,
                    SubscriptionStatus.ACTIVE,
                    "Payment completed successfully");
            
            subscriptionHistoryRepository.save(history);
        }
        
        return paymentMapper.toDto(updatedPayment);
    }

    @Override
    @Transactional
    public PaymentDTO processFailedPayment(Long id, String failureReason) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        
        // Update payment status
        payment.setPaymentStatus(PaymentStatus.FAILED);
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Update subscription status
        Subscription subscription = payment.getSubscription();
        SubscriptionStatus previousStatus = subscription.getStatus();
        
        if (previousStatus != SubscriptionStatus.PAYMENT_FAILED) {
            // Mark subscription as payment failed
            UpdateSubscriptionRequest updateRequest = new UpdateSubscriptionRequest();
            updateRequest.setStatus(SubscriptionStatus.PAYMENT_FAILED);
            subscriptionService.updateSubscription(subscription.getId(), updateRequest);
            
            // Create subscription history entry
            SubscriptionHistory history = subscriptionHistoryMapper.createHistoryEntry(
                    subscription,
                    SubscriptionAction.PAYMENT_FAILED,
                    previousStatus,
                    SubscriptionStatus.PAYMENT_FAILED,
                    failureReason != null ? failureReason : "Payment failed");
            
            subscriptionHistoryRepository.save(history);
        }
        
        return paymentMapper.toDto(updatedPayment);
    }

    @Override
    public PageResponse<PaymentDTO> getPaymentsByStatus(PaymentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Payment> paymentPage = paymentRepository.findByPaymentStatus(status, pageable);
        
        Page<PaymentDTO> dtoPage = paymentPage.map(paymentMapper::toDto);
        return PageResponse.from(dtoPage);
    }

    @Override
    public PageResponse<PaymentDTO> getPaymentsBySubscriptionId(Long subscriptionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Payment> paymentPage = paymentRepository.findBySubscriptionId(subscriptionId, pageable);
        
        Page<PaymentDTO> dtoPage = paymentPage.map(paymentMapper::toDto);
        return PageResponse.from(dtoPage);
    }
}