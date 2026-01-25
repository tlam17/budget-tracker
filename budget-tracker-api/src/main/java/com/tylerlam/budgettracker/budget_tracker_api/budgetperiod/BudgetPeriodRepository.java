package com.tylerlam.budgettracker.budget_tracker_api.budgetperiod;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetPeriodRepository extends JpaRepository<BudgetPeriod, UUID> {

}
