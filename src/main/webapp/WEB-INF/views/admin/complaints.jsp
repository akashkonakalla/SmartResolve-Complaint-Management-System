<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.smartresolve.model.Complaint" %>
<%@ page import="com.smartresolve.model.Category" %>
<%@ page import="com.smartresolve.model.User" %>
<%
    @SuppressWarnings("unchecked")
    List<Complaint> complaints = (List<Complaint>) request.getAttribute("complaints");

    @SuppressWarnings("unchecked")
    List<Category> categories = (List<Category>) request.getAttribute("categories");

    @SuppressWarnings("unchecked")
    List<User> technicians = (List<User>) request.getAttribute("technicians");

    String keyword = (String) request.getAttribute("keyword");
    String selectedStatus = (String) request.getAttribute("selectedStatus");
    Integer selectedCategoryId = (Integer) request.getAttribute("selectedCategoryId");
    String selectedPriority = (String) request.getAttribute("selectedPriority");

    String adminName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Complaints — SmartResolve Admin</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f6f9;
        }
        .filter-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
        .complaints-table th {
            font-size: 0.8rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
    </style>
</head>
<body>

    <!-- Admin Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container-fluid px-4">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="bi bi-shield-lock text-primary me-2"></i>SmartResolve <span class="badge bg-danger ms-1 small">Admin</span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="adminNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i>Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active fw-semibold" href="${pageContext.request.contextPath}/admin/complaints">
                            <i class="bi bi-card-checklist me-1"></i>All Complaints
                        </a>
                    </li>
                </ul>
                <div class="d-flex align-items-center">
                    <span class="text-white-50 small me-3">
                        <i class="bi bi-person-fill-gear me-1"></i><%= adminName != null ? adminName : "Admin" %>
                    </span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-box-arrow-right me-1"></i>Logout
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container-fluid px-4 py-4">

        <!-- Breadcrumb & Header -->
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
            <div>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-1">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard" class="text-decoration-none">Dashboard</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Complaints Directory</li>
                    </ol>
                </nav>
                <h3 class="fw-bold mb-0">Complaint Management & Dispatch</h3>
            </div>
            <div>
                <span class="badge bg-secondary px-3 py-2 fs-6">
                    <%= complaints != null ? complaints.size() : 0 %> Complaints Filtered
                </span>
            </div>
        </div>

        <!-- Filter & Search Toolbar Card -->
        <div class="card filter-card p-4 bg-white mb-4">
            <form action="${pageContext.request.contextPath}/admin/complaints" method="GET" class="row g-3 align-items-end">
                
                <!-- Keyword Search -->
                <div class="col-md-3">
                    <label for="keyword" class="form-label small fw-semibold text-secondary">Search Keyword</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-search"></i></span>
                        <input type="text" class="form-control" id="keyword" name="keyword" 
                               placeholder="ID, Title, Submitter, Location..." 
                               value="<%= keyword != null ? keyword : "" %>">
                    </div>
                </div>

                <!-- Status Filter -->
                <div class="col-md-2">
                    <label for="status" class="form-label small fw-semibold text-secondary">Status</label>
                    <select class="form-select" id="status" name="status">
                        <option value="ALL" <%= ("ALL".equals(selectedStatus) || selectedStatus == null) ? "selected" : "" %>>All Statuses</option>
                        <option value="NEW" <%= "NEW".equals(selectedStatus) ? "selected" : "" %>>NEW (Unassigned)</option>
                        <option value="ASSIGNED" <%= "ASSIGNED".equals(selectedStatus) ? "selected" : "" %>>ASSIGNED</option>
                        <option value="IN_PROGRESS" <%= "IN_PROGRESS".equals(selectedStatus) ? "selected" : "" %>>IN_PROGRESS</option>
                        <option value="RESOLVED" <%= "RESOLVED".equals(selectedStatus) ? "selected" : "" %>>RESOLVED</option>
                        <option value="CLOSED" <%= "CLOSED".equals(selectedStatus) ? "selected" : "" %>>CLOSED</option>
                    </select>
                </div>

                <!-- Category Filter -->
                <div class="col-md-3">
                    <label for="categoryId" class="form-label small fw-semibold text-secondary">Category</label>
                    <select class="form-select" id="categoryId" name="categoryId">
                        <option value="" <%= (selectedCategoryId == null || selectedCategoryId == 0) ? "selected" : "" %>>All Categories</option>
                        <% if (categories != null) { 
                            for (Category cat : categories) { 
                                boolean isSel = (selectedCategoryId != null && selectedCategoryId.equals(cat.getId()));
                        %>
                            <option value="<%= cat.getId() %>" <%= isSel ? "selected" : "" %>><%= cat.getName() %></option>
                        <% } } %>
                    </select>
                </div>

                <!-- Priority Filter -->
                <div class="col-md-2">
                    <label for="priority" class="form-label small fw-semibold text-secondary">Priority</label>
                    <select class="form-select" id="priority" name="priority">
                        <option value="ALL" <%= ("ALL".equals(selectedPriority) || selectedPriority == null) ? "selected" : "" %>>All Priorities</option>
                        <option value="URGENT" <%= "URGENT".equals(selectedPriority) ? "selected" : "" %>>Urgent</option>
                        <option value="HIGH" <%= "HIGH".equals(selectedPriority) ? "selected" : "" %>>High</option>
                        <option value="MEDIUM" <%= "MEDIUM".equals(selectedPriority) ? "selected" : "" %>>Medium</option>
                        <option value="LOW" <%= "LOW".equals(selectedPriority) ? "selected" : "" %>>Low</option>
                    </select>
                </div>

                <!-- Action Buttons -->
                <div class="col-md-2 d-flex gap-2">
                    <button type="submit" class="btn btn-primary w-100 fw-semibold">
                        <i class="bi bi-funnel me-1"></i>Filter
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-outline-secondary" title="Reset Filters">
                        <i class="bi bi-arrow-counterclockwise"></i>
                    </a>
                </div>
            </form>
        </div>

        <!-- Complaints Listing Table -->
        <div class="card border-0 shadow-sm bg-white p-4">
            <% if (complaints == null || complaints.isEmpty()) { %>
                <div class="text-center py-5 text-muted">
                    <i class="bi bi-search fs-1 d-block mb-3 text-secondary"></i>
                    <h5 class="fw-bold">No complaints match your filter criteria</h5>
                    <p class="small text-muted mb-3">Try adjusting your keywords, status, or category parameters.</p>
                    <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-outline-primary btn-sm px-3">
                        Reset All Filters
                    </a>
                </div>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle complaints-table mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Tracking #</th>
                                <th>Resident</th>
                                <th>Category & Location</th>
                                <th>Title</th>
                                <th>Priority</th>
                                <th>Status</th>
                                <th>Technician</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Complaint c : complaints) { %>
                                <tr>
                                    <td>
                                        <span class="badge bg-light text-dark border fw-bold font-monospace"><%= c.getComplaintNumber() %></span>
                                    </td>
                                    <td>
                                        <div class="fw-semibold text-dark small"><%= c.getUserName() %></div>
                                        <div class="text-muted small"><%= c.getUserEmail() %></div>
                                    </td>
                                    <td>
                                        <span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle small mb-1"><%= c.getCategoryName() %></span>
                                        <div class="small text-muted"><i class="bi bi-geo-alt me-1"></i><%= c.getLocation() %></div>
                                    </td>
                                    <td>
                                        <div class="fw-semibold text-dark small"><%= c.getTitle() %></div>
                                        <div class="text-muted small text-truncate" style="max-width: 250px;"><%= c.getDescription() %></div>
                                    </td>
                                    <td>
                                        <span class="<%= c.getPriorityBadgeClass() %>"><%= c.getPriority() %></span>
                                    </td>
                                    <td>
                                        <span class="badge <%= c.getStatusBadgeClass() %>"><%= c.getStatus() %></span>
                                    </td>
                                    <td>
                                        <% if (c.getTechnicianName() != null) { %>
                                            <div class="small fw-semibold text-primary"><i class="bi bi-person-check me-1"></i><%= c.getTechnicianName() %></div>
                                        <% } else { %>
                                            <span class="badge bg-danger-subtle text-danger border border-danger-subtle small">Unassigned</span>
                                        <% } %>
                                    </td>
                                    <td class="small text-muted">
                                        <%= c.getCreatedAt() %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>

    </div>

    <!-- Bootstrap Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
