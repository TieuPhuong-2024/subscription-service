package org.crochet.subscription.service;

import org.crochet.subscription.dto.PayOSPaymentRequest;
import org.crochet.subscription.dto.PayOSPaymentResponse;
import org.crochet.subscription.dto.PayOSWebhookData;

public interface PayOSService {

    /**
     * Tạo link thanh toán PayOS
     *
     * @param request thông tin thanh toán
     * @return response chứa link thanh toán và QR code
     */
    PayOSPaymentResponse createPaymentLink(PayOSPaymentRequest request);

    /**
     * Xử lý webhook từ PayOS
     *
     * @param webhookData dữ liệu từ webhook
     * @param signature chữ ký để verify
     * @return true nếu xử lý thành công
     */
    boolean processWebhook(PayOSWebhookData webhookData, String signature);

    /**
     * Hủy link thanh toán
     *
     * @param paymentLinkId ID của link thanh toán
     * @return true nếu hủy thành công
     */
    boolean cancelPaymentLink(String paymentLinkId);

    /**
     * Lấy thông tin link thanh toán
     *
     * @param paymentLinkId ID của link thanh toán
     * @return thông tin link thanh toán
     */
    PayOSPaymentResponse getPaymentLinkInfo(String paymentLinkId);
}
