<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.smartresolve.model.Complaint" %>
<%
    Complaint c = (Complaint) request.getAttribute("complaint");
    if (c == null) {
        response.sendRedirect(request.getContextPath() + "/user/complaints");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Complaint Details — <%= c.getComplaintNumber() %></title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8fafc;
        }
        .detail-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.05);
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
                <a href="${pageContext.request.contextPath}/user/complaints" class="btn btn-outline-light btn-sm me-3">
                    <i class="bi bi-list-task me-1"></i>My Complaints
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container py-4">

        <!-- Breadcrumb Navigation -->
        <nav aria-label="breadcrumb" class="mb-3">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/user/dashboard" class="text-decoration-none">Dashboard</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/user/complaints" class="text-decoration-none">My Complaints</a></li>
                <li class="breadcrumb-item active" aria-current="page"><%= c.getComplaintNumber() %></li>
            </ol>
        </nav>

        <div class="row g-4">
            
            <!-- Left Column: Complaint Overview -->
            <div class="col-lg-8">
                <div class="card detail-card p-4 bg-white mb-4">
                    
                    <div class="d-flex justify-content-between align-items-start mb-3 flex-wrap gap-2">
                        <div>
                            <span class="badge bg-light text-dark border fs-6 fw-bold mb-2"><%= c.getComplaintNumber() %></span>
                            <h3 class="fw-bold mb-1 text-dark"><%= c.getTitle() %></h3>
                            <div class="text-muted small">
                                <span><i class="bi bi-calendar3 me-1"></i>Reported on <%= c.getCreatedAt() %></span>
                                <span class="mx-2">•</span>
                                <span><i class="bi bi-tag me-1"></i>Category: <strong><%= c.getCategoryName() %></strong></span>
                            </div>
                        </div>
                        <div class="text-end">
                            <span class="badge <%= c.getStatusBadgeClass() %> fs-6 px-3 py-2"><%= c.getStatus() %></span>
                            <div class="mt-2">
                                <span class="<%= c.getPriorityBadgeClass() %> px-2 py-1">Priority: <%= c.getPriority() %></span>
                            </div>
                        </div>
                    </div>

                    <hr class="my-3">

                    <!-- Location -->
                    <div class="mb-4">
                        <h6 class="fw-bold text-secondary text-uppercase small"><i class="bi bi-geo-alt me-1 text-danger"></i>Location</h6>
                        <p class="mb-0 fs-6"><%= c.getLocation() %></p>
                    </div>

                    <!-- Description -->
                    <div class="mb-4">
                        <h6 class="fw-bold text-secondary text-uppercase small"><i class="bi bi-file-text me-1 text-primary"></i>Problem Description</h6>
                        <div class="p-3 bg-light rounded-3 text-secondary lh-base">
                            <%= c.getDescription() %>
                        </div>
                    </div>

                    <!-- Photo Attachment (if present) -->
                    <% if (c.getImagePath() != null && !c.getImagePath().trim().isEmpty()) { %>
                        <div class="mb-3">
                            <h6 class="fw-bold text-secondary text-uppercase small"><i class="bi bi-image me-1 text-success"></i>Attached Photo</h6>
                            <div class="border rounded p-2 d-inline-block">
                                <img src="${pageContext.request.contextPath}/<%= c.getImagePath() %>" alt="Complaint Photo" class="img-fluid rounded" style="max-height: 250px;">
                            </div>
                        </div>
                    <% } %>

                    <div class="pt-3 border-top d-flex justify-content-between align-items-center">
                        <a href="${pageContext.request.contextPath}/user/complaints" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i>Back to Complaints
                        </a>
                        <% if (c.isResolved()) { %>
                            <span class="text-success fw-semibold"><i class="bi bi-check-circle-fill me-1"></i>Work Marked Resolved</span>
                        <% } %>
                    </div>
                </div>
            </div>

            <!-- Right Column: Status & Assignment Summary -->
            <div class="col-lg-4">
                
                <!-- Technician Card -->
                <div class="card detail-card p-4 bg-white mb-4">
                    <h6 class="fw-bold text-secondary text-uppercase small mb-3">
                        <i class="bi bi-person-badge me-1 text-primary"></i>Assigned Technician
                    </h6>
                    <% if (c.getAssignedTo() != null && c.getTechnicianName() != null) { %>
                        <div class="d-flex align-items-center">
                            <div class="bg-primary bg-opacity-10 text-primary rounded-circle p-3 me-3">
                                <i class="bi bi-tools fs-4"></i>
                            </div>
                            <div>
                                <h6 class="fw-bold mb-0"><%= c.getTechnicianName() %></h6>
                                <span class="badge bg-success-subtle text-success border border-success-subtle small mt-1">Assigned</span>
                            </div>
                        </div>
                    <% } else { %>
                        <div class="alert alert-light border mb-0 text-muted small">
                            <i class="bi bi-clock me-1"></i>Technician assignment pending by administrator.
                        </div>
                    <% } %>
                </div>

                <!-- Timestamps Card -->
                <div class="card detail-card p-4 bg-white">
                    <h6 class="fw-bold text-secondary text-uppercase small mb-3">
                        <i class="bi bi-clock-history me-1 text-info"></i>Audit Details
                    </h6>
                    <ul class="list-unstyled mb-0 small text-secondary">
                        <li class="mb-2"><strong>Created:</strong> <%= c.getCreatedAt() %></li>
                        <li class="mb-2"><strong>Last Updated:</strong> <%= c.getUpdatedAt() %></li>
                        <% if (c.getResolvedAt() != null) { %>
                            <li class="text-success"><strong>Resolved At:</strong> <%= c.getResolvedAt() %></li>
                        <% } %>
                    </ul>
                </div>

            </div>

        </div>

    </div>

    <!-- Bootstrap Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
