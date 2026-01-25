package com.tylerlam.budgettracker.budget_tracker_api.budgetallocation;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetAllocationRepository extends JpaRepository<BudgetAllocation, UUID> {

}
