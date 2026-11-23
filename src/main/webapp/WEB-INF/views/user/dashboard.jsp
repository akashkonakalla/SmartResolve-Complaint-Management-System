<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.smartresolve.model.Complaint" %>
<%
    @SuppressWarnings("unchecked")
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats");
    int total = (stats != null && stats.get("total") != null) ? stats.get("total") : 0;
    int pending = (stats != null && stats.get("pending") != null) ? stats.get("pending") : 0;
    int inProgress = (stats != null && stats.get("inProgress") != null) ? stats.get("inProgress") : 0;
    int resolved = (stats != null && stats.get("resolved") != null) ? stats.get("resolved") : 0;

    @SuppressWarnings("unchecked")
    List<Complaint> recentComplaints = (List<Complaint>) request.getAttribute("recentComplaints");
    String userName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Dashboard — SmartResolve</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f6f9;
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
        .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
        }
        .welcome-card {
            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
            color: #ffffff;
            border-radius: 14px;
            border: none;
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
                <a href="${pageContext.request.contextPath}/user/complaints" class="btn btn-outline-light btn-sm me-3">
                    <i class="bi bi-list-task me-1"></i>My Complaints
                </a>
                <span class="text-white-50 small me-3 d-none d-sm-inline">
                    <i class="bi bi-person-circle me-1"></i><%= userName != null ? userName : "User" %>
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container py-4">

        <!-- Welcome Banner -->
        <div class="card welcome-card p-4 p-md-5 mb-4 shadow">
            <div class="row align-items-center">
                <div class="col-md-8 mb-3 mb-md-0">
                    <span class="badge bg-light text-primary px-3 py-1 rounded-pill fw-semibold mb-2">Resident Portal</span>
                    <h2 class="fw-bold mb-1">Welcome back, <%= userName != null ? userName : "Resident" %>!</h2>
                    <p class="mb-0 opacity-90">Track your maintenance service requests and report new facility issues in real time.</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <a href="${pageContext.request.contextPath}/user/complaint/create" class="btn btn-light btn-lg text-primary fw-semibold shadow-sm">
                        <i class="bi bi-plus-lg me-1"></i>Submit Service Request
                    </a>
                </div>
            </div>
        </div>

        <!-- 4 Metric Cards (Live Real-Time Data) -->
        <div class="row g-3 mb-4">
            
            <!-- Total Complaints -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Total Submitted</span>
                            <h2 class="fw-bold text-dark mb-0 mt-1"><%= total %></h2>
                        </div>
                        <div class="stat-icon bg-primary bg-opacity-10 text-primary">
                            <i class="bi bi-folder2-open"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Pending Review -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Pending Review</span>
                            <h2 class="fw-bold text-secondary mb-0 mt-1"><%= pending %></h2>
                        </div>
                        <div class="stat-icon bg-secondary bg-opacity-10 text-secondary">
                            <i class="bi bi-hourglass-split"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- In Progress -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">In Progress</span>
                            <h2 class="fw-bold text-warning mb-0 mt-1"><%= inProgress %></h2>
                        </div>
                        <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                            <i class="bi bi-tools"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Resolved -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Resolved / Closed</span>
                            <h2 class="fw-bold text-success mb-0 mt-1"><%= resolved %></h2>
                        </div>
                        <div class="stat-icon bg-success bg-opacity-10 text-success">
                            <i class="bi bi-check2-circle"></i>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <!-- Recent Activity Section -->
        <div class="card border-0 shadow-sm bg-white p-4">
            <div class="d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom">
                <h5 class="fw-bold mb-0"><i class="bi bi-clock-history me-2 text-primary"></i>Recent Complaints</h5>
                <a href="${pageContext.request.contextPath}/user/complaints" class="btn btn-outline-primary btn-sm">
                    View All (<%= total %>) <i class="bi bi-chevron-right ms-1"></i>
                </a>
            </div>

            <% if (recentComplaints == null || recentComplaints.isEmpty()) { %>
                <div class="text-center py-5 text-muted">
                    <i class="bi bi-inbox fs-1 d-block mb-2 text-secondary"></i>
                    <p class="mb-3">You have not submitted any service requests yet.</p>
                    <a href="${pageContext.request.contextPath}/user/complaint/create" class="btn btn-primary btn-sm px-3">
                        <i class="bi bi-plus-circle me-1"></i>Submit Your First Issue
                    </a>
                </div>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light small text-uppercase">
                            <tr>
                                <th>Complaint #</th>
                                <th>Category</th>
                                <th>Title</th>
                                <th>Priority</th>
                                <th>Status</th>
                                <th>Date</th>
                                <th class="text-end">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Complaint c : recentComplaints) { %>
                                <tr>
                                    <td><span class="badge bg-light text-dark border fw-bold"><%= c.getComplaintNumber() %></span></td>
                                    <td><span class="text-secondary"><%= c.getCategoryName() %></span></td>
                                    <td class="fw-semibold text-dark"><%= c.getTitle() %></td>
                                    <td><span class="<%= c.getPriorityBadgeClass() %>"><%= c.getPriority() %></span></td>
                                    <td><span class="badge <%= c.getStatusBadgeClass() %>"><%= c.getStatus() %></span></td>
                                    <td class="small text-muted"><%= c.getCreatedAt() %></td>
                                    <td class="text-end">
                                        <a href="${pageContext.request.contextPath}/user/complaint/details?id=<%= c.getId() %>" 
                                           class="btn btn-outline-secondary btn-sm">
                                            Details
                                        </a>
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
