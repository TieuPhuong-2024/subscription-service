package org.crochet.subscription.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.crochet.subscription.dto.PaymentDTO;
import org.crochet.subscription.dto.request.CreatePaymentRequest;
import org.crochet.subscription.dto.request.UpdatePaymentRequest;
import org.crochet.subscription.dto.response.ApiResponse;
import org.crochet.subscription.dto.response.PageResponse;
import org.crochet.subscription.enums.PaymentStatus;
import org.crochet.subscription.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO>> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentDTO createdPayment = paymentService.createPayment(request);
        return new ResponseEntity<>(ApiResponse.success("Payment created successfully", createdPayment), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentRequest request) {
        PaymentDTO updatedPayment = paymentService.updatePayment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Payment updated successfully", updatedPayment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(@PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<ApiResponse<PageResponse<PaymentDTO>>> getPaymentsBySubscriptionId(
            @PathVariable Long subscriptionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<PaymentDTO> payments = paymentService.getPaymentsBySubscriptionId(subscriptionId, page, size);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<PaymentDTO>>> getPaymentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<PaymentDTO> payments = paymentService.getPaymentsByUserId(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByTransactionId(@PathVariable String transactionId) {
        PaymentDTO payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<PaymentDTO>>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<PaymentDTO> payments = paymentService.getPaymentsByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PatchMapping("/{id}/success")
    public ResponseEntity<ApiResponse<PaymentDTO>> processSuccessfulPayment(@PathVariable Long id) {
        PaymentDTO processedPayment = paymentService.processSuccessfulPayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", processedPayment));
    }

    @PatchMapping("/{id}/failure")
    public ResponseEntity<ApiResponse<PaymentDTO>> processFailedPayment(@PathVariable Long id) {
        PaymentDTO processedPayment = paymentService.processFailedPayment(id, null);
        return ResponseEntity.ok(ApiResponse.success("Payment marked as failed", processedPayment));
    }
}