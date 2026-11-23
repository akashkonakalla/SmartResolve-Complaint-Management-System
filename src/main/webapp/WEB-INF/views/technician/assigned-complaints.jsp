<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.smartresolve.model.Complaint" %>
<%
    @SuppressWarnings("unchecked")
    List<Complaint> complaints = (List<Complaint>) request.getAttribute("complaints");
    String techName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Assigned Work Orders — SmartResolve</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f6f9;
        }
        .task-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
    </style>
</head>
<body>

    <!-- Technician Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container-fluid px-4">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/technician/dashboard">
                <i class="bi bi-tools text-warning me-2"></i>SmartResolve <span class="badge bg-success ms-1 small">Technician</span>
            </a>
            <div class="ms-auto d-flex align-items-center">
                <a href="${pageContext.request.contextPath}/technician/dashboard" class="btn btn-outline-light btn-sm me-3">
                    <i class="bi bi-speedometer2 me-1"></i>Dashboard
                </a>
                <span class="text-white-50 small me-3">
                    <i class="bi bi-person-badge me-1"></i><%= techName != null ? techName : "Technician" %>
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
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
            <div>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-1">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/technician/dashboard" class="text-decoration-none">Dashboard</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Assigned Work Orders</li>
                    </ol>
                </nav>
                <h3 class="fw-bold mb-0">Assigned Maintenance Tasks</h3>
            </div>
            <div>
                <span class="badge bg-dark px-3 py-2 fs-6">
                    <%= complaints != null ? complaints.size() : 0 %> Tasks in Queue
                </span>
            </div>
        </div>

        <!-- Task List -->
        <% if (complaints == null || complaints.isEmpty()) { %>
            <div class="card border-0 shadow-sm p-5 text-center my-4 bg-white">
                <div class="text-success mb-3">
                    <i class="bi bi-check-circle fs-1"></i>
                </div>
                <h5 class="fw-bold">No service requests assigned to you</h5>
                <p class="text-muted small mb-0">Check back later or contact your facility administrator.</p>
            </div>
        <% } else { %>
            <div class="row g-3">
                <% for (Complaint c : complaints) { %>
                    <div class="col-12">
                        <div class="card task-card p-4 bg-white">
                            <div class="d-flex justify-content-between align-items-start mb-2 flex-wrap gap-2">
                                <div>
                                    <span class="badge bg-light text-dark border fw-bold font-monospace me-2"><%= c.getComplaintNumber() %></span>
                                    <span class="badge <%= c.getStatusBadgeClass() %> me-2"><%= c.getStatus() %></span>
                                    <span class="<%= c.getPriorityBadgeClass() %> me-2"><%= c.getPriority() %></span>
                                    <span class="text-muted small"><i class="bi bi-tag me-1"></i><%= c.getCategoryName() %></span>
                                </div>
                                <div class="text-muted small">
                                    <i class="bi bi-clock me-1"></i><%= c.getCreatedAt() %>
                                </div>
                            </div>

                            <h5 class="fw-bold mb-2 text-dark"><%= c.getTitle() %></h5>
                            <div class="text-secondary small mb-3">
                                <i class="bi bi-geo-alt-fill text-danger me-1"></i><strong>Location:</strong> <%= c.getLocation() %>
                            </div>

                            <div class="p-3 bg-light rounded-3 text-secondary small mb-3">
                                <%= c.getDescription() %>
                            </div>

                            <div class="d-flex justify-content-between align-items-center pt-2 border-top flex-wrap gap-2">
                                <div class="small text-muted">
                                    <i class="bi bi-person me-1"></i>Reported by: <strong><%= c.getUserName() %></strong> (<%= c.getUserEmail() %>)
                                </div>
                                <div>
                                    <span class="badge bg-secondary-subtle text-secondary border small">Assigned to You</span>
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
