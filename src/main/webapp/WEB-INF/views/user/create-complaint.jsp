<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.smartresolve.model.Category" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submit Complaint — SmartResolve</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8fafc;
        }
        .form-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.05);
        }
    </style>
</head>
<body class="bg-light">

    <!-- Top Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/user/dashboard">
                <i class="bi bi-tools text-primary me-2"></i>SmartResolve
            </a>
            <div class="ms-auto d-flex align-items-center">
                <span class="text-white-50 small me-3 d-none d-sm-inline">
                    <i class="bi bi-person-circle me-1"></i><%= session.getAttribute("userName") != null ? session.getAttribute("userName") : "User" %>
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">

                <!-- Breadcrumb -->
                <nav aria-label="breadcrumb" class="mb-4">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/user/dashboard" class="text-decoration-none">Dashboard</a></li>
                        <li class="breadcrumb-item active" aria-current="page">New Complaint</li>
                    </ol>
                </nav>

                <div class="card form-card p-4 p-md-5 bg-white">
                    <div class="d-flex align-items-center mb-4 pb-3 border-bottom">
                        <div class="bg-primary bg-opacity-10 text-primary rounded-circle p-3 me-3">
                            <i class="bi bi-pencil-square fs-3"></i>
                        </div>
                        <div>
                            <h3 class="fw-bold mb-0">Submit Service Request</h3>
                            <p class="text-muted small mb-0">Provide detailed information so our team can resolve the issue promptly.</p>
                        </div>
                    </div>

                    <!-- Server Validation Error Alert -->
                    <% if (request.getAttribute("errorMessage") != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center mb-4" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2 flex-shrink-0"></i>
                            <div><%= request.getAttribute("errorMessage") %></div>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/user/complaint/create" method="POST" id="complaintForm" novalidate>

                        <!-- Complaint Title -->
                        <div class="mb-3">
                            <label for="title" class="form-label fw-semibold">Complaint Title <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="title" name="title" 
                                   placeholder="e.g. Water dripping from washroom tap continuously" required
                                   value="<%= request.getAttribute("title") != null ? request.getAttribute("title") : "" %>">
                            <div class="form-text">Summarize the issue clearly in one sentence.</div>
                        </div>

                        <div class="row g-3 mb-3">
                            <!-- Category Dropdown -->
                            <div class="col-md-6">
                                <label for="categoryId" class="form-label fw-semibold">Category <span class="text-danger">*</span></label>
                                <select class="form-select" id="categoryId" name="categoryId" required>
                                    <option value="" disabled <%= request.getAttribute("selectedCategoryId") == null ? "selected" : "" %>>Select a category...</option>
                                    <% 
                                        @SuppressWarnings("unchecked")
                                        List<Category> categories = (List<Category>) request.getAttribute("categories");
                                        Integer selectedCat = (Integer) request.getAttribute("selectedCategoryId");
                                        if (categories != null) {
                                            for (Category cat : categories) {
                                                boolean isSel = (selectedCat != null && selectedCat.equals(cat.getId()));
                                    %>
                                        <option value="<%= cat.getId() %>" <%= isSel ? "selected" : "" %>><%= cat.getName() %></option>
                                    <% 
                                            }
                                        } 
                                    %>
                                </select>
                            </div>

                            <!-- Priority -->
                            <div class="col-md-6">
                                <label for="priority" class="form-label fw-semibold">Priority Level</label>
                                <select class="form-select" id="priority" name="priority">
                                    <% String selP = (String) request.getAttribute("selectedPriority"); %>
                                    <option value="LOW" <%= "LOW".equals(selP) ? "selected" : "" %>>Low (Minor cosmetic issue)</option>
                                    <option value="MEDIUM" <%= (selP == null || "MEDIUM".equals(selP)) ? "selected" : "" %>>Medium (Standard maintenance)</option>
                                    <option value="HIGH" <%= "HIGH".equals(selP) ? "selected" : "" %>>High (Significant disruption)</option>
                                    <option value="URGENT" <%= "URGENT".equals(selP) ? "selected" : "" %>>Urgent (Emergency hazard)</option>
                                </select>
                            </div>
                        </div>

                        <!-- Location -->
                        <div class="mb-3">
                            <label for="location" class="form-label fw-semibold">Exact Location <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-geo-alt"></i></span>
                                <input type="text" class="form-control" id="location" name="location" 
                                       placeholder="e.g. Block C, 3rd Floor, Room 312 or Common Corridor" required
                                       value="<%= request.getAttribute("location") != null ? request.getAttribute("location") : "" %>">
                            </div>
                        </div>

                        <!-- Detailed Description -->
                        <div class="mb-4">
                            <label for="description" class="form-label fw-semibold">Detailed Description <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="description" name="description" rows="4" 
                                      placeholder="Explain the problem in detail: when it started, severity, and any hazards..." required><%= request.getAttribute("description") != null ? request.getAttribute("description") : "" %></textarea>
                            <div class="form-text">Minimum 10 characters.</div>
                        </div>

                        <!-- Buttons -->
                        <div class="d-flex justify-content-between align-items-center pt-2">
                            <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-outline-secondary px-4">
                                <i class="bi bi-arrow-left me-1"></i>Cancel
                            </a>
                            <button type="submit" class="btn btn-primary px-4 fw-semibold shadow-sm">
                                <i class="bi bi-send me-1"></i>Submit Complaint
                            </button>
                        </div>
                    </form>
                </div>

            </div>
        </div>
    </div>

    <!-- Bootstrap Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
