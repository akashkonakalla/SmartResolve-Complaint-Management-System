# SmartResolve — Interview Guide

## Why Jakarta Servlets?

**Question**: Why did you choose Java Servlets for this project?

**Answer**:
Servlets are the foundation of Java web development. Every modern Java web framework
(Spring MVC, Jakarta Faces, etc.) is built on top of the Servlet API.

By building directly with Servlets, I understand exactly what happens when an HTTP request
arrives at a Java web server — how the container maps URLs to Servlet classes, how the
request and response objects work, how sessions are managed, and how the lifecycle of
a Servlet works (`init()`, `service()`, `destroy()`).

This project uses Jakarta Servlet 6.0 (the modern `jakarta.servlet.*` namespace, replacing
the legacy `javax.servlet.*` namespace), which is required by Tomcat 10.1 and Jakarta EE 10.

## What is MVC Architecture?

**Question**: Explain the MVC pattern used in SmartResolve.

**Answer**:
MVC separates an application into three distinct concerns:

- **Model** — Java classes representing data (`User`, `Complaint`, `Category`). These are
  plain POJOs with getters/setters. They carry data between layers.

- **View** — JSP pages that render HTML. They receive data from the Controller via
  request attributes and display it using JSTL and Expression Language. No SQL, no business
  logic inside JSP.

- **Controller** — Servlets that receive HTTP requests, read parameters, call the service
  layer, set attributes on the request, and forward to the appropriate JSP view.

The benefit is separation of concerns: a database schema change only touches the DAO layer;
a UI redesign only touches JSP files; a new business rule only touches the service layer.

## How Does JDBC Work?

**Question**: Walk me through how JDBC is used in SmartResolve.

**Answer**:
JDBC (Java Database Connectivity) is the standard Java API for connecting to relational
databases. Here is the sequence used in every DAO method:

```java
// 1. Get a connection from the pool / factory
try (Connection conn = DBConnection.getConnection();
     // 2. Prepare a parameterized statement (prevents SQL injection)
     PreparedStatement ps = conn.prepareStatement(
         "SELECT * FROM users WHERE email = ?")) {

    // 3. Bind parameters (1-indexed)
    ps.setString(1, email);

    // 4. Execute the query
    try (ResultSet rs = ps.executeQuery()) {
        // 5. Iterate and map to model
        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    }
} // 6. try-with-resources auto-closes Connection, PreparedStatement, ResultSet
```

The `try-with-resources` pattern ensures JDBC resources are always closed, even on exception,
preventing connection leaks.

## How Does Session Management Work?

**Question**: How does SmartResolve manage user sessions?

**Answer**:
After successful login, three things happen:

1. **Session fixation protection**: The old session (created before login) is invalidated with
   `session.invalidate()`. A new session is then created.

2. **Session population**: The new session stores the user's ID and role:
   ```java
   HttpSession newSession = request.getSession(true);
   newSession.setAttribute("userId", user.getId());
   newSession.setAttribute("userRole", user.getRole());
   ```

3. **Session timeout**: Configured to 30 minutes in `web.xml`.

For every subsequent request, `AuthenticationFilter` reads the session:
```java
HttpSession session = request.getSession(false); // don't create a new one
if (session == null || session.getAttribute("userId") == null) {
    response.sendRedirect("/SmartResolve/login");
    return;
}
```

On logout, `session.invalidate()` destroys the session and all its attributes.

## How Is Role-Based Access Enforced?

**Question**: How do you prevent a regular user from accessing admin pages?

**Answer**:
Two Jakarta Filters protect every request:

**AuthenticationFilter** — checks if the user is logged in at all.

**AuthorizationFilter** — checks if the logged-in user's role matches the URL they're trying to access:
```java
String role = (String) session.getAttribute("userRole");
String path = request.getRequestURI();

if (path.contains("/admin/") && !"ADMIN".equals(role)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    return;
}
```

