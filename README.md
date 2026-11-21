# 🛠️ SmartResolve — Smart Complaint & Service Request Management System

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.java.com)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-blue.svg)](https://jakarta.ee)
[![Tomcat](https://img.shields.io/badge/Tomcat-10.1-green.svg)](https://tomcat.apache.org)
[![MySQL](https://img.shields.io/badge/MySQL-8%2B-blue.svg)](https://www.mysql.com)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5-purple.svg)](https://getbootstrap.com)

---

## 📌 Project Overview

**SmartResolve** is a full-stack Java web application for managing service requests and maintenance complaints in colleges, hostels, residential apartments, and offices.

It replaces informal complaint channels (WhatsApp groups, phone calls) with a structured, trackable, role-based complaint management platform.

---

## 🔥 Real-World Problem Statement

In colleges, hostels, and offices, maintenance complaints are communicated informally — through phone calls, WhatsApp messages, or word of mouth. This causes:

- Complaints being forgotten or lost
- No visibility into complaint status
- Duplicate requests reaching maintenance staff
- Inability to prioritize urgent issues
- No audit trail of completed work

**SmartResolve** solves these problems by providing a centralized platform where every complaint has a lifecycle, an owner, and a complete history.

---

## ✨ Features

### Users
- Register and log in securely
- Submit complaints with category, description, location, and optional image
- Track complaint status in real time
- View complete complaint history/timeline
- Confirm resolution and submit feedback

### Administrators
- View all complaints with filter by status, category, priority
- Assign technicians to complaints
- Change complaint priority
- Monitor technician workload
- Reopen complaints when necessary
- Dashboard with complaint statistics

### Technicians
- View assigned complaints
- Update work progress with remarks
- Mark complaints as resolved

---

## 🏗️ Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 25 |
| Servlet API | Jakarta Servlet 6.0 |
| Web Server | Apache Tomcat 10.1 |
| Database | MySQL 8+ |
| DB Connectivity | JDBC with PreparedStatement |
| View Layer | JSP + JSTL 3.0 + EL |
| Frontend | HTML5, CSS3, Bootstrap 5, Bootstrap Icons |
| Password Security | BCrypt (jBCrypt) |
| Architecture | MVC (Model-View-Controller) |
| IDE | Eclipse IDE for Enterprise Java |
| Version Control | Git + GitHub |

---

## 🏛️ Application Architecture

```
Browser
  ↓ HTTP Request
[Filter Layer] AuthenticationFilter + AuthorizationFilter
  ↓
[Controller] Servlet (thin — reads params, calls service)
  ↓
[Service] Business logic (status transitions, auth rules)
  ↓
[DAO] JDBC PreparedStatement queries
  ↓
MySQL Database
  ↑ ResultSet → Model objects
[Controller] Sets request attributes
  ↑
[View] JSP with JSTL/EL (no scriptlets)
  ↑ HTTP Response
Browser
```

Pattern: **Post/Redirect/Get (PRG)** after all form submissions to prevent duplicate submissions on browser refresh.

---

## 🗄️ Database Design

Database: `smartresolve_db`

### Tables
- **users** — Registered users with roles (USER / ADMIN / TECHNICIAN)
- **categories** — Complaint categories (Electrical, Plumbing, Wi-Fi, etc.)
- **complaints** — Core complaint records with lifecycle status
- **complaint_updates** — Full audit trail of every status change
- **feedback** — Post-resolution user satisfaction feedback

### Complaint Lifecycle
```
NEW → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
              ↑___________________________|  (Admin can reopen)
```

See [`database/schema.sql`](database/schema.sql) for the full schema.

---

## 🚀 Installation & Setup

### Prerequisites
- JDK 25 (or JDK 17 — see note below)
- Eclipse IDE for Enterprise Java and Web Developers
- Apache Tomcat 10.1
- MySQL 8+
- Git

### 1. Database Setup
```sql
-- In MySQL Workbench or MySQL CLI:
SOURCE database/schema.sql;
SOURCE database/seed.sql;
```

### 2. Configure Database Connection
Edit `src/main/java/com/smartresolve/util/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/smartresolve_db";
private static final String USER = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";
```

> ⚠️ **Never commit real credentials.** Use environment variables in production.

### 3. Add Required JARs to WEB-INF/lib
Place these JARs in `src/main/webapp/WEB-INF/lib/`:

| JAR | Purpose |
|-----|---------|
| `mysql-connector-j-8.x.x.jar` | MySQL JDBC driver |
| `bcrypt-0.10.2.jar` (at.favre.lib) | Password hashing |
| `jakarta.servlet.jsp.jstl-3.0.1.jar` | JSTL implementation |
| `jakarta.servlet.jsp.jstl-api-3.0.0.jar` | JSTL API |

Download from:
- MySQL Connector/J: https://dev.mysql.com/downloads/connector/j/
- BCrypt: https://mvnrepository.com/artifact/at.favre.lib/bcrypt/0.10.2
- JSTL: https://mvnrepository.com/artifact/org.glassfish.web/jakarta.servlet.jsp.jstl

### 4. Eclipse Project Setup
1. Import the project: **File → Import → Existing Projects into Workspace**
2. Right-click project → **Build Path → Configure Build Path** → verify JARs appear on classpath
3. Right-click project → **Properties → Deployment Assembly** → verify `WEB-INF/lib` is mapped
4. Right-click project → **Run As → Run on Server** → select Tomcat 10.1

### 5. Access the Application
Open: `http://localhost:8080/SmartResolve/`

---

## 🧪 Test Accounts (from seed.sql)

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@smartresolve.com | Admin@123 |
| Technician | tech1@smartresolve.com | Tech@123 |
| User | user@smartresolve.com | User@123 |

---

## 🔐 Security Considerations

1. **BCrypt** — Passwords are hashed with BCrypt (cost factor 12) — never stored in plain text
2. **PreparedStatement** — All SQL uses parameterized queries to prevent SQL injection
3. **Session fixation** — Session is invalidated and recreated on successful login
4. **Role-based access** — Server-side filters enforce role-path restrictions
5. **CSRF protection** — Synchronizer token pattern on all state-changing forms
6. **XSS prevention** — JSTL `<c:out>` and `fn:escapeXml()` escape all user data
7. **File upload safety** — Uploaded files stored outside webroot with type/size validation

---

## 📁 Project Structure

```
SmartResolve/
├── database/           SQL schema and seed data
├── docs/               Architecture, setup, interview guides
└── src/main/
    ├── java/com/smartresolve/
    │   ├── controller/ Servlet classes (HTTP handlers)
    │   ├── model/      Java bean classes (User, Complaint, etc.)
    │   ├── dao/        JDBC database access objects
    │   ├── service/    Business logic layer
    │   ├── filter/     Authentication and authorization filters
    │   └── util/       DBConnection, PasswordUtil, ValidationUtil
    └── webapp/
        ├── assets/     CSS, JS, images
        └── WEB-INF/
            ├── lib/    Runtime JARs
            └── views/  JSP pages (auth, user, admin, technician, common)
```

---

## 🎯 Future Enhancements

- Email notifications on status changes (JavaMail API)
- Push notifications (WebSocket)
- SLA tracking and breach alerts
- PDF report generation
- REST API for mobile client
- OAuth2 SSO integration
- Dashboard charts (Chart.js)

---

## 👨‍💻 Developer

**Akash Konakalla**  
Java Full-Stack Developer  
GitHub: [akashkonakalla](https://github.com/akashkonakalla)

---

## 📄 License

This project is developed for educational and portfolio purposes.
