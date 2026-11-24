# SmartResolve — Architecture Documentation

## Overview

SmartResolve follows the classic **Model-View-Controller (MVC)** pattern implemented
with Java Servlets, JSP, and JDBC on Apache Tomcat 10.1.

## Layer Descriptions

### Controller Layer (`com.smartresolve.controller`)
Servlet classes that handle HTTP requests.

Responsibilities:
- Read and sanitize HTTP request parameters
- Validate input using `ValidationUtil`
- Call service layer methods
- Set request attributes for JSP rendering
- Forward to JSP view or redirect (PRG pattern)

Servlets must NOT contain SQL queries or complex business logic.

### Service Layer (`com.smartresolve.service`)
Pure Java classes containing business rules.

Responsibilities:
- Complaint status transition validation
- Role-based authorization checks
- Coordinating multi-step operations (e.g., assign + record history)
- Wrapping DAO calls in transactions

### DAO Layer (`com.smartresolve.dao`)
Data Access Objects using JDBC.

Responsibilities:
- All SQL queries using `PreparedStatement`
- Mapping `ResultSet` rows to Model objects
- Resource cleanup with try-with-resources
- Database transactions for multi-step updates

### Model Layer (`com.smartresolve.model`)
Plain Java classes (POJOs/JavaBeans) representing domain entities.

Classes: `User`, `Complaint`, `Category`, `ComplaintUpdate`, `Feedback`

### Filter Layer (`com.smartresolve.filter`)
Jakarta Servlet filters executed before every protected request.

- `AuthenticationFilter` — checks session for logged-in user
- `AuthorizationFilter` — checks user role matches the requested path

### Utility Layer (`com.smartresolve.util`)
Helper classes:
- `DBConnection` — singleton JDBC connection factory
- `PasswordUtil` — BCrypt hashing and verification
- `ValidationUtil` — reusable input validation methods

## Request Flow

```
HTTP Request
    ↓
[AuthenticationFilter]  → not logged in? → redirect to /login
    ↓
[AuthorizationFilter]   → wrong role? → 403 Forbidden
    ↓
[Servlet.doGet() / doPost()]
    ↓ calls
[Service]
    ↓ calls
[DAO] ← PreparedStatement → MySQL
    ↑ Model objects
[Service] → returns result to Servlet
    ↓
[Servlet] → request.setAttribute("data", result)
    ↓
[RequestDispatcher.forward()] → JSP
    ↓
[JSP + JSTL/EL] → renders HTML
    ↑
HTTP Response
```

## Post/Redirect/Get Pattern

All form submissions use PRG:
1. `POST /complaint/create` — process form
2. `response.sendRedirect("/user/complaints")` — redirect on success
3. `GET /user/complaints` — load fresh data

This prevents duplicate form submission on browser refresh.

## Session Management

- Login: invalidate old session → create new session → store `userId` and `role`
- Every protected request: filter reads session → validates role → allows/denies
- Logout: `session.invalidate()`
- Timeout: 30 minutes (configured in web.xml)

## Complaint Lifecycle State Machine

```
NEW
 ↓  (Admin assigns technician)
ASSIGNED
 ↓  (Technician starts work)
IN_PROGRESS
 ↓  (Technician marks resolved)
RESOLVED
 ↓  (User confirms resolution)
CLOSED
 ↑  (Admin can reopen from RESOLVED or CLOSED)
```

Every transition is recorded in `complaint_updates` within a database transaction.
