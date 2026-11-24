# SmartResolve — Setup Guide

## Prerequisites

| Requirement | Version | Notes |
|------------|---------|-------|
| JDK | 25 (or 17+) | Must match Eclipse project facet |
| Eclipse IDE | 2024-09+ | Enterprise Java edition |
| Apache Tomcat | 10.1.x | Must be registered in Eclipse |
| MySQL Server | 8.0+ | Local or remote |
| MySQL Connector/J | 8.x | Place in WEB-INF/lib |

## Step 1 — Clone or Import Project

```bash
git clone https://github.com/akashkonakalla/SmartResolve-Complaint-Management-System.git
```

In Eclipse: **File → Import → Existing Projects into Workspace** → select the cloned folder.

## Step 2 — Add Required JARs

Download and place in `src/main/webapp/WEB-INF/lib/`:

1. **MySQL Connector/J**: https://dev.mysql.com/downloads/connector/j/
   - File: `mysql-connector-j-8.x.x.jar`

2. **BCrypt** (at.favre.lib): https://mvnrepository.com/artifact/at.favre.lib/bcrypt/0.10.2
   - Files: `bcrypt-0.10.2.jar` and `bytes-1.5.0.jar` (dependency)

3. **JSTL 3.0**: https://mvnrepository.com/artifact/org.glassfish.web/jakarta.servlet.jsp.jstl/3.0.1
   - Files: `jakarta.servlet.jsp.jstl-3.0.1.jar` and `jakarta.servlet.jsp.jstl-api-3.0.0.jar`

After copying JARs:
- Right-click project → **Build Path → Configure Build Path**
- Verify JARs appear under **Libraries → Web App Libraries**

## Step 3 — Set Up MySQL Database

Open MySQL Workbench or MySQL command line:

```sql
-- Run schema (creates database, tables, indexes, constraints):
SOURCE /path/to/SmartResolve/database/schema.sql;

-- Run seed data (categories, test accounts):
SOURCE /path/to/SmartResolve/database/seed.sql;
```

## Step 4 — Configure Database Connection

Edit `src/main/java/com/smartresolve/util/DBConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/smartresolve_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String DB_USER = "your_mysql_user";
private static final String DB_PASSWORD = "your_mysql_password";
```

> ⚠️ Do not commit credentials. Rotate them before pushing.

## Step 5 — Run on Tomcat

1. Right-click project → **Run As → Run on Server**
2. Select **Apache Tomcat v10.1**
3. Click **Finish**
4. Access: `http://localhost:8080/SmartResolve/`

## Deployment Assembly Verification

Right-click project → **Properties → Deployment Assembly**

Ensure these mappings exist:
- `src/main/java` → `WEB-INF/classes`
- `src/main/webapp` → `/`
- `WEB-INF/lib` → `WEB-INF/lib`

## Troubleshooting

| Problem | Solution |
|---------|---------|
| ClassNotFoundException: com.mysql.cj.jdbc.Driver | Add MySQL Connector JAR to WEB-INF/lib |
| NoClassDefFoundError: BCrypt | Add bcrypt JAR to WEB-INF/lib |
| 404 on all pages | Check context root is `SmartResolve` |
| Cannot connect to MySQL | Verify host, port, credentials in DBConnection.java |
| JSP tag errors | Add JSTL JARs to WEB-INF/lib |
| Session not persisting | Check browser has cookies enabled |
