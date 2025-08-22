package org.crochet.subscription.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.crochet.subscription.dto.SubscriptionPlanDTO;
import org.crochet.subscription.dto.request.CreateSubscriptionPlanRequest;
import org.crochet.subscription.dto.request.UpdateSubscriptionPlanRequest;
import org.crochet.subscription.dto.response.ApiResponse;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.PlanDuration;
import org.crochet.subscription.service.SubscriptionPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> createPlan(@Valid @RequestBody CreateSubscriptionPlanRequest request) {
        SubscriptionPlanDTO createdPlan = subscriptionPlanService.createPlan(request);
        return new ResponseEntity<>(ApiResponse.success("Subscription plan created successfully", createdPlan), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSubscriptionPlanRequest request) {
        SubscriptionPlanDTO updatedPlan = subscriptionPlanService.updatePlan(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan updated successfully", updatedPlan));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> getPlanById(@PathVariable Long id) {
        SubscriptionPlanDTO plan = subscriptionPlanService.getPlanById(id);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SubscriptionPlanDTO>>> getAllPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SubscriptionPlanDTO> plans = subscriptionPlanService.getAllPlans(page, size);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDTO>>> getAllActivePlans() {
        List<SubscriptionPlanDTO> activePlans = subscriptionPlanService.getAllActivePlans();
        return ResponseEntity.ok(ApiResponse.success(activePlans));
    }

    @GetMapping("/active/duration/{duration}")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDTO>>> getActivePlansByDuration(
            @PathVariable PlanDuration duration) {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getActivePlansByDuration(duration);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long id) {
        subscriptionPlanService.deletePlan(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan deleted successfully", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> togglePlanStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        SubscriptionPlanDTO updatedPlan = subscriptionPlanService.togglePlanStatus(id, active);
        String message = active ? "Subscription plan activated successfully" : "Subscription plan deactivated successfully";
        return ResponseEntity.ok(ApiResponse.success(message, updatedPlan));
    }
}