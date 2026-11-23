<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.smartresolve.model.Complaint" %>
<%@ page import="com.smartresolve.model.User" %>
<%
    @SuppressWarnings("unchecked")
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats");
    int total = (stats != null && stats.get("total") != null) ? stats.get("total") : 0;
    int newC = (stats != null && stats.get("newComplaints") != null) ? stats.get("newComplaints") : 0;
    int assigned = (stats != null && stats.get("assigned") != null) ? stats.get("assigned") : 0;
    int inProgress = (stats != null && stats.get("inProgress") != null) ? stats.get("inProgress") : 0;
    int resolved = (stats != null && stats.get("resolved") != null) ? stats.get("resolved") : 0;
    int closed = (stats != null && stats.get("closed") != null) ? stats.get("closed") : 0;

    @SuppressWarnings("unchecked")
    List<Complaint> recentComplaints = (List<Complaint>) request.getAttribute("recentComplaints");

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> workloads = (List<Map<String, Object>>) request.getAttribute("workloads");

    String adminName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administrator Dashboard — SmartResolve</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f6f9;
        }
        .admin-header {
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
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
        .stat-icon {
            width: 48px;
            height: 48px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.4rem;
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
                        <a class="nav-link active fw-semibold" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i>Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/complaints">
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

        <!-- Top Welcome Header -->
        <div class="card admin-header p-4 mb-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                <div>
                    <span class="badge bg-danger px-3 py-1 rounded-pill fw-semibold mb-2">Central Operations</span>
                    <h3 class="fw-bold mb-1">Facility Operations & Complaint Oversight</h3>
                    <p class="mb-0 text-white-50 small">Manage assignments, track technician workloads, and resolve campus service disruptions.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-primary btn-sm px-3">
                        <i class="bi bi-sliders me-1"></i>Manage Complaints
                    </a>
                </div>
            </div>
        </div>

        <!-- System-wide 5 Metric Cards -->
        <div class="row g-3 mb-4">
            
            <!-- Total Complaints -->
            <div class="col-sm-6 col-lg-2">
                <div class="card stat-card p-3 bg-white">
                    <span class="text-muted small fw-semibold text-uppercase">Total Issues</span>
                    <h3 class="fw-bold text-dark mb-0 mt-1"><%= total %></h3>
                </div>
            </div>

            <!-- New (Unassigned) -->
            <div class="col-sm-6 col-lg-2">
                <div class="card stat-card p-3 bg-white border-start border-danger border-4">
                    <span class="text-muted small fw-semibold text-uppercase">New / Unassigned</span>
                    <h3 class="fw-bold text-danger mb-0 mt-1"><%= newC %></h3>
                </div>
            </div>

            <!-- Assigned -->
            <div class="col-sm-6 col-lg-2">
                <div class="card stat-card p-3 bg-white border-start border-info border-4">
                    <span class="text-muted small fw-semibold text-uppercase">Assigned</span>
                    <h3 class="fw-bold text-info mb-0 mt-1"><%= assigned %></h3>
                </div>
            </div>

            <!-- In Progress -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white border-start border-warning border-4">
                    <span class="text-muted small fw-semibold text-uppercase">Work In Progress</span>
                    <h3 class="fw-bold text-warning mb-0 mt-1"><%= inProgress %></h3>
                </div>
            </div>

            <!-- Resolved & Closed -->
            <div class="col-sm-6 col-lg-3">
                <div class="card stat-card p-3 bg-white border-start border-success border-4">
                    <span class="text-muted small fw-semibold text-uppercase">Resolved / Closed</span>
                    <h3 class="fw-bold text-success mb-0 mt-1"><%= resolved + closed %></h3>
                </div>
            </div>

        </div>

        <div class="row g-4">
            
            <!-- Left: Recent System Complaints Table -->
            <div class="col-lg-8">
                <div class="card border-0 shadow-sm bg-white p-4 h-100">
                    <div class="d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom">
                        <h5 class="fw-bold mb-0"><i class="bi bi-clock-history me-2 text-primary"></i>Recent System Activity</h5>
                        <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-outline-primary btn-sm">
                            View All <i class="bi bi-chevron-right ms-1"></i>
                        </a>
                    </div>

                    <% if (recentComplaints == null || recentComplaints.isEmpty()) { %>
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-inbox fs-1 d-block mb-2"></i>
                            <p class="mb-0">No complaints reported yet in the database.</p>
                        </div>
                    <% } else { %>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light small text-uppercase">
                                    <tr>
                                        <th>Number</th>
                                        <th>Submitter</th>
                                        <th>Category</th>
                                        <th>Title</th>
                                        <th>Technician</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (Complaint c : recentComplaints) { %>
                                        <tr>
                                            <td><span class="badge bg-light text-dark border fw-bold"><%= c.getComplaintNumber() %></span></td>
                                            <td>
                                                <div class="fw-semibold text-dark small"><%= c.getUserName() %></div>
                                                <small class="text-muted"><%= c.getLocation() %></small>
                                            </td>
                                            <td><span class="text-secondary small"><%= c.getCategoryName() %></span></td>
                                            <td class="fw-semibold small"><%= c.getTitle() %></td>
                                            <td class="small">
                                                <%= c.getTechnicianName() != null ? c.getTechnicianName() : "<span class='text-danger small fst-italic'>Unassigned</span>" %>
                                            </td>
                                            <td><span class="badge <%= c.getStatusBadgeClass() %>"><%= c.getStatus() %></span></td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } %>
                </div>
            </div>

            <!-- Right: Technician Workload Distribution -->
            <div class="col-lg-4">
                <div class="card border-0 shadow-sm bg-white p-4 h-100">
                    <h5 class="fw-bold mb-3 pb-2 border-bottom">
                        <i class="bi bi-people-fill me-2 text-primary"></i>Technician Workloads
                    </h5>
                    <p class="text-muted small mb-3">Active pending & in-progress assignments per maintenance technician.</p>

                    <% if (workloads == null || workloads.isEmpty()) { %>
                        <p class="text-muted small">No technicians registered.</p>
                    <% } else { %>
                        <ul class="list-group list-group-flush">
                            <% for (Map<String, Object> tech : workloads) { 
                                int load = (Integer) tech.get("activeLoad");
                                String loadBadge = load == 0 ? "bg-success" : (load < 3 ? "bg-info text-dark" : "bg-warning text-dark");
                            %>
                                <li class="list-group-item d-flex justify-content-between align-items-center px-0 py-3">
                                    <div class="d-flex align-items-center">
                                        <div class="bg-primary bg-opacity-10 text-primary rounded-circle p-2 me-3">
                                            <i class="bi bi-person-wrench fs-5"></i>
                                        </div>
                                        <div>
                                            <div class="fw-semibold text-dark"><%= tech.get("technicianName") %></div>
                                            <small class="text-muted">Staff ID: #<%= tech.get("technicianId") %></small>
                                        </div>
                                    </div>
                                    <span class="badge <%= loadBadge %> rounded-pill px-3 py-2">
                                        <%= load %> active <%= load == 1 ? "task" : "tasks" %>
                                    </span>
                                </li>
                            <% } %>
                        </ul>
                    <% } %>

                    <div class="mt-4 pt-3 border-top text-center">
                        <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-outline-secondary btn-sm w-100">
                            <i class="bi bi-arrow-repeat me-1"></i>Assign Work to Staff
                        </a>
                    </div>
                </div>
            </div>

        </div>

    </div>

    <!-- Bootstrap Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
