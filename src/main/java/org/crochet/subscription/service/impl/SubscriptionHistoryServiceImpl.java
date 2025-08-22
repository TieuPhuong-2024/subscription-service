package org.crochet.subscription.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.subscription.dto.SubscriptionHistoryDTO;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.SubscriptionAction;
import org.crochet.subscription.exception.ResourceNotFoundException;
import org.crochet.subscription.mapper.SubscriptionHistoryMapper;
import org.crochet.subscription.model.SubscriptionHistory;
import org.crochet.subscription.repository.SubscriptionHistoryRepository;
import org.crochet.subscription.repository.SubscriptionRepository;
import org.crochet.subscription.service.SubscriptionHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionHistoryServiceImpl implements SubscriptionHistoryService {

    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryMapper subscriptionHistoryMapper;

    @Override
    public List<SubscriptionHistoryDTO> getHistoryBySubscriptionId(Long subscriptionId) {
        // Verify subscription exists
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + subscriptionId);
        }
        
        List<SubscriptionHistory> historyList = subscriptionHistoryRepository.findBySubscriptionId(subscriptionId);
        return historyList.stream()
                .map(subscriptionHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<SubscriptionHistoryDTO> getHistoryBySubscriptionId(Long subscriptionId, int page, int size) {
        // Verify subscription exists
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + subscriptionId);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SubscriptionHistory> historyPage = subscriptionHistoryRepository.findBySubscriptionId(subscriptionId, pageable);
        
        Page<SubscriptionHistoryDTO> dtoPage = historyPage.map(subscriptionHistoryMapper::toDto);
        return PageResponse.from(dtoPage);
    }

    @Override
    public List<SubscriptionHistoryDTO> getHistoryBySubscriptionIdAndAction(Long subscriptionId, SubscriptionAction action) {
        // Verify subscription exists
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + subscriptionId);
        }
        
        List<SubscriptionHistory> historyList = subscriptionHistoryRepository.findBySubscriptionIdAndAction(subscriptionId, action);
        return historyList.stream()
                .map(subscriptionHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionHistoryDTO> getHistoryBySubscriptionIdAndDateRange(Long subscriptionId, LocalDateTime startDate, LocalDateTime endDate) {
        // Verify subscription exists
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + subscriptionId);
        }
        
        List<SubscriptionHistory> historyList = subscriptionHistoryRepository.findBySubscriptionIdAndCreatedAtBetween(
                subscriptionId, startDate, endDate);
        return historyList.stream()
                .map(subscriptionHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
}