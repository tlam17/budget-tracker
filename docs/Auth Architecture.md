# 🔐 Auth System Architecture (JWT)

This document diagrams how authentication works in the Budget vs. Reality Tracker using **JWTs** with Spring Boot. It covers login, token issuance, authenticated requests, and refresh.

---

## 1) High-Level Components

```mermaid
flowchart LR

  A["iOS App (SwiftUI)"] -->|"/api/* over HTTPS"| B["API Gateway / LB"]
  B --> C["Spring Boot App"]

  subgraph "Spring Boot"
    C --> D["Spring Security Filter Chain"]
    D --> E["Auth Controller"]
    D --> F["Domain Controllers"]
    E --> G["Auth Service"]
    G --> H["User Repository (JPA)"]
    G --> I["Password Hasher (BCrypt)"]
    G --> J["JWT Issuer (RS256 Private Key)"]
    D --> K["JWT Verifier (RS256 Public Key)"]
  end

  H --> L["PostgreSQL"]
  M["Redis (optional)"] --- G
  M --- K
```

**Notes**

* **RS256** (asymmetric) recommended so mobile apps never need a secret; server signs with **private key**, verifies with **public key**.
* **Redis (optional)** for refresh-token rotation / revocation lists if needed.
* iOS stores tokens in **Keychain**.

---

## 2) Login & Token Issuance

```mermaid
sequenceDiagram
  autonumber
  participant U as User (SwiftUI)
  participant E as POST /auth/login
  participant S as AuthService
  participant R as UserRepository
  participant B as BCrypt
  participant J as JWT Issuer (sign)

  U->>E: POST /auth/login (email, password)
  E->>S: authenticate(email, password)
  S->>R: findByEmail(email)
  R-->>S: user + password_hash
  S->>B: verify(password, hash)
  B-->>S: OK
  S->>J: sign access (sub, email, roles, iat, exp)
  J-->>S: access_token (15m)
  S-->>U: 200 OK (access_token, refresh_token 30d)
  Note over U: Store tokens securely in iOS Keychain
```

**Claims & Lifetimes (suggested):**

* `access_token` **15 min** (short‑lived)
* `refresh_token` **30 days**, opaque or JWT with `typ=refresh`, rotate on every refresh
* Include `sub`, `email`, `roles`, `iat`, `exp`, `jti` (for rotation)

---

## 3) Authenticated API Request (with JWT)

```mermaid
sequenceDiagram
  autonumber
  participant U as User (SwiftUI)
  participant G as API Gateway/LB
  participant F as Spring Security Filter Chain
  participant V as JWT Verifier
  participant D as Controller/Service

  U->>G: GET /api/transactions (Authorization: Bearer <token>)
  G->>F: Forward request
  F->>V: Validate signature, exp, audience
  V-->>F: Valid -> create SecurityContext(UserId, Roles)
  F->>D: Invoke controller with Principal
  D-->>U: 200 OK (JSON data)
```

**Failure paths:**

* Missing/invalid token → **401** with `WWW-Authenticate: Bearer`
* Token expired → **401** `token_expired` (client should try refresh)
* Insufficient role/scope → **403 Forbidden**

---

## 4) Token Refresh (Rotation, Optional but Recommended)

```mermaid
sequenceDiagram
  autonumber
  participant U as User (SwiftUI)
  participant E as POST /auth/refresh
  participant S as AuthService
  participant C as Store (Redis/DB)
  participant J as JWT Issuer

  U->>E: POST /auth/refresh (refresh_token)
  E->>S: validate(refresh_token)
  S->>C: check jti not revoked and is latest
  C-->>S: OK
  S->>J: sign new access (15m) + new refresh (30d)
  S->>C: revoke old jti
  S->>C: save new jti
  S-->>U: 200 OK (access_token, refresh_token)

```

**Rotation rules:**

* Each refresh **invalidates** the previous refresh token (`jti` blacklist or version counter per user).
* On suspicious activity, **revoke all** user tokens (set user token version ↑).

---

## 5) Sequence: Logout

```mermaid
sequenceDiagram
  autonumber
  participant U as User (SwiftUI)
  participant X as POST /auth/logout
  participant C as Store (Redis/DB)

  U->>X: POST /auth/logout (refresh_token)
  X->>C: revoke refresh jti (and optionally current access jti)
  X-->>U: 204 No Content
  Note over U: Delete tokens from Keychain
```

---

## 6) Spring Boot Implementation Notes

* **Spring Security**

  * Add a `JwtAuthenticationFilter` **before** `UsernamePasswordAuthenticationFilter`.
  * Use `AuthenticationManager` only for `/auth/login` (password auth).
  * Controllers are annotated with `@PreAuthorize("hasRole('USER')")` where needed.
* **Password hashing:** `BCryptPasswordEncoder` (strength 10–12).
* **Key management:**

  * RS256 keypair generated offline; private key injected via secret manager.
  * Roll keys with `kid` header; maintain a small **JWKS** set for verification.
* **Error model:** consistent JSON `{code, message}` for 401/403.

---

## 7) Client (iOS) Storage & Behavior

* Store tokens in **Keychain**; never in UserDefaults.
* Attach `Authorization: Bearer <access_token>` to protected requests.
* On 401 `token_expired`, call `/auth/refresh`; if that fails, route to login.
* Clock skew: refresh proactively when token has <2 minutes remaining.

---

## 8) Minimal Endpoints (MVP)

```
POST   /auth/register       {email, password}
POST   /auth/login          {email, password}
POST   /auth/refresh        {refresh_token}
POST   /auth/logout         {refresh_token}
GET    /me                  -> current user profile
```

---

## 9) Security Defaults (MVP)

* Enforce HTTPS only.
* CORS: allow iOS app origin(s) only; restrict methods/headers.
* Rate limit `/auth/*` (e.g., via gateway or Spring filter).
* Audit logs for auth events (login success/failure, refresh, revoke).
