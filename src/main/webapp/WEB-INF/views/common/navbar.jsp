<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String currentRole = (String) session.getAttribute("userRole");
    String currentUserName = (String) session.getAttribute("userName");
    if (currentUserName == null) currentUserName = "User";
%>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm py-2">
    <div class="container-fluid px-lg-4">
        <a class="navbar-brand fw-bold d-flex align-items-center" href="${pageContext.request.contextPath}/<%= "ADMIN".equalsIgnoreCase(currentRole) ? "admin/dashboard" : ("TECHNICIAN".equalsIgnoreCase(currentRole) ? "technician/dashboard" : "user/dashboard") %>">
            <span class="bg-primary text-white rounded-3 p-1 px-2 me-2">
                <i class="bi bi-shield-check"></i>
            </span>
            <span class="text-white">Smart<span class="text-primary">Resolve</span></span>
        </a>

        <button class="navbar-toggler border-0" type="button" data-bs-toggle="collapse" data-bs-target="#topNavbarContent" aria-controls="topNavbarContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="topNavbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <% if ("ADMIN".equalsIgnoreCase(currentRole)) { %>
                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i>Admin Console
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/admin/complaints">
                            <i class="bi bi-folder2-open me-1"></i>All Complaints
                        </a>
                    </li>
                <% } else if ("TECHNICIAN".equalsIgnoreCase(currentRole)) { %>
                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/technician/dashboard">
                            <i class="bi bi-tools me-1"></i>Workbench
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/technician/complaints">
                            <i class="bi bi-clipboard-check me-1"></i>My Assignments
                        </a>
                    </li>
                <% } else { %>
                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/user/dashboard">
                            <i class="bi bi-grid-1x2 me-1"></i>Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/user/complaints">
                            <i class="bi bi-card-checklist me-1"></i>My Complaints
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/complaint/submit">
                            <i class="bi bi-plus-circle me-1"></i>New Request
                        </a>
                    </li>
                <% } %>
            </ul>

            <div class="d-flex align-items-center gap-3">
                <div class="text-light small text-end d-none d-md-block">
                    <div class="fw-semibold"><%= currentUserName %></div>
                    <span class="badge bg-secondary-subtle text-light border border-secondary px-2 py-0" style="font-size: 0.7rem;">
                        <%= currentRole != null ? currentRole : "GUEST" %>
                    </span>
                </div>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm d-flex align-items-center">
                    <i class="bi bi-box-arrow-right me-1"></i>Sign Out
                </a>
            </div>
        </div>
    </div>
</nav>
