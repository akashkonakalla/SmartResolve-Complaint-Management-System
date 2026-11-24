# 🛠️ SmartResolve — Enterprise Complaint & Service Resolution System

<div align="center">

![Java](https://img.shields.io/badge/Java-25%20%7C%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-107C41?style=for-the-badge&logo=eclipse&logoColor=white)
![Tomcat](https://img.shields.io/badge/Apache%20Tomcat-10.1-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)
![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)
![Security](https://img.shields.io/badge/Security-BCrypt%20%2B%20CSRF-green?style=for-the-badge&logo=springsecurity&logoColor=white)

<p align="center">
  <strong>A transparent, role-based service request management platform built with modern Jakarta Servlets, JDBC, and MySQL.</strong><br>
  Eliminating lost complaints and messy communication channels through structured workflows, verifiable audit histories, and citizen ratings.
</p>

[Explore Architecture](docs/architecture.md) • [Database Design](docs/database-design.md) • [Local Setup Guide](docs/setup-guide.md) • [Interview & Viva Guide](docs/interview-guide.md)

</div>

---

## 💡 Why SmartResolve?

If you have ever lived in a college hostel, managed an apartment block, or worked in an office facility, you know the frustration:
- A circuit breaker trips or water supply stops.
- A complaint is fired into an unmonitored WhatsApp group or mentioned verbally to someone passing by in the hallway.
- Days pass with zero updates. Residents grow frustrated, maintenance staff are blamed unfairly, and nobody knows who actually owns the task.

**SmartResolve** was built to replace informal chaos with complete operational visibility. Every maintenance issue submitted through SmartResolve receives a unique tracking number, an assigned specialist, a chronological timeline of actions, and a mandatory quality confirmation from the person who reported it.

---

## 🎯 Key Capabilities at a Glance

### 👥 For Residents & Employees (Users)
- **Frictionless Submission:** File tickets in seconds with targeted categories, priority indicators, exact locations, and optional photo attachments.
- **Live Lifecycle Tracking:** Follow work order progression in real-time (`NEW` ➔ `ASSIGNED` ➔ `IN_PROGRESS` ➔ `RESOLVED` ➔ `CLOSED`).
- **Complete Timeline Audit:** Read notes and timestamped status updates posted directly by technicians and administrators.
- **Resolution Confirmation & Feedback:** Verify completion quality with a 5-star rating system and remarks before a ticket is officially closed.

### 🛡️ For Operations Managers & Facility Heads (Administrators)
- **Centralized Command Center:** Real-time metrics on open, in-progress, and resolved workloads across the entire facility.
- **Smart Technician Dispatch:** View specialist availability, evaluate existing task loads, and assign technicians with custom operational instructions.
- **Granular Multi-Criteria Filtering:** Filter records instantly by status, priority tier, category, and textual search queries.
- **Ticket Reopening Rights:** Re-engage closed or resolved tickets if issues recur or citizen feedback indicates incomplete repair work.

### 🔧 For Field Technicians
- **Dedicated Workbench:** A distraction-free dashboard showing assigned tickets, priority levels, and exact physical locations.
- **Actionable Status Progression:** Transition jobs from `ASSIGNED` to `IN_PROGRESS` when starting work, and mark them `RESOLVED` with diagnostic remarks upon completion.
- **Audit Logging:** Maintain a verifiable record of physical repairs, parts replaced, and resolution notes.

---

## 🔄 The Complaint Lifecycle

Every service request follows a strict, auditable state machine with automated database transactions:

```
    [ User Submits ]
           │
           ▼
        ┌─────┐
        │ NEW │
        └──┬──┘
           │ Admin assigns technician
           ▼
      ┌──────────┐
      │ ASSIGNED │
      └────┬─────┘
           │ Technician begins work
           ▼
    ┌─────────────┐
    │ IN_PROGRESS │
    └──────┬──────┘
           │ Technician completes repair
           ▼
      ┌──────────┐      Citizen confirms & rates
      │ RESOLVED ├──────────────────────────────────┐
      └────┬─────┘                                  │
           │                                        ▼
           │ Admin reopens (if incomplete)     ┌────────┐
           └──────────────────────────────────>│ CLOSED │
                                               └────────┘
```

---

## 🏛️ Architecture & Clean Design

SmartResolve is engineered following the enterprise **MVC (Model-View-Controller)** pattern using vanilla Jakarta EE components, deliberately avoiding heavyweight framework magic to highlight robust low-level engineering fundamentals:

```
[ Web Browser ]
      │  HTTPS / HTTP Requests
      ▼
[ Filter Chain ]
      ├── AuthenticationFilter  (Validates session identity & handles expired logins)
      ├── AuthorizationFilter   (Enforces role-based URL access control matrices)
      └── CsrfFilter            (Synchronizer token pattern against request forgery)
      │
      ▼
[ Controller Layer (Servlets) ]
      ├── Reads and sanitizes HTTP request parameters
      ├── Enforces IDOR (Insecure Direct Object Reference) access boundaries
      └── Invokes Service layer orchestrations
      │
      ▼
[ Service & Business Logic Layer ]
      ├── Lifecycle state-machine validations
      ├── Atomic transaction management (Connection autocommit = false)
      └── Auditing hooks (recording historical transitions in complaint_updates)
      │
      ▼
[ Data Access Layer (DAO) ]
      ├── Parameterized queries using JDBC PreparedStatement
      └── Robust ResultSet-to-Model object mapping
      │
      ▼
[ MySQL Relational Database ]
```

> **Form Submission Integrity:** Implements the **Post/Redirect/Get (PRG)** pattern across all mutating actions (registration, submission, status updates, ratings) to eliminate accidental duplicate submissions from browser reloads.

---

## 🔐 Built-in Security Architecture

| Security Domain | Defense Implementation |
|---|---|
| **Password Storage** | OpenBSD **BCrypt** with adaptive cost factor (12 salt rounds) — immune to rainbow tables. |
| **SQL Injection** | Exclusively parameterized JDBC `PreparedStatement` queries across all DAOs; zero raw string concatenations. |
| **Session Fixation** | Complete session invalidation and token regeneration upon authentication. |
| **Cross-Site Request Forgery (CSRF)** | Secure cryptographic synchronizer token validation on all state-altering requests (`POST`/`PUT`/`DELETE`). |
| **Broken Object Level Auth (IDOR)** | Server-side user identity verification ensures users can inspect only their own complaints. |
| **Cross-Site Scripting (XSS)** | HTML entity encoding with `ValidationUtil` and JSTL `<c:out>` / Expression Language rendering. |
| **Information Disclosure** | Custom container error pages (`404.jsp` & `500.jsp`) prevent stack trace leaks and server fingerprinting. |

---

## 🗄️ Relational Schema & Storage

The database architecture is built on MySQL 8.0+ InnoDB engine for strict referential integrity and transaction safety:

- **`users`** — System principals (User, Admin, Technician) with contact information and password hashes.
- **`categories`** — Master catalogue for categorization (Electrical, Plumbing, HVAC, Carpentry, Network, General).
- **`complaints`** — Core ticket records containing status, priority, location coordinates, technician assignments, and timestamps.
- **`complaint_updates`** — Append-only audit history tracking every status change, updater identity, timestamp, and remark.
- **`feedback`** — Post-resolution evaluations linking a 1-to-5 star rating and feedback comments to complaints.

---

## 🚀 Quickstart & Local Setup

### System Prerequisites
- **Java Development Kit (JDK):** Version 17 or 25+
- **Application Server:** Apache Tomcat 10.1 (Jakarta EE 10 namespace)
- **Database:** MySQL Server 8.0+
- **IDE:** Eclipse IDE for Enterprise Java Developers

---

### 1. Database Initialization
Execute the provided SQL scripts in MySQL Workbench or via terminal:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

### 2. Configure Database Connection
Review your local credentials in `src/main/java/com/smartresolve/util/DBConnection.java`:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/smartresolve_db";
private static final String USER     = "root";
private static final String PASSWORD = "your_mysql_password";
```

### 3. Add Required Libraries to `WEB-INF/lib`
Place the following runtime JARs in `src/main/webapp/WEB-INF/lib/`:
- `mysql-connector-j-8.x.x.jar` (MySQL JDBC Driver)
- `bcrypt-0.10.2.jar` & `bytes-1.5.0.jar` (BCrypt password cryptography)
- `jakarta.servlet.jsp.jstl-3.0.1.jar` & `jakarta.servlet.jsp.jstl-api-3.0.0.jar` (Jakarta JSTL 3.0)

### 4. Run on Apache Tomcat 10.1
1. In Eclipse: **File ➔ Import ➔ Existing Projects into Workspace** and select the `SmartResolve` directory.
2. Right-click the project ➔ **Run As ➔ Run on Server**.
3. Select **Apache Tomcat v10.1** and click **Finish**.
4. Access the web application at:
   ```
   http://localhost:8080/SmartResolve/
   ```

---

## 🧪 Seed Accounts for Immediate Testing

You can sign in immediately using pre-seeded test credentials:

| Role | Email Address | Password | Intended Workflow |
|---|---|---|---|
| **Admin** | `admin@smartresolve.com` | `Admin@123` | Global KPI metrics, technician assignment, priority management |
| **Technician** | `tech1@smartresolve.com` | `Tech@123` | Workbench inspection, progress updates, marking resolved |
| **Resident/User** | `user@smartresolve.com` | `User@123` | Submitting requests, viewing timeline, rating completed work |

---

## 📚 Technical Documentation Index

Detailed guides are provided inside the [`docs/`](docs/) directory:

- 🏛️ [**Architecture & Design Guide**](docs/architecture.md) — Comprehensive explanation of design patterns, MVC layer responsibilities, transaction boundaries, and thread-safety considerations.
- 🗄️ [**Database & Indexing Strategy**](docs/database-design.md) — Complete data dictionary, foreign key cascades, normalization breakdown, and composite index optimization.
- 🚀 [**Deployment & Setup Guide**](docs/setup-guide.md) — Step-by-step installation instructions, classpath troubleshooting, and Tomcat runtime setup.
- 🎓 [**Technical Viva & Interview Guide**](docs/interview-guide.md) — In-depth answers to 30+ technical questions covering Servlet lifecycles, JDBC transactions, connection pooling, and security paradigms.

---

## 📁 Repository Directory Structure

```text
SmartResolve/
├── database/
│   ├── schema.sql           # MySQL DDL schema definition
│   └── seed.sql             # Demo accounts, categories, and initial data
├── docs/
│   ├── architecture.md      # MVC architecture and request flow documentation
│   ├── database-design.md   # Data dictionary and indexing strategy
│   ├── interview-guide.md   # 30+ technical interview & viva questions
│   └── setup-guide.md       # Step-by-step local setup guide
└── src/main/
    ├── java/com/smartresolve/
    │   ├── controller/      # HTTP Servlets (Auth, Complaint, Admin, Technician)
    │   ├── dao/             # Data Access Objects (UserDAO, ComplaintDAO, FeedbackDAO, etc.)
    │   ├── filter/          # Security filters (Authentication, Authorization, CSRF)
    │   ├── model/           # POJO Domain entities (User, Complaint, Feedback, etc.)
    │   ├── service/         # Business logic layer & atomic transactions
    │   └── util/            # Helpers (DBConnection, PasswordUtil, ValidationUtil)
    └── webapp/
        ├── assets/          # Custom stylesheets (style.css, dashboard.css, auth.css)
        └── WEB-INF/
            ├── lib/         # Third-party runtime libraries
            ├── web.xml      # Deployment descriptor & error page mappings
            └── views/       # JSP presentation layer (auth, user, admin, technician, common)
```

---

## 👨‍💻 Author & Engineering Credits

Developed with dedication to clean code, robust design principles, and enterprise Java standards:

**Akash Konakalla**  
Java Full-Stack Developer  
GitHub: [@akashkonakalla](https://github.com/akashkonakalla) • Repository: [SmartResolve](https://github.com/akashkonakalla/SmartResolve-Complaint-Management-System)

---

## 📄 License

This project is licensed for educational, portfolio, and reference purposes. All rights reserved.
