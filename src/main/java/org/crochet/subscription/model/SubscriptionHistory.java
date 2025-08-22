package org.crochet.subscription.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.subscription.enums.SubscriptionAction;
import org.crochet.subscription.enums.SubscriptionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private SubscriptionAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private SubscriptionStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private SubscriptionStatus newStatus;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}