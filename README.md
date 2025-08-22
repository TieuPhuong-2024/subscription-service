# Subscription Service

Dịch vụ quản lý đăng ký (subscription) được xây dựng bằng Spring Boot và sử dụng Liquibase để quản lý database schema.

## Tính năng

- Quản lý gói đăng ký (Subscription Plans)
- Quản lý đăng ký của khách hàng (Subscriptions)
- Quản lý thanh toán (Payments)
- Lịch sử đăng ký (Subscription History)
- Tích hợp với Spring Cloud (Eureka, Config, Circuit Breaker)
- API Documentation với OpenAPI/Swagger

## Công nghệ sử dụng

- **Framework**: Spring Boot 3.4.3
- **Database**: PostgreSQL
- **Database Migration**: Liquibase
- **Build Tool**: Gradle
- **Java Version**: 21
- **Spring Cloud**: 2024.0.0-RC1

## Cấu trúc dự án

```
subscription-service/
├── src/
│   ├── main/
│   │   ├── java/org/crochet/subscription/
│   │   │   ├── config/           # Cấu hình
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── enums/           # Enumerations
│   │   │   ├── event/           # Events
│   │   │   ├── exception/       # Exception handlers
│   │   │   ├── mapper/          # MapStruct mappers
│   │   │   ├── model/           # Entity models
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── scheduler/       # Scheduled tasks
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── application.yml  # Application configuration
│   │       └── db/
│   │           └── changelog/   # Liquibase changelogs
│   └── test/                    # Unit tests
├── scripts/                     # Helper scripts
├── build.gradle                # Gradle build configuration
└── README.md                   # This file
```

## Cài đặt và chạy

### Yêu cầu hệ thống

- Java 21
- PostgreSQL 12+
- Gradle 8.0+

### 1. Clone repository

```bash
git clone <repository-url>
cd subscription-service
```

### 2. Cấu hình database

Tạo database PostgreSQL và cấu hình thông tin kết nối trong biến môi trường:

```bash
# Linux/Mac
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=subscription_db
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

# Windows
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=subscription_db
set DB_USERNAME=postgres
set DB_PASSWORD=your_password
```

### 3. Chạy database migration

```bash
# Sử dụng Gradle
./gradlew liquibaseUpdate

# Hoặc sử dụng helper script
./scripts/liquibase-helper.sh update main
# Windows
scripts\liquibase-helper.bat update main
```

### 4. Build và chạy ứng dụng

```bash
# Build
./gradlew build

# Chạy
./gradlew bootRun
```

Ứng dụng sẽ chạy tại: http://localhost:8080

## Database Migration với Liquibase

Dự án sử dụng Liquibase để quản lý schema database. Xem chi tiết tại [LIQUIBASE_USAGE.md](LIQUIBASE_USAGE.md).

### Các lệnh Liquibase cơ bản

```bash
# Cập nhật database
./gradlew liquibaseUpdate

# Kiểm tra trạng thái
./gradlew liquibaseStatus

# Tạo SQL (dry run)
./gradlew liquibaseUpdateSQL

# Rollback
./gradlew liquibaseRollback -ProllbackCount=1
```

### Sử dụng helper scripts

```bash
# Linux/Mac
./scripts/liquibase-helper.sh update dev
./scripts/liquibase-helper.sh status main

# Windows
scripts\liquibase-helper.bat update dev
scripts\liquibase-helper.bat status main
```

## API Documentation

Sau khi chạy ứng dụng, truy cập Swagger UI tại:
http://localhost:8080/swagger-ui.html

## Các endpoints chính

### Subscription Plans
- `GET /api/subscription-plans` - Lấy danh sách gói đăng ký
- `POST /api/subscription-plans` - Tạo gói đăng ký mới
- `PUT /api/subscription-plans/{id}` - Cập nhật gói đăng ký
- `DELETE /api/subscription-plans/{id}` - Xóa gói đăng ký

### Subscriptions
- `GET /api/subscriptions` - Lấy danh sách đăng ký
- `POST /api/subscriptions` - Tạo đăng ký mới
- `PUT /api/subscriptions/{id}` - Cập nhật đăng ký
- `DELETE /api/subscriptions/{id}` - Hủy đăng ký

### Payments
- `GET /api/payments` - Lấy danh sách thanh toán
- `POST /api/payments` - Tạo thanh toán mới
- `PUT /api/payments/{id}` - Cập nhật thanh toán

## Development

### Chạy tests

```bash
./gradlew test
```

### Code formatting

```bash
./gradlew spotlessApply
```

### Build Docker image

```bash
./gradlew bootBuildImage
```

## Deployment

### Docker

```bash
# Build image
docker build -t subscription-service .

# Run container
docker run -p 8080:8080 \
  -e DB_HOST=your-db-host \
  -e DB_PORT=5432 \
  -e DB_NAME=subscription_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=your-password \
  subscription-service
```

### Kubernetes

```bash
kubectl apply -f k8s/
```

## Monitoring

Ứng dụng tích hợp với Spring Boot Actuator. Truy cập các endpoints:

- Health check: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Info: http://localhost:8080/actuator/info

## Troubleshooting

### Lỗi thường gặp

1. **Database connection failed**
   - Kiểm tra PostgreSQL có đang chạy không
   - Kiểm tra thông tin kết nối database
   - Chạy migration: `./gradlew liquibaseUpdate`

2. **Liquibase errors**
   - Xem [LIQUIBASE_USAGE.md](LIQUIBASE_USAGE.md) để biết thêm chi tiết
   - Chạy: `./gradlew clearChecksums` nếu gặp lỗi checksum (task ẩn)

3. **Build errors**
   - Kiểm tra Java version: `java -version`
   - Clean và rebuild: `./gradlew clean build`

## Contributing

1. Fork repository
2. Tạo feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push branch: `git push origin feature/new-feature`
5. Tạo Pull Request

## License

[MIT License](LICENSE)

## Liên hệ

- Email: [your-email@example.com]
- GitHub: [your-github-username]
