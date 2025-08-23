package org.crochet.subscription.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.subscription.config.PayOSConfig;
import org.crochet.subscription.dto.PayOSPaymentRequest;
import org.crochet.subscription.dto.PayOSPaymentResponse;
import org.crochet.subscription.dto.PayOSWebhookData;
import org.crochet.subscription.enums.PaymentStatus;
import org.crochet.subscription.exception.ResourceNotFoundException;
import org.crochet.subscription.model.Payment;
import org.crochet.subscription.repository.PaymentRepository;
import org.crochet.subscription.service.PayOSService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayOSServiceImpl implements PayOSService {

    private final PayOSConfig payOSConfig;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    public PayOSServiceImpl(PayOSConfig payOSConfig, PaymentRepository paymentRepository) {
        this.payOSConfig = payOSConfig;
        this.paymentRepository = paymentRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public PayOSPaymentResponse createPaymentLink(PayOSPaymentRequest request) {
        try {
            log.info("Creating PayOS payment link for order: {}", request.getOrderCode());

            // Create request payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderCode", Long.parseLong(request.getOrderCode()));
            payload.put("amount", (int) (request.getAmount().doubleValue() * 100)); // Convert to VND cents
            payload.put("description", request.getDescription());
            payload.put("returnUrl", request.getReturnUrl());
            payload.put("cancelUrl", request.getCancelUrl());

            // Create items array
            Map<String, Object> item = new HashMap<>();
            item.put("name", request.getDescription());
            item.put("quantity", 1);
            item.put("price", (int) (request.getAmount().doubleValue() * 100));
            payload.put("items", new Map[]{item});

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", payOSConfig.getClientId());
            headers.set("x-api-key", payOSConfig.getApiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            // Call PayOS API
            String url = "https://api-merchant.payos.vn/v2/payment-requests";
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseData = response.getBody();
                Map<String, Object> data = (Map<String, Object>) responseData.get("data");

                log.info("PayOS payment link created successfully");

                return PayOSPaymentResponse.builder()
                        .paymentLinkId((String) data.get("paymentLinkId"))
                        .orderCode(Long.parseLong(request.getOrderCode()))
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .paymentLink((String) data.get("paymentLink"))
                        .qrCode((String) data.get("qrCode"))
                        .status("PENDING")
                        .createdAt(LocalDateTime.now())
                        .expiredAt(LocalDateTime.now().plusMinutes(15))
                        .build();
            } else {
                throw new RuntimeException("Failed to create PayOS payment link: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to create PayOS payment link for order: {}", request.getOrderCode(), e);
            throw new RuntimeException("Failed to create payment link: " + e.getMessage());
        }
    }

    @Override
    public boolean processWebhook(PayOSWebhookData webhookData, String signature) {
        try {
            log.info("Processing PayOS webhook for order: {}", webhookData.getOrderCode());

            // Verify webhook signature
            if (!verifyWebhookSignature(webhookData, signature)) {
                log.error("Invalid webhook signature for order: {}", webhookData.getOrderCode());
                return false;
            }

            // Find payment by order code (transaction ID)
            Payment payment = paymentRepository.findByTransactionId(String.valueOf(webhookData.getOrderCode()))
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction id: " + webhookData.getOrderCode()));

            // Update payment status based on webhook data
            if ("success".equals(webhookData.getDescription()) || webhookData.getReference() != null) {
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setPaymentDate(LocalDateTime.now());
                log.info("Payment {} marked as completed", payment.getId());
            } else {
                payment.setPaymentStatus(PaymentStatus.CANCELLED);
                log.info("Payment {} marked as cancelled", payment.getId());
            }

            paymentRepository.save(payment);
            log.info("Successfully processed PayOS webhook for order: {}", webhookData.getOrderCode());
            return true;

        } catch (Exception e) {
            log.error("Failed to process PayOS webhook for order: {}", webhookData.getOrderCode(), e);
            return false;
        }
    }

    @Override
    public boolean cancelPaymentLink(String paymentLinkId) {
        try {
            log.info("Cancelling PayOS payment link: {}", paymentLinkId);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-client-id", payOSConfig.getClientId());
            headers.set("x-api-key", payOSConfig.getApiKey());

            HttpEntity<?> entity = new HttpEntity<>(headers);

            // Call PayOS API to cancel
            String url = "https://api-merchant.payos.vn/v2/payment-requests/" + paymentLinkId + "/cancel";
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully cancelled PayOS payment link: {}", paymentLinkId);
                return true;
            } else {
                log.error("Failed to cancel PayOS payment link: {} - {}", paymentLinkId, response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to cancel PayOS payment link: {}", paymentLinkId, e);
            return false;
        }
    }

    @Override
    public PayOSPaymentResponse getPaymentLinkInfo(String paymentLinkId) {
        try {
            log.info("Getting PayOS payment link info: {}", paymentLinkId);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-client-id", payOSConfig.getClientId());
            headers.set("x-api-key", payOSConfig.getApiKey());

            HttpEntity<?> entity = new HttpEntity<>(headers);

            // Call PayOS API to get payment link info
            String url = "https://api-merchant.payos.vn/v2/payment-requests/" + paymentLinkId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseData = response.getBody();
                Map<String, Object> data = (Map<String, Object>) responseData.get("data");

                return PayOSPaymentResponse.builder()
                        .paymentLinkId((String) data.get("paymentLinkId"))
                        .orderCode(((Number) data.get("orderCode")).longValue())
                        .amount(BigDecimal.valueOf(((Number) data.get("amount")).intValue()).divide(BigDecimal.valueOf(100)))
                        .description((String) data.get("description"))
                        .paymentLink((String) data.get("paymentLink"))
                        .qrCode((String) data.get("qrCode"))
                        .status((String) data.get("status"))
                        .createdAt(LocalDateTime.now())
                        .build();
            } else {
                throw new RuntimeException("Failed to get PayOS payment link info: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to get PayOS payment link info: {}", paymentLinkId, e);
            throw new RuntimeException("Failed to get payment link info: " + e.getMessage());
        }
    }

    private boolean verifyWebhookSignature(PayOSWebhookData webhookData, String signature) {
        try {
            String data = webhookData.getOrderCode() + "|" +
                         webhookData.getAmount() + "|" +
                         webhookData.getDescription() + "|" +
                         webhookData.getTransactionDateTime();

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                payOSConfig.getWebhookSecret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
            );
            mac.init(secretKey);

            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hash);

            return expectedSignature.equals(signature);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to verify webhook signature", e);
            return false;
        }
    }
}