Critically, the role is **never read from the request**. It is only read from the server-side session,
which the user cannot modify.

## How Is SQL Injection Prevented?

**Question**: How do you prevent SQL injection in SmartResolve?

**Answer**:
Every single SQL query in the DAO layer uses `PreparedStatement` with parameter placeholders (`?`).

The JDBC driver ensures parameters are properly escaped before they are sent to MySQL.
This makes it impossible for malicious input like `' OR '1'='1` to alter the SQL structure.

We **never** do string concatenation to build SQL:
```java
// WRONG — vulnerable to SQL injection:
String sql = "SELECT * FROM users WHERE email = '" + email + "'";

// CORRECT — safe PreparedStatement:
PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
ps.setString(1, email);
```

## How Does Complaint Assignment Work?

**Question**: Walk me through the complaint assignment workflow.

**Answer**:
1. Admin views the complaints list and selects a complaint
2. Admin chooses a technician from a dropdown (populated from DB - only users with TECHNICIAN role)
3. Admin submits the assignment form via POST to `AdminServlet`
4. `AdminServlet` calls `ComplaintService.assignTechnician(complaintId, technicianId, adminId)`
5. `ComplaintService` validates the transition (complaint must be NEW, technician must have role TECHNICIAN)
6. `ComplaintDAO.assignTechnician()` runs two SQL operations inside a transaction:
   - `UPDATE complaints SET assigned_to=?, status='ASSIGNED' WHERE id=?`
   - `INSERT INTO complaint_updates (complaint_id, updated_by, old_status, new_status, remarks) VALUES (...)`
7. If either operation fails, the transaction is rolled back — no partial state
8. On success, Admin is redirected back to the complaints list (PRG pattern)

## How Are Database Transactions Used?

**Question**: When do you use database transactions and why?

**Answer**:
Transactions are used whenever two or more SQL operations must succeed or fail together.

In SmartResolve, every complaint status change must:
1. Update the status in the `complaints` table
2. Record the change in `complaint_updates`

If only step 1 succeeds and step 2 fails, the audit trail is broken — we'd have no record of
who changed the status and when.

The transaction pattern used:
```java
connection.setAutoCommit(false);
try {
    updateComplaintStatus(connection, ...);    // Step 1
    insertComplaintUpdate(connection, ...);   // Step 2
    connection.commit();                      // Both succeeded
} catch (SQLException e) {
    connection.rollback();                    // Either failed — undo everything
    throw e;
}
```

## What Challenges Were Encountered?

**Question**: What was the most challenging part of this project?

**Answer**:
1. **Jakarta namespace migration** — Tomcat 10.1 uses `jakarta.servlet.*` instead of
   `javax.servlet.*`. All imports had to use the Jakarta namespace.

2. **Session fixation** — Understanding that the session ID must be rotated after login to
   prevent session fixation attacks was a non-obvious security detail.

3. **CSRF protection** — Implementing the synchronizer token pattern required generating
   a unique token per session, embedding it in every form, and validating it on every POST.

4. **Database transactions** — Managing `Connection.setAutoCommit(false)` correctly with
   proper rollback in catch blocks and always restoring `autoCommit(true)` or closing the
   connection in finally blocks.

5. **Filter ordering** — Understanding that filter chain order matters: authentication must
   be checked before authorization.

## What Could Be Improved?

**Question**: What would you improve if you had more time?

**Answer**:
1. **Connection pooling** — Use HikariCP instead of creating a new JDBC connection per request
2. **Email notifications** — Use JavaMail API to notify users on status changes
3. **REST API** — Expose a JSON API so the system could support a mobile app
4. **Dashboard charts** — Add Chart.js visualizations for complaint statistics
5. **SLA tracking** — Track resolution time and alert on SLA breaches
6. **Full-text search** — MySQL FULLTEXT index on complaint title and description
7. **Pagination** — Large complaint lists need server-side pagination
8. **Unit tests** — JUnit 5 tests for service and DAO layers
