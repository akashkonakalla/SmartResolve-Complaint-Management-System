<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.smartresolve.model.Complaint" %>
<%
    @SuppressWarnings("unchecked")
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats");
    int total = (stats != null && stats.get("total") != null) ? stats.get("total") : 0;
    int assigned = (stats != null && stats.get("assigned") != null) ? stats.get("assigned") : 0;
    int inProgress = (stats != null && stats.get("inProgress") != null) ? stats.get("inProgress") : 0;
    int completed = (stats != null && stats.get("completed") != null) ? stats.get("completed") : 0;

    @SuppressWarnings("unchecked")
    List<Complaint> complaints = (List<Complaint>) request.getAttribute("complaints");
    String techName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Technician Dashboard — SmartResolve</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f6f9;
        }
        .tech-header {
            background: linear-gradient(135deg, #065f46 0%, #047857 100%);
            color: #ffffff;
            border-radius: 14px;
            border: none;
        }
        .stat-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            transition: transform 0.15s ease;
        }
        .stat-card:hover {
            transform: translateY(-3px);
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
                <a href="${pageContext.request.contextPath}/technician/complaints" class="btn btn-outline-light btn-sm me-3">
                    <i class="bi bi-list-check me-1"></i>My Assigned Work
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
    <div class="container-fluid px-4 py-4">

        <!-- Welcome Header -->
        <div class="card tech-header p-4 p-md-5 mb-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                <div>
                    <span class="badge bg-light text-success px-3 py-1 rounded-pill fw-semibold mb-2">Field Maintenance Portal</span>
                    <h2 class="fw-bold mb-1">Welcome, <%= techName != null ? techName : "Staff Member" %>!</h2>
                    <p class="mb-0 opacity-90">View your assigned service tasks, update progress status, and report resolutions.</p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/technician/complaints" class="btn btn-light text-success fw-semibold px-4 shadow-sm">
                        <i class="bi bi-clipboard2-check me-1"></i>View Work Queue
                    </a>
                </div>
            </div>
        </div>

        <!-- 4 Metric Cards -->
        <div class="row g-3 mb-4">
            
            <!-- Total Assigned -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white">
                    <span class="text-muted small fw-semibold text-uppercase">Total Assigned</span>
                    <h3 class="fw-bold text-dark mb-0 mt-1"><%= total %></h3>
                </div>
            </div>

            <!-- Pending To Start -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white border-start border-info border-4">
                    <span class="text-muted small fw-semibold text-uppercase">Pending Start</span>
                    <h3 class="fw-bold text-info mb-0 mt-1"><%= assigned %></h3>
                </div>
            </div>

            <!-- In Progress -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white border-start border-warning border-4">
                    <span class="text-muted small fw-semibold text-uppercase">In Progress</span>
                    <h3 class="fw-bold text-warning mb-0 mt-1"><%= inProgress %></h3>
                </div>
            </div>

            <!-- Completed -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white border-start border-success border-4">
                    <span class="text-muted small fw-semibold text-uppercase">Resolved / Completed</span>
                    <h3 class="fw-bold text-success mb-0 mt-1"><%= completed %></h3>
                </div>
            </div>

        </div>

        <!-- Priority Work Queue Section -->
        <div class="card border-0 shadow-sm bg-white p-4">
            <div class="d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom">
                <h5 class="fw-bold mb-0"><i class="bi bi-exclamation-triangle text-warning me-2"></i>Active Work Queue</h5>
                <span class="badge bg-secondary"><%= complaints != null ? complaints.size() : 0 %> Tasks</span>
            </div>

            <% if (complaints == null || complaints.isEmpty()) { %>
                <div class="text-center py-5 text-muted">
                    <i class="bi bi-check2-circle fs-1 text-success d-block mb-2"></i>
                    <h5 class="fw-bold">No tasks currently assigned</h5>
                    <p class="small text-muted mb-0">You are all caught up! New facility assignments will appear here automatically.</p>
                </div>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light small text-uppercase">
                            <tr>
                                <th>Complaint #</th>
                                <th>Category & Location</th>
                                <th>Issue Summary</th>
                                <th>Priority</th>
                                <th>Status</th>
                                <th>Submitted By</th>
                                <th>Assigned Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Complaint c : complaints) { %>
                                <tr>
                                    <td>
                                        <span class="badge bg-light text-dark border fw-bold font-monospace"><%= c.getComplaintNumber() %></span>
                                    </td>
                                    <td>
                                        <span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle small mb-1"><%= c.getCategoryName() %></span>
                                        <div class="small text-muted"><i class="bi bi-geo-alt me-1 text-danger"></i><%= c.getLocation() %></div>
                                    </td>
                                    <td>
                                        <div class="fw-semibold text-dark small"><%= c.getTitle() %></div>
                                        <div class="text-muted small text-truncate" style="max-width: 280px;"><%= c.getDescription() %></div>
                                    </td>
                                    <td><span class="<%= c.getPriorityBadgeClass() %>"><%= c.getPriority() %></span></td>
                                    <td><span class="badge <%= c.getStatusBadgeClass() %>"><%= c.getStatus() %></span></td>
                                    <td class="small">
                                        <div class="fw-semibold"><%= c.getUserName() %></div>
                                        <small class="text-muted"><%= c.getUserEmail() %></small>
                                    </td>
                                    <td class="small text-muted"><%= c.getCreatedAt() %></td>
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
