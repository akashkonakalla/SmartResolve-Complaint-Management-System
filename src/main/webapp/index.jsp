<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartResolve — Smart Complaint & Service Request Management</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .hero-section {
            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
            color: #ffffff;
            padding: 80px 0;
        }
        .feature-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .feature-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.12);
        }
        .icon-box {
            width: 54px;
            height: 54px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
                <i class="bi bi-tools text-primary me-2"></i>SmartResolve
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item me-2">
                        <a class="btn btn-outline-light btn-sm px-3" href="${pageContext.request.contextPath}/login">
                            <i class="bi bi-box-arrow-in-right me-1"></i>Login
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-primary btn-sm px-3" href="${pageContext.request.contextPath}/register">
                            <i class="bi bi-person-plus me-1"></i>Register
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section text-center">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <span class="badge bg-light text-primary px-3 py-2 rounded-pill fw-semibold mb-3">Enterprise Maintenance Management</span>
                    <h1 class="display-4 fw-bold mb-3">Smart Complaint & Service Request Management</h1>
                    <p class="lead mb-4 opacity-90">
                        Streamline issue reporting, automate technician assignments, and track resolutions with complete transparency for colleges, hostels, apartments, and offices.
                    </p>
                    <div class="d-flex justify-content-center gap-3">
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-lg px-4 shadow">Get Started</a>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-light btn-lg px-4">Sign In</a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="py-5">
        <div class="container py-4">
            <div class="text-center mb-5">
                <h2 class="fw-bold">Why SmartResolve?</h2>
                <p class="text-muted">Say goodbye to missed phone calls and unorganized chat messages</p>
            </div>
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card h-100 p-4 feature-card">
                        <div class="icon-box bg-primary bg-opacity-10 text-primary">
                            <i class="bi bi-pencil-square"></i>
                        </div>
                        <h5 class="fw-bold">Easy Complaint Submission</h5>
                        <p class="text-muted mb-0">Submit service requests in seconds with category selection, detailed descriptions, location, and photo attachments.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 p-4 feature-card">
                        <div class="icon-box bg-success bg-opacity-10 text-success">
                            <i class="bi bi-clock-history"></i>
                        </div>
                        <h5 class="fw-bold">Real-Time Lifecycle Tracking</h5>
                        <p class="text-muted mb-0">Follow your complaint through every stage: New, Assigned, In Progress, Resolved, and Closed with an audit timeline.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 p-4 feature-card">
                        <div class="icon-box bg-warning bg-opacity-10 text-warning">
                            <i class="bi bi-shield-check"></i>
                        </div>
                        <h5 class="fw-bold">Role-Based Governance</h5>
                        <p class="text-muted mb-0">Dedicated portals for Users, Administrators, and Technicians with strict server-side session authorization.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="bg-dark text-white py-4 mt-auto">
        <div class="container text-center">
            <p class="mb-1 fw-semibold">SmartResolve &copy; 2026. All rights reserved.</p>
            <small class="text-secondary">Designed & Developed by Akash Konakalla</small>
        </div>
    </footer>

    <!-- Bootstrap Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
