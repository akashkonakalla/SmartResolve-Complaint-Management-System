<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String role = (String) session.getAttribute("userRole");
    String activePage = (String) request.getAttribute("activeMenu");
    if (activePage == null) activePage = "";
%>
<div class="card border-0 shadow-sm rounded-4 p-3 bg-white mb-4">
    <div class="text-uppercase small fw-bold text-muted px-3 mb-2">Navigation</div>
    <div class="nav flex-column nav-pills gap-1">
        <% if ("ADMIN".equalsIgnoreCase(role)) { %>
            <a class="nav-link d-flex align-items-center <%= "dashboard".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="bi bi-speedometer2 me-2"></i>Executive Overview
            </a>
            <a class="nav-link d-flex align-items-center <%= "complaints".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/complaints">
                <i class="bi bi-inbox-fill me-2"></i>Complaint Registry
            </a>
        <% } else if ("TECHNICIAN".equalsIgnoreCase(role)) { %>
            <a class="nav-link d-flex align-items-center <%= "dashboard".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/technician/dashboard">
                <i class="bi bi-speedometer2 me-2"></i>Workbench Overview
            </a>
            <a class="nav-link d-flex align-items-center <%= "assignments".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/technician/complaints">
                <i class="bi bi-clipboard2-check me-2"></i>Assigned Work Orders
            </a>
        <% } else { %>
            <a class="nav-link d-flex align-items-center <%= "dashboard".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/user/dashboard">
                <i class="bi bi-house-door me-2"></i>My Dashboard
            </a>
            <a class="nav-link d-flex align-items-center <%= "complaints".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/user/complaints">
                <i class="bi bi-list-task me-2"></i>My Complaints
            </a>
            <a class="nav-link d-flex align-items-center <%= "submit".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/complaint/submit">
                <i class="bi bi-file-earmark-plus me-2"></i>Submit New Request
            </a>
        <% } %>
    </div>
</div>
