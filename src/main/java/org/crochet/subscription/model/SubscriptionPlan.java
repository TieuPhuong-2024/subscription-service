package org.crochet.subscription.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.PlanDuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration", nullable = false)
    private PlanDuration duration;

    @Column(name = "features")
    private String features;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}