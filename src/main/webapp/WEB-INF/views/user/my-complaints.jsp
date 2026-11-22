<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.smartresolve.model.Complaint" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Complaints — SmartResolve</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8fafc;
        }
        .complaint-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.04);
            transition: transform 0.15s ease, box-shadow 0.15s ease;
        }
        .complaint-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 16px rgba(0,0,0,0.08);
        }
    </style>
</head>
<body>

    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/user/dashboard">
                <i class="bi bi-tools text-primary me-2"></i>SmartResolve
            </a>
            <div class="ms-auto d-flex align-items-center">
                <a href="${pageContext.request.contextPath}/user/complaint/create" class="btn btn-primary btn-sm me-3">
                    <i class="bi bi-plus-circle me-1"></i>New Complaint
                </a>
                <span class="text-white-50 small me-3 d-none d-sm-inline">
                    <i class="bi bi-person-circle me-1"></i><%= session.getAttribute("userName") != null ? session.getAttribute("userName") : "User" %>
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container py-4">

        <!-- Breadcrumb & Header -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 pb-2 border-bottom">
            <div>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-1">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/user/dashboard" class="text-decoration-none">Dashboard</a></li>
                        <li class="breadcrumb-item active" aria-current="page">My Complaints</li>
                    </ol>
                </nav>
                <h3 class="fw-bold mb-0">My Submitted Complaints</h3>
            </div>
            <div class="mt-3 mt-md-0">
                <a href="${pageContext.request.contextPath}/user/complaint/create" class="btn btn-primary">
                    <i class="bi bi-plus-lg me-1"></i>Report New Issue
                </a>
            </div>
        </div>

        <!-- Success Flash Alert -->
        <% if (request.getAttribute("successMessage") != null) { %>
            <div class="alert alert-success alert-dismissible fade show d-flex align-items-center mb-4 shadow-sm" role="alert">
                <i class="bi bi-check-circle-fill me-2 fs-5"></i>
                <div><%= request.getAttribute("successMessage") %></div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        <% } %>

        <!-- Filter Bar -->
        <% String activeFilter = (String) request.getAttribute("selectedStatus"); %>
        <div class="card mb-4 border-0 shadow-sm">
            <div class="card-body p-2 d-flex flex-wrap gap-2">
                <a href="${pageContext.request.contextPath}/user/complaints" 
                   class="btn btn-sm <%= ("ALL".equals(activeFilter) || activeFilter == null) ? "btn-dark" : "btn-outline-secondary" %>">All</a>
                <a href="${pageContext.request.contextPath}/user/complaints?status=NEW" 
                   class="btn btn-sm <%= "NEW".equals(activeFilter) ? "btn-secondary" : "btn-outline-secondary" %>">New</a>
                <a href="${pageContext.request.contextPath}/user/complaints?status=ASSIGNED" 
                   class="btn btn-sm <%= "ASSIGNED".equals(activeFilter) ? "btn-info" : "btn-outline-secondary" %>">Assigned</a>
                <a href="${pageContext.request.contextPath}/user/complaints?status=IN_PROGRESS" 
                   class="btn btn-sm <%= "IN_PROGRESS".equals(activeFilter) ? "btn-warning" : "btn-outline-secondary" %>">In Progress</a>
                <a href="${pageContext.request.contextPath}/user/complaints?status=RESOLVED" 
                   class="btn btn-sm <%= "RESOLVED".equals(activeFilter) ? "btn-success" : "btn-outline-secondary" %>">Resolved</a>
                <a href="${pageContext.request.contextPath}/user/complaints?status=CLOSED" 
                   class="btn btn-sm <%= "CLOSED".equals(activeFilter) ? "btn-dark" : "btn-outline-secondary" %>">Closed</a>
            </div>
        </div>

        <!-- Complaints Listing -->
        <% 
            @SuppressWarnings("unchecked")
            List<Complaint> complaints = (List<Complaint>) request.getAttribute("complaints");
            if (complaints == null || complaints.isEmpty()) { 
        %>
            <!-- Empty State -->
            <div class="card border-0 shadow-sm p-5 text-center my-4">
                <div class="text-muted mb-3">
                    <i class="bi bi-inbox fs-1 text-secondary"></i>
                </div>
                <h5 class="fw-bold">No complaints found</h5>
                <p class="text-muted small mb-4">You have not submitted any complaints matching this filter.</p>
                <div>
                    <a href="${pageContext.request.contextPath}/user/complaint/create" class="btn btn-primary px-4">
                        <i class="bi bi-plus-circle me-1"></i>Submit Your First Complaint
                    </a>
                </div>
            </div>
        <% } else { %>
            <div class="row g-3">
                <% for (Complaint c : complaints) { %>
                    <div class="col-12">
                        <div class="card complaint-card p-3 p-md-4 bg-white">
                            <div class="row align-items-center">
                                <div class="col-md-7 mb-2 mb-md-0">
                                    <div class="d-flex align-items-center gap-2 mb-1 flex-wrap">
                                        <span class="badge bg-light text-dark border fw-bold"><%= c.getComplaintNumber() %></span>
                                        <span class="badge <%= c.getStatusBadgeClass() %>"><%= c.getStatus() %></span>
                                        <span class="<%= c.getPriorityBadgeClass() %>"><%= c.getPriority() %></span>
                                        <span class="text-muted small"><i class="bi bi-tag me-1"></i><%= c.getCategoryName() %></span>
                                    </div>
                                    <h5 class="fw-bold mb-1 text-dark"><%= c.getTitle() %></h5>
                                    <div class="text-muted small">
                                        <i class="bi bi-geo-alt me-1"></i><%= c.getLocation() %>
                                        <span class="mx-2">•</span>
                                        <i class="bi bi-calendar-event me-1"></i><%= c.getCreatedAt() %>
                                    </div>
                                </div>
                                <div class="col-md-3 mb-2 mb-md-0">
                                    <div class="small">
                                        <span class="text-muted">Technician:</span><br>
                                        <strong><%= c.getTechnicianName() != null ? c.getTechnicianName() : "<span class='text-muted fst-italic'>Not assigned yet</span>" %></strong>
                                    </div>
                                </div>
                                <div class="col-md-2 text-md-end">
                                    <a href="${pageContext.request.contextPath}/user/complaint/details?id=<%= c.getId() %>" 
                                       class="btn btn-outline-primary btn-sm px-3">
                                        View Details <i class="bi bi-chevron-right ms-1"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>

    </div>

    <!-- Bootstrap Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
