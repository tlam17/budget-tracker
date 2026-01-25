package com.tylerlam.budgettracker.budget_tracker_api.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tylerlam.budgettracker.budget_tracker_api.category.Category;
import com.tylerlam.budgettracker.budget_tracker_api.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "transactions",
    indexes = {
        @Index(name = "idx_transaction_user_id_occurred_at", columnList = "user_id, occurred_at"),
        @Index(name = "idx_transaction_user_id_category_id_occurred_at", columnList = "user_id, category_id, occurred_at")
    }
)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;
}
