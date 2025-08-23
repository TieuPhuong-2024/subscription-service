package org.crochet.subscription.util;

import org.crochet.subscription.enums.PaymentMethod;

/**
 * Utility class for PaymentMethod operations
 */
public class PaymentMethodUtil {

    /**
     * Kiểm tra xem phương thức thanh toán có được PayOS hỗ trợ không
     */
    public static boolean isPayOSSupported(PaymentMethod method) {
        return method != null;
    }

    /**
     * Lấy tên hiển thị cho phương thức thanh toán
     */
    public static String getDisplayName(PaymentMethod method) {
        if (method == null) return null;

        return switch (method) {
            case CREDIT_CARD -> "Thẻ tín dụng/Ghi nợ";
            case BANK_TRANSFER -> "Chuyển khoản ngân hàng";
            case EWALLET -> "Ví điện tử PayOS";
            case QR_CODE -> "QR Code";
            case DOMESTIC_CARD -> "Thẻ nội địa";
            case INTERNATIONAL_CARD -> "Thẻ quốc tế";
        };
    }

    /**
     * Lấy mô tả cho phương thức thanh toán
     */
    public static String getDescription(PaymentMethod method) {
        if (method == null) return null;

        return switch (method) {
            case CREDIT_CARD -> "Thanh toán bằng thẻ tín dụng hoặc thẻ ghi nợ Visa, Mastercard";
            case BANK_TRANSFER -> "Chuyển khoản qua Internet Banking hoặc tại quầy";
            case EWALLET -> "Thanh toán qua ví điện tử PayOS";
            case QR_CODE -> "Quét mã QR để thanh toán";
            case DOMESTIC_CARD -> "Thẻ ngân hàng nội địa";
            case INTERNATIONAL_CARD -> "Thẻ tín dụng quốc tế";
        };
    }
}
