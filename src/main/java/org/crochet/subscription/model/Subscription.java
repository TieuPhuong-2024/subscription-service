package org.crochet.subscription.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.SubscriptionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "auto_renew", nullable = false)
    private boolean autoRenew;

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

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && 
               LocalDateTime.now().isBefore(endDate);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }

    public boolean isPending() {
        return status == SubscriptionStatus.PENDING;
    }

    public boolean isCancelled() {
        return status == SubscriptionStatus.CANCELLED;
    }
}