# Tích hợp PayOS vào Subscription Service

## Tổng quan

Hệ thống subscription service đã được tích hợp với cổng thanh toán PayOS để hỗ trợ thanh toán qua QR code ngân hàng 24/7.

## Các tính năng đã tích hợp

### 1. Cấu hình PayOS
- Thêm cấu hình PayOS trong `application.yml`
- Client ID, API Key, Checksum Key, Webhook Secret
- URLs cho return, cancel và webhook

### 2. Payment Model
- Thêm các trường mới cho PayOS:
  - `payment_link_id`: ID của link thanh toán PayOS
  - `qr_code`: Mã QR để quét thanh toán
  - `payment_url`: URL thanh toán PayOS
  - `webhook_data`: Dữ liệu từ webhook PayOS

### 3. PayOS Service
- `PayOSService`: Interface cho các thao tác PayOS
- `PayOSServiceImpl`: Implementation với PayOS SDK
- Hỗ trợ tạo link thanh toán, xử lý webhook, hủy link

### 4. Payment Service
- Mở rộng `PaymentService` với các method PayOS:
  - `createPayOSPaymentLink()`: Tạo link thanh toán PayOS
  - `processPayOSWebhook()`: Xử lý webhook từ PayOS
  - `getPaymentByPaymentLinkId()`: Lấy payment theo link ID

### 5. API Endpoints
- `POST /api/payments/payos/create-link/{paymentId}`: Tạo link thanh toán
- `GET /api/payments/payos/return`: Xử lý return URL
- `GET /api/payments/payos/cancel`: Xử lý cancel URL
- `POST /api/payments/payos/webhook`: Xử lý webhook
- `GET /api/payments/payos/payment/{paymentLinkId}`: Lấy thông tin payment

### 6. Database Migration
- Migration `002-add-payos-fields.yaml` để thêm các trường PayOS vào bảng payment

## Cách sử dụng

### 1. Cấu hình môi trường

Thêm các biến môi trường sau:

```bash
PAYOS_CLIENT_ID=your_client_id
PAYOS_API_KEY=your_api_key
PAYOS_CHECKSUM_KEY=your_checksum_key
PAYOS_WEBHOOK_SECRET=your_webhook_secret
PAYOS_RETURN_URL=http://localhost:8081/api/payments/payos/return
PAYOS_CANCEL_URL=http://localhost:8081/api/payments/payos/cancel
PAYOS_WEBHOOK_URL=http://localhost:8081/api/payments/payos/webhook
```

### 2. Tạo payment với PayOS

```java
// 1. Tạo payment thông thường
CreatePaymentRequest request = CreatePaymentRequest.builder()
    .subscriptionId(subscriptionId)
    .amount(new BigDecimal("100000"))
    .paymentMethod(PaymentMethod.PAYOS)
    .build();

PaymentDTO payment = paymentService.createPayment(request);

// 2. Tạo link thanh toán PayOS
PaymentDTO payosPayment = paymentService.createPayOSPaymentLink(payment.getId());

// 3. Lấy QR code và URL thanh toán
String qrCode = payosPayment.getQrCode();
String paymentUrl = payosPayment.getPaymentUrl();
```

### 3. Xử lý webhook từ PayOS

PayOS sẽ gửi webhook đến endpoint `/api/payments/payos/webhook` khi có sự kiện thanh toán:

```java
@PostMapping("/webhook")
public ResponseEntity<ApiResponse<String>> handleWebhook(
        @RequestParam Long orderCode,
        @RequestBody String webhookData,
        @RequestHeader("x-payos-signature") String signature) {
    // Webhook sẽ được xử lý tự động
}
```

### 4. Xử lý return/cancel URL

Khi người dùng hoàn thành hoặc hủy thanh toán, PayOS sẽ redirect về các URL:

- Return URL: `/api/payments/payos/return`
- Cancel URL: `/api/payments/payos/cancel`

## Luồng thanh toán

1. **Tạo payment**: Client tạo payment với phương thức PAYOS
2. **Tạo link thanh toán**: Server tạo link thanh toán PayOS và trả về QR code + URL
3. **Hiển thị QR**: Client hiển thị QR code hoặc redirect đến URL thanh toán
4. **Thanh toán**: Người dùng quét QR và thanh toán qua app ngân hàng
5. **Webhook**: PayOS gửi webhook về server để cập nhật trạng thái
6. **Redirect**: Người dùng được redirect về trang thành công/thất bại

## Bảo mật

- Sử dụng signature để verify webhook từ PayOS
- Validate các tham số bắt buộc
- Kiểm tra trạng thái payment trước khi xử lý

## Testing

Để test tích hợp PayOS, bạn có thể:

1. Sử dụng PayOS sandbox environment
2. Tạo payment test với số tiền nhỏ
3. Sử dụng app ngân hàng để quét QR test
4. Kiểm tra webhook và database updates

## Troubleshooting

### Lỗi thường gặp:

1. **Invalid signature**: Kiểm tra `PAYOS_WEBHOOK_SECRET` có đúng không
2. **Payment not found**: Kiểm tra `transactionId` có được set đúng không
3. **PayOS API error**: Kiểm tra credentials và network connection

### Logs quan trọng:

- Tất cả hoạt động PayOS được log với prefix `PayOS`
- Webhook processing được log chi tiết
- Payment status changes được track

## Dependencies

```gradle
implementation 'vn.payos:payos-java:1.0.7'
```

## API Reference

### PayOS Official Documentation
- Website: https://payos.vn/docs/
- SDK Documentation: https://payos.vn/docs/sdk

### Các endpoint chính:

- **Create payment link**: `POST /api/payments/payos/create-link/{paymentId}`
- **Webhook**: `POST /api/payments/payos/webhook`
- **Return URL**: `GET /api/payments/payos/return`
- **Cancel URL**: `GET /api/payments/payos/cancel`

## Support

Nếu có vấn đề với tích hợp PayOS, hãy kiểm tra:

1. PayOS dashboard để xem payment status
2. Application logs để debug
3. Network connectivity đến PayOS API
4. Database migration đã chạy thành công
