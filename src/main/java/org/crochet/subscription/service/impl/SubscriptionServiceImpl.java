package org.crochet.subscription.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.subscription.dto.SubscriptionDTO;
import org.crochet.subscription.dto.request.CreateSubscriptionRequest;
import org.crochet.subscription.dto.request.UpdateSubscriptionRequest;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.SubscriptionAction;
import org.crochet.subscription.enums.SubscriptionStatus;
import org.crochet.subscription.exception.ResourceNotFoundException;
import org.crochet.subscription.mapper.SubscriptionHistoryMapper;
import org.crochet.subscription.mapper.SubscriptionMapper;
import org.crochet.subscription.model.Subscription;
import org.crochet.subscription.model.SubscriptionHistory;
import org.crochet.subscription.model.SubscriptionPlan;
import org.crochet.subscription.repository.SubscriptionHistoryRepository;
import org.crochet.subscription.repository.SubscriptionPlanRepository;
import org.crochet.subscription.repository.SubscriptionRepository;
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
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionHistoryMapper subscriptionHistoryMapper;

    @Override
    @Transactional
    public SubscriptionDTO createSubscription(CreateSubscriptionRequest request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + request.getPlanId()));
        
        if (!plan.isActive()) {
            throw new IllegalArgumentException("Cannot create subscription with inactive plan");
        }
        
        Subscription subscription = subscriptionMapper.toEntity(request, plan);
        
        // Set end date based on plan duration
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = switch (plan.getDuration()) {
            case MONTH -> startDate.plusMonths(1);
            case YEAR -> startDate.plusYears(1);
        };

        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        
        // Create subscription history entry
        SubscriptionHistory history = subscriptionHistoryMapper.createHistoryEntry(
                savedSubscription, 
                SubscriptionAction.CREATED, 
                null, 
                SubscriptionStatus.PENDING, 
                "Subscription created");
        
        subscriptionHistoryRepository.save(history);
        
        return subscriptionMapper.toDto(savedSubscription);
    }

    @Override
    @Transactional
    public SubscriptionDTO updateSubscription(Long id, UpdateSubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
        
        SubscriptionStatus previousStatus = subscription.getStatus();
        SubscriptionPlan previousPlan = subscription.getPlan();
        
        // Update plan if requested
        if (request.getPlanId() != null && !request.getPlanId().equals(subscription.getPlan().getId())) {
            SubscriptionPlan newPlan = subscriptionPlanRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + request.getPlanId()));
            
            if (!newPlan.isActive()) {
                throw new IllegalArgumentException("Cannot update subscription with inactive plan");
            }
            
            subscription.setPlan(newPlan);
            
            // Create history entry for plan change
            SubscriptionHistory planChangeHistory = subscriptionHistoryMapper.createHistoryEntry(
                    subscription,
                    SubscriptionAction.RENEWED,
                    previousStatus,
                    subscription.getStatus(),
                    "Plan changed from " + previousPlan.getName() + " to " + newPlan.getName());
            
            subscriptionHistoryRepository.save(planChangeHistory);
        }
        
        // Update other fields
        subscriptionMapper.updateEntityFromRequest(request, subscription);
        
        // If status changed, create history entry
        if (request.getStatus() != null && request.getStatus() != previousStatus) {
            SubscriptionAction action;
            String description;

            switch (request.getStatus()) {
                case ACTIVE:
                    action = SubscriptionAction.ACTIVATED;
                    description = "Subscription activated";
                    break;
                case CANCELLED:
                    action = SubscriptionAction.CANCELLED;
                    description = "Subscription cancelled";
                    break;
                case EXPIRED:
                    action = SubscriptionAction.EXPIRED;
                    description = "Subscription expired";
                    break;
                case PAYMENT_FAILED:
                    action = SubscriptionAction.PAYMENT_FAILED;
                    description = "Subscription payment failed";
                    break;
                default:
                    action = SubscriptionAction.RENEWED;
                    description = "Subscription status updated to " + request.getStatus();
            }

            SubscriptionHistory statusChangeHistory = subscriptionHistoryMapper.createHistoryEntry(
                    subscription,
                    action,
                    previousStatus,
                    request.getStatus(),
                    description);
            
            subscriptionHistoryRepository.save(statusChangeHistory);
        }
        
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDto(updatedSubscription);
    }

    @Override
    public SubscriptionDTO getSubscriptionById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
        return subscriptionMapper.toDto(subscription);
    }

    @Override
    public List<SubscriptionDTO> getSubscriptionsByUserId(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<SubscriptionDTO> getSubscriptionsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Subscription> subscriptionPage = subscriptionRepository.findByUserId(userId, pageable);
        
        Page<SubscriptionDTO> dtoPage = subscriptionPage.map(subscriptionMapper::toDto);
        return PageResponse.from(dtoPage);
    }

    @Override
    public SubscriptionDTO getActiveSubscriptionByUserId(Long userId) {
        return subscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .map(subscriptionMapper::toDto)
                .orElse(null);
    }

    @Override
    public PageResponse<SubscriptionDTO> getSubscriptionsByStatus(SubscriptionStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Subscription> subscriptionPage = subscriptionRepository.findByStatus(status, pageable);
        
        Page<SubscriptionDTO> dtoPage = subscriptionPage.map(subscriptionMapper::toDto);
        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional
    public SubscriptionDTO cancelSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
        
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            return subscriptionMapper.toDto(subscription);
        }
        
        SubscriptionStatus previousStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);
        
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        
        // Create history entry
        SubscriptionHistory history = subscriptionHistoryMapper.createHistoryEntry(
                updatedSubscription,
                SubscriptionAction.CANCELLED,
                previousStatus,
                SubscriptionStatus.CANCELLED,
                "Subscription cancelled");
        
        subscriptionHistoryRepository.save(history);
        
        return subscriptionMapper.toDto(updatedSubscription);
    }

    @Override
    @Transactional
    public SubscriptionDTO activateSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
        
        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            return subscriptionMapper.toDto(subscription);
        }
        
        SubscriptionStatus previousStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        
        // Create history entry
        SubscriptionHistory history = subscriptionHistoryMapper.createHistoryEntry(
                updatedSubscription,
                SubscriptionAction.ACTIVATED,
                previousStatus,
                SubscriptionStatus.ACTIVE,
                "Subscription activated");
        
        subscriptionHistoryRepository.save(history);
        
        return subscriptionMapper.toDto(updatedSubscription);
    }

    @Override
    @Transactional
    public SubscriptionDTO renewSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
        
        SubscriptionStatus previousStatus = subscription.getStatus();
        LocalDateTime previousEndDate = subscription.getEndDate();
        
        // Calculate new end date based on plan duration
        LocalDateTime newEndDate = switch (subscription.getPlan().getDuration()) {
            case MONTH -> previousEndDate.plusMonths(1);
            case YEAR -> previousEndDate.plusYears(1);
        };

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setEndDate(newEndDate);
        
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        
        // Create history entry
        SubscriptionHistory history = subscriptionHistoryMapper.createHistoryEntry(
                updatedSubscription,
                SubscriptionAction.RENEWED,
                previousStatus,
                SubscriptionStatus.ACTIVE,
                "Subscription renewed until " + newEndDate);
        
        subscriptionHistoryRepository.save(history);
        
        return subscriptionMapper.toDto(updatedSubscription);
    }

    @Override
    @Transactional
    public void processRenewals() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(7);
        List<Subscription> renewableSubscriptions = subscriptionRepository.findSubscriptionsForRenewal(now, endDate);
        
        for (Subscription subscription : renewableSubscriptions) {
            try {
                renewSubscription(subscription.getId());
                // Payment processing would happen here or be triggered via events
            } catch (Exception e) {
                // Log error and continue with next subscription
                System.err.println("Error renewing subscription " + subscription.getId() + ": " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void processExpirations() {
        LocalDateTime now = LocalDateTime.now();
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(now);
        
        for (Subscription subscription : expiredSubscriptions) {
            try {
                SubscriptionStatus previousStatus = subscription.getStatus();
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                
                Subscription updatedSubscription = subscriptionRepository.save(subscription);
                
                // Create history entry
                SubscriptionHistory history = subscriptionHistoryMapper.createHistoryEntry(
                        updatedSubscription,
                        SubscriptionAction.EXPIRED,
                        previousStatus,
                        SubscriptionStatus.EXPIRED,
                        "Subscription expired");
                
                subscriptionHistoryRepository.save(history);
            } catch (Exception e) {
                // Log error and continue with next subscription
                System.err.println("Error processing expired subscription " + subscription.getId() + ": " + e.getMessage());
            }
        }
    }
}