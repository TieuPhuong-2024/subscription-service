package org.crochet.subscription.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.subscription.dto.SubscriptionPlanDTO;
import org.crochet.subscription.dto.request.CreateSubscriptionPlanRequest;
import org.crochet.subscription.dto.request.UpdateSubscriptionPlanRequest;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.PlanDuration;
import org.crochet.subscription.exception.ResourceNotFoundException;
import org.crochet.subscription.mapper.SubscriptionPlanMapper;
import org.crochet.subscription.model.SubscriptionPlan;
import org.crochet.subscription.repository.SubscriptionPlanRepository;
import org.crochet.subscription.service.SubscriptionPlanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;

    @Override
    @Transactional
    public SubscriptionPlanDTO createPlan(CreateSubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanMapper.toEntity(request);
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return subscriptionPlanMapper.toDto(savedPlan);
    }

    @Override
    @Transactional
    public SubscriptionPlanDTO updatePlan(Long id, UpdateSubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));
        
        subscriptionPlanMapper.updateEntityFromRequest(request, plan);
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return subscriptionPlanMapper.toDto(updatedPlan);
    }

    @Override
    public SubscriptionPlanDTO getPlanById(Long id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));
        return subscriptionPlanMapper.toDto(plan);
    }

    @Override
    public PageResponse<SubscriptionPlanDTO> getAllPlans(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SubscriptionPlan> planPage = subscriptionPlanRepository.findAll(pageable);
        
        Page<SubscriptionPlanDTO> dtoPage = planPage.map(subscriptionPlanMapper::toDto);
        return PageResponse.from(dtoPage);
    }

    @Override
    public List<SubscriptionPlanDTO> getAllActivePlans() {
        List<SubscriptionPlan> activePlans = subscriptionPlanRepository.findByIsActiveTrue();
        return activePlans.stream()
                .map(subscriptionPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionPlanDTO> getActivePlansByDuration(PlanDuration duration) {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findByIsActiveTrueAndDuration(duration);
        return plans.stream()
                .map(subscriptionPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        if (!subscriptionPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription plan not found with id: " + id);
        }
        subscriptionPlanRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SubscriptionPlanDTO togglePlanStatus(Long id, boolean active) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));
        
        plan.setActive(active);
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return subscriptionPlanMapper.toDto(updatedPlan);
    }
}