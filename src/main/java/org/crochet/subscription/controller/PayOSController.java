package org.crochet.subscription.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.subscription.dto.PaymentDTO;
import org.crochet.subscription.dto.response.ApiResponse;
import org.crochet.subscription.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments/payos")
@RequiredArgsConstructor
public class PayOSController {

    private final PaymentService paymentService;

    @PostMapping("/create-link/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> createPaymentLink(@PathVariable Long paymentId) {
        try {
            log.info("Creating PayOS payment link for payment ID: {}", paymentId);
            PaymentDTO payment = paymentService.createPayOSPaymentLink(paymentId);
            return ResponseEntity.ok(ApiResponse.success("Payment link created successfully", payment));
        } catch (IllegalStateException e) {
            log.error("Failed to create payment link for payment {}: {}", paymentId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Payment is already processed"));
        } catch (Exception e) {
            log.error("Failed to create payment link for payment {}: {}", paymentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create payment link: " + e.getMessage()));
        }
    }

    @GetMapping("/return")
    public ResponseEntity<ApiResponse<String>> handleReturnUrl(
            @RequestParam Long paymentId,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String status) {
        try {
            log.info("Handling PayOS return URL for payment ID: {}, orderCode: {}, status: {}",
                    paymentId, orderCode, status);

            // This is just for redirect purposes - actual payment processing is done via webhook
            String redirectUrl = "http://localhost:3000/payment/success?paymentId=" + paymentId;

            if ("CANCELLED".equals(status)) {
                redirectUrl = "http://localhost:3000/payment/cancelled?paymentId=" + paymentId;
            }

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();

        } catch (Exception e) {
            log.error("Error handling PayOS return URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error processing return URL"));
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> handleCancelUrl(
            @RequestParam Long paymentId,
            @RequestParam(required = false) String orderCode) {
        try {
            log.info("Handling PayOS cancel URL for payment ID: {}, orderCode: {}",
                    paymentId, orderCode);

            String redirectUrl = "http://localhost:3000/payment/cancelled?paymentId=" + paymentId;

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();

        } catch (Exception e) {
            log.error("Error handling PayOS cancel URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error processing cancel URL"));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> handleWebhook(
            @RequestParam Long orderCode,
            @RequestBody String webhookData,
            @RequestHeader(value = "x-payos-signature", required = false) String signature) {
        try {
            log.info("Received PayOS webhook for order code: {}", orderCode);

            PaymentDTO payment = paymentService.processPayOSWebhook(orderCode, webhookData, signature);
            log.info("Successfully processed PayOS webhook for payment ID: {}", payment.getId());

            return ResponseEntity.ok(ApiResponse.success("Webhook processed successfully"));

        } catch (Exception e) {
            log.error("Failed to process PayOS webhook for order code {}: {}", orderCode, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process webhook: " + e.getMessage()));
        }
    }

    @GetMapping("/payment/{paymentLinkId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByLinkId(@PathVariable String paymentLinkId) {
        try {
            PaymentDTO payment = paymentService.getPaymentByPaymentLinkId(paymentLinkId);
            return ResponseEntity.ok(ApiResponse.success(payment));
        } catch (Exception e) {
            log.error("Failed to get payment by link ID {}: {}", paymentLinkId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Payment not found"));
        }
    }
}
