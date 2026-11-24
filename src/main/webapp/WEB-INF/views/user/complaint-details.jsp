<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.smartresolve.model.Complaint" %>
<%@ page import="com.smartresolve.model.ComplaintUpdate" %>
<%@ page import="com.smartresolve.model.Feedback" %>
<%
    Complaint c = (Complaint) request.getAttribute("complaint");
    if (c == null) {
        response.sendRedirect(request.getContextPath() + "/user/complaints");
        return;
    }
    @SuppressWarnings("unchecked")
    List<ComplaintUpdate> updates = (List<ComplaintUpdate>) request.getAttribute("updates");
    Feedback fb = (Feedback) request.getAttribute("feedback");
    String successMsg = (String) request.getAttribute("successMessage");
    String errorParam = request.getParameter("error");
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
        .timeline {
            position: relative;
            padding-left: 30px;
        }
        .timeline::before {
            content: '';
            position: absolute;
            left: 11px;
            top: 5px;
            bottom: 5px;
            width: 2px;
            background: #e2e8f0;
        }
        .timeline-item {
            position: relative;
            margin-bottom: 24px;
        }
        .timeline-marker {
            position: absolute;
            left: -30px;
            top: 2px;
            width: 24px;
            height: 24px;
            border-radius: 50%;
            background: #fff;
            border: 2px solid #3b82f6;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
        }
        .rating-option {
            cursor: pointer;
            border: 2px solid #e2e8f0;
            border-radius: 8px;
            padding: 8px 14px;
            transition: all 0.2s ease;
        }
        .rating-option:hover {
            border-color: #f59e0b;
            background-color: #fffbeb;
        }
        .form-check-input:checked + .rating-label {
            font-weight: bold;
            color: #b45309;
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

        <% if (successMsg != null) { %>
            <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i><%= successMsg %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <% if (errorParam != null && !errorParam.trim().isEmpty()) { %>
            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i><%= errorParam %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <div class="row g-4">
            
            <!-- Left Column: Complaint Overview & Audit Timeline & Feedback -->
            <div class="col-lg-8">
                
                <!-- Main Overview Card -->
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
                        <% } else if (c.isClosed()) { %>
                            <span class="text-secondary fw-semibold"><i class="bi bi-archive-fill me-1"></i>Complaint Closed</span>
                        <% } %>
                    </div>
                </div>

                <!-- Post-Resolution Feedback Section -->
                <% if (c.isResolved() && fb == null) { %>
                    <!-- User Feedback Form Card -->
                    <div class="card detail-card p-4 bg-white mb-4 border-start border-4 border-warning shadow-sm">
                        <div class="d-flex align-items-center mb-2">
                            <i class="bi bi-star-fill text-warning fs-3 me-2"></i>
                            <div>
                                <h5 class="fw-bold mb-0">Confirm Resolution & Rate Service</h5>
                                <small class="text-muted">The assigned technician marked this issue resolved. Please rate your experience to close the ticket.</small>
                            </div>
                        </div>
                        <hr class="my-3">
                        <form action="${pageContext.request.contextPath}/user/complaint/feedback" method="POST">
                            <input type="hidden" name="complaintId" value="<%= c.getId() %>">

                            <div class="mb-3">
                                <label class="form-label fw-semibold">How would you rate the resolution quality? <span class="text-danger">*</span></label>
                                <div class="row g-2">
                                    <div class="col-md-auto">
                                        <label class="rating-option d-flex align-items-center">
                                            <input type="radio" name="rating" value="5" class="form-check-input me-2" checked>
                                            <span class="rating-label">⭐⭐⭐⭐⭐ 5 - Excellent</span>
                                        </label>
                                    </div>
                                    <div class="col-md-auto">
                                        <label class="rating-option d-flex align-items-center">
                                            <input type="radio" name="rating" value="4" class="form-check-input me-2">
                                            <span class="rating-label">⭐⭐⭐⭐ 4 - Good</span>
                                        </label>
                                    </div>
                                    <div class="col-md-auto">
                                        <label class="rating-option d-flex align-items-center">
                                            <input type="radio" name="rating" value="3" class="form-check-input me-2">
                                            <span class="rating-label">⭐⭐⭐ 3 - Average</span>
                                        </label>
                                    </div>
                                    <div class="col-md-auto">
                                        <label class="rating-option d-flex align-items-center">
                                            <input type="radio" name="rating" value="2" class="form-check-input me-2">
                                            <span class="rating-label">⭐⭐ 2 - Fair</span>
                                        </label>
                                    </div>
                                    <div class="col-md-auto">
                                        <label class="rating-option d-flex align-items-center">
                                            <input type="radio" name="rating" value="1" class="form-check-input me-2">
                                            <span class="rating-label">⭐ 1 - Poor</span>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="comments" class="form-label fw-semibold">Feedback / Comments (Optional)</label>
                                <textarea class="form-control" id="comments" name="comments" rows="3" placeholder="Share your experience or remarks regarding the resolution..."></textarea>
                            </div>

                            <button type="submit" class="btn btn-warning text-dark fw-bold px-4">
                                <i class="bi bi-check2-circle me-1"></i>Submit Feedback & Close Ticket
                            </button>
                        </form>
                    </div>
                <% } %>

                <!-- Completed Feedback Review Card (if already submitted) -->
                <% if (fb != null) { %>
                    <div class="card detail-card p-4 bg-white mb-4 border-start border-4 border-success shadow-sm">
                        <div class="d-flex justify-content-between align-items-center mb-2 flex-wrap">
                            <h5 class="fw-bold mb-0 text-success">
                                <i class="bi bi-chat-heart-fill me-2"></i>Citizen Feedback & Rating
                            </h5>
                            <span class="badge bg-success-subtle text-success border border-success-subtle">
                                <i class="bi bi-check-circle me-1"></i>Verified Feedback
                            </span>
                        </div>
                        <div class="d-flex align-items-center my-3">
                            <div class="fs-4 text-warning me-3">
                                <% for (int s = 1; s <= 5; s++) { %>
                                    <i class="bi <%= s <= fb.getRating() ? "bi-star-fill text-warning" : "bi-star text-muted" %>"></i>
                                <% } %>
                            </div>
                            <span class="badge bg-dark fs-6"><%= fb.getRating() %> / 5 — <%= fb.getRatingLabel() %></span>
                        </div>
                        <% if (fb.getComments() != null && !fb.getComments().trim().isEmpty()) { %>
                            <div class="p-3 bg-light rounded-3 text-secondary fst-italic mb-2">
                                "<%= fb.getComments() %>"
                            </div>
                        <% } %>
                        <div class="text-muted small">
                            <i class="bi bi-clock me-1"></i>Submitted on <%= fb.getCreatedAt() %>
                        </div>
                    </div>
                <% } %>

                <!-- Complaint Status History Timeline Card -->
                <div class="card detail-card p-4 bg-white">
                    <h5 class="fw-bold mb-4"><i class="bi bi-clock-history text-primary me-2"></i>Status History & Timeline</h5>
                    
                    <% if (updates == null || updates.isEmpty()) { %>
                        <p class="text-muted small mb-0">No updates recorded yet.</p>
                    <% } else { %>
                        <div class="timeline">
                            <% for (ComplaintUpdate u : updates) { %>
                                <div class="timeline-item">
                                    <div class="timeline-marker">
                                        <i class="bi <%= u.getStatusIcon() %>"></i>
                                    </div>
                                    <div class="bg-light p-3 rounded-3">
                                        <div class="d-flex justify-content-between align-items-center mb-1 flex-wrap">
                                            <span class="badge <%= u.getStatusBadgeClass() %>"><%= u.getNewStatus() %></span>
                                            <small class="text-muted"><%= u.getCreatedAt() %></small>
                                        </div>
                                        <div class="small fw-semibold text-dark mt-1">
                                            Updated by: <%= u.getUpdatedByName() %> (<span class="text-muted"><%= u.getUpdatedByRole() %></span>)
                                        </div>
                                        <% if (u.getRemarks() != null && !u.getRemarks().trim().isEmpty()) { %>
                                            <div class="text-secondary small mt-2 fst-italic">
                                                "<%= u.getRemarks() %>"
                                            </div>
                                        <% } %>
                                    </div>
                                </div>
                            <% } %>
                        </div>
                    <% } %>
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
                        <i class="bi bi-info-circle me-1 text-info"></i>Audit Summary
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
