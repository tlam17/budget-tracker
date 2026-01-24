# 💰 Budget vs. Reality Tracker (MVP)

A personal finance mobile app built with **SwiftUI (iOS)** and **Spring Boot + PostgreSQL** backend.  
This MVP helps users plan monthly budgets, log transactions, and visualize how their *actual spending* compares to their *planned amounts* — helping build better financial habits.

---

## 🧭 Overview

**Goal:**  
Provide users with a clean, intuitive budgeting tool to:
- Create and manage a monthly budget.
- Log daily transactions (expenses and income).
- Compare planned vs. actual spending per category.
- Visualize spending patterns through simple charts.

**Out of scope for MVP:**  
- Offline mode (moved to Phase 2).  
- Bank integrations (Plaid, AI categorization, etc.).  
- Shared budgets or collaboration features.

---

## ⚙️ Tech Stack

| Layer | Technology |
|-------|-------------|
| Mobile | SwiftUI (iOS 17+), MVVM architecture |
| Backend | Spring Boot 3.x (Java 17+) |
| Database | PostgreSQL |
| Auth | JWT-based authentication |
| API Spec | REST (JSON) |

---

## 🧩 Core Features (MVP Scope)

### 1. Authentication
**Description:**  
Users can create an account and log in securely.

**User Stories:**
- *As a user, I want to sign up with my email and password so that I can have a secure personal account.*
- *As a user, I want to log in and out easily so that my data is protected.*

**Acceptance Criteria:**
- [ ] Users can register with a valid email and password.  
- [ ] Passwords are securely hashed (BCrypt).  
- [ ] Users receive JWT upon login.  
- [ ] Authenticated requests must include valid JWT in headers.  
- [ ] Login and signup errors are clearly displayed in UI.

---

### 2. Category Management
**Description:**  
Users can define spending categories (e.g., Food, Rent, Entertainment).

**User Stories:**
- *As a user, I want to create and name my own categories so I can customize my budget.*
- *As a user, I want to edit or delete categories if I change my budgeting needs.*

**Acceptance Criteria:**
- [ ] User can create a new category (name + optional emoji).  
- [ ] User can view all categories in a list.  
- [ ] User can rename or delete categories.  
- [ ] Category changes reflect immediately in budget setup and transaction entry.

---

### 3. Monthly Budget Setup
**Description:**  
Users define planned spending per category for a chosen month.

**User Stories:**
- *As a user, I want to set a budget for each category so I can plan my monthly spending.*
- *As a user, I want to view total planned spending for the month.*

**Acceptance Criteria:**
- [ ] User can select a month and create a new budget period.  
- [ ] User can assign planned amounts (numeric input) to each category.  
- [ ] Total budgeted amount automatically sums across all categories.  
- [ ] User can view all active months in a list (e.g., “October 2025 Budget”).  
- [ ] Prevent duplicate budget periods (unique per user + month).

---

### 4. Transaction Logging
**Description:**  
Users record expenses and income transactions, linked to a category.

**User Stories:**
- *As a user, I want to quickly log each expense or income so that I can track where my money goes.*
- *As a user, I want to view my past transactions with details.*

**Acceptance Criteria:**
- [ ] User can log a transaction with:
  - Amount (positive = income, negative = expense)
  - Category
  - Date
  - Description (optional)
- [ ] Transactions display in a chronological list (most recent first).  
- [ ] User can edit or delete a transaction.  
- [ ] Amount input validation prevents invalid or empty entries.

---

### 5. Budget vs. Actual Dashboard
**Description:**  
Visual comparison of planned vs. actual spending.

**User Stories:**
- *As a user, I want to see how much I’ve spent versus my planned amount per category.*
- *As a user, I want a clear visualization of spending by category.*

**Acceptance Criteria:**
- [ ] Display each category with:
  - Planned amount
  - Actual spending
  - Remaining budget (planned - actual)
- [ ] Show totals for the entire month.  
- [ ] Pie chart or bar chart shows spending distribution by category.  
- [ ] Over-budget categories visually highlighted (e.g., red progress bar).  
- [ ] Chart updates dynamically when transactions change.

---

## 🧪 Non-Functional Requirements

| Area | Requirement |
|------|--------------|
| Security | JWT-based authentication, password hashing with BCrypt |
| Data Integrity | Backend validation for unique months, non-negative budgets |
| Scalability | Designed for single-user use initially, but scalable for multi-user |
| Performance | Must load dashboard within 1s on network connection |
| Usability | Intuitive UX: ≤ 3 taps to log a transaction |

---

## 🧭 Next Steps After MVP

(For reference; not part of MVP scope)
- Phase 2: Offline mode with SQLite sync.
- Phase 2: Recurring budgets, CSV import/export.
- Phase 3: Plaid integration, forecasting, shared budgets.

---

**Link to Figma Make:**
https://www.figma.com/make/pTIJV3ukkxgpCBl2PghSj4/Budget-vs.-Reality-Tracker?node-id=0-4&t=4vFDGWorWv6lN4Wo-1
