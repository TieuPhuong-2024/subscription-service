package org.crochet.subscription.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.crochet.subscription.dto.SubscriptionDTO;
import org.crochet.subscription.dto.SubscriptionHistoryDTO;
import org.crochet.subscription.dto.request.CreateSubscriptionRequest;
import org.crochet.subscription.dto.request.UpdateSubscriptionRequest;
import org.crochet.subscription.dto.response.ApiResponse;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.SubscriptionStatus;
import org.crochet.subscription.service.SubscriptionHistoryService;
import org.crochet.subscription.service.SubscriptionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionHistoryService subscriptionHistoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionDTO>> createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionDTO createdSubscription = subscriptionService.createSubscription(request);
        return new ResponseEntity<>(ApiResponse.success("Subscription created successfully", createdSubscription), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> updateSubscription(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSubscriptionRequest request) {
        SubscriptionDTO updatedSubscription = subscriptionService.updateSubscription(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subscription updated successfully", updatedSubscription));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> getSubscriptionById(@PathVariable Long id) {
        SubscriptionDTO subscription = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<SubscriptionDTO>>> getSubscriptionsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptionsByUserId(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> getActiveSubscriptionByUserId(@PathVariable Long userId) {
        SubscriptionDTO subscription = subscriptionService.getActiveSubscriptionByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<SubscriptionDTO>>> getSubscriptionsByStatus(
            @PathVariable SubscriptionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptionsByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> cancelSubscription(@PathVariable Long id) {
        SubscriptionDTO cancelledSubscription = subscriptionService.cancelSubscription(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully", cancelledSubscription));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> activateSubscription(@PathVariable Long id) {
        SubscriptionDTO activatedSubscription = subscriptionService.activateSubscription(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription activated successfully", activatedSubscription));
    }

    @PatchMapping("/{id}/renew")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> renewSubscription(@PathVariable Long id) {
        SubscriptionDTO renewedSubscription = subscriptionService.renewSubscription(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription renewed successfully", renewedSubscription));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<SubscriptionHistoryDTO>>> getSubscriptionHistory(@PathVariable Long id) {
        List<SubscriptionHistoryDTO> history = subscriptionHistoryService.getHistoryBySubscriptionId(id);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/{id}/history/paged")
    public ResponseEntity<ApiResponse<PageResponse<SubscriptionHistoryDTO>>> getSubscriptionHistoryPaged(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SubscriptionHistoryDTO> history = subscriptionHistoryService.getHistoryBySubscriptionId(id, page, size);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/{id}/history/date-range")
    public ResponseEntity<ApiResponse<List<SubscriptionHistoryDTO>>> getSubscriptionHistoryByDateRange(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<SubscriptionHistoryDTO> history = subscriptionHistoryService.getHistoryBySubscriptionIdAndDateRange(id, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}