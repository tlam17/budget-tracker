package com.tylerlam.budgettracker.budget_tracker_api.budgetallocation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tylerlam.budgettracker.budget_tracker_api.budgetperiod.BudgetPeriod;
import com.tylerlam.budgettracker.budget_tracker_api.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "budget_allocations",
    uniqueConstraints = {
        @UniqueConstraint(name = "ux_budget_allocation_budget_period_id_category_id", columnNames = {"budget_period_id", "category_id"})
    }
)
public class BudgetAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "budget_period_id", nullable = false)
    private BudgetPeriod budgetPeriod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @DecimalMin("0.00")
    @Column(nullable = false, name = "planned_amount", precision = 19, scale = 2)
    private BigDecimal plannedAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;
}
