<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In — SmartResolve</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f6f9;
        }
        .auth-card {
            border: none;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
        }
        .brand-header {
            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
            color: #fff;
            padding: 30px 20px;
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }
        .demo-box {
            background-color: #f8f9fa;
            border: 1px dashed #dee2e6;
            border-radius: 8px;
            font-size: 0.85rem;
        }
    </style>
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100 py-5">

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card auth-card overflow-hidden">
                    
                    <!-- Header -->
                    <div class="brand-header text-center">
                        <h3 class="fw-bold mb-1"><i class="bi bi-tools me-2"></i>SmartResolve</h3>
                        <p class="mb-0 text-white-50 small">Sign in to your account</p>
                    </div>

                    <div class="card-body p-4 p-md-5">

                        <!-- Flash Messages -->
                        <% if (request.getAttribute("successMessage") != null) { %>
                            <div class="alert alert-success alert-dismissible fade show d-flex align-items-center mb-4" role="alert">
                                <i class="bi bi-check-circle-fill me-2 flex-shrink-0"></i>
                                <div><%= request.getAttribute("successMessage") %></div>
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        <% } %>

                        <% if (request.getAttribute("infoMessage") != null) { %>
                            <div class="alert alert-info alert-dismissible fade show d-flex align-items-center mb-4" role="alert">
                                <i class="bi bi-info-circle-fill me-2 flex-shrink-0"></i>
                                <div><%= request.getAttribute("infoMessage") %></div>
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        <% } %>

                        <% if (request.getAttribute("warningMessage") != null) { %>
                            <div class="alert alert-warning alert-dismissible fade show d-flex align-items-center mb-4" role="alert">
                                <i class="bi bi-exclamation-triangle-fill me-2 flex-shrink-0"></i>
                                <div><%= request.getAttribute("warningMessage") %></div>
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        <% } %>

                        <% if (request.getAttribute("errorMessage") != null) { %>
                            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center mb-4" role="alert">
                                <i class="bi bi-x-circle-fill me-2 flex-shrink-0"></i>
                                <div><%= request.getAttribute("errorMessage") %></div>
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        <% } %>

                        <form action="${pageContext.request.contextPath}/login" method="POST" id="loginForm" novalidate>
                            
                            <!-- Email Address -->
                            <div class="mb-3">
                                <label for="email" class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                                    <input type="email" class="form-control" id="email" name="email" 
                                           placeholder="e.g. user@example.com" required
                                           value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>">
                                </div>
                            </div>

                            <!-- Password -->
                            <div class="mb-4">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <label for="password" class="form-label fw-semibold mb-0">Password <span class="text-danger">*</span></label>
                                </div>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-lock"></i></span>
                                    <input type="password" class="form-control" id="password" name="password" 
                                           placeholder="Enter your password" required>
                                    <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                        <i class="bi bi-eye" id="eyeIcon"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- Submit Button -->
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary btn-lg fw-semibold">
                                    <i class="bi bi-box-arrow-in-right me-2"></i>Sign In
                                </button>
                            </div>
                        </form>

                        <!-- Demo Credentials Box -->
                        <div class="demo-box p-3 mt-4">
                            <div class="fw-bold text-secondary mb-1"><i class="bi bi-key me-1"></i>Demo Accounts (Seed Data):</div>
                            <div class="text-muted">
                                <div><strong>Admin:</strong> admin@smartresolve.com / Password@123</div>
                                <div><strong>Technician:</strong> tech1@smartresolve.com / Password@123</div>
                                <div><strong>User:</strong> user@smartresolve.com / Password@123</div>
                            </div>
                        </div>

                        <!-- Registration Link -->
                        <div class="text-center mt-4">
                            <span class="text-muted">Don't have an account?</span>
                            <a href="${pageContext.request.contextPath}/register" class="fw-semibold text-decoration-none ms-1">Register here</a>
                        </div>
                    </div>

                    <div class="card-footer bg-light text-center py-3 border-top-0">
                        <small class="text-muted"><a href="${pageContext.request.contextPath}/" class="text-decoration-none">← Back to Home</a></small>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Password visibility toggle
        const togglePassword = document.querySelector('#togglePassword');
        const passwordInput = document.querySelector('#password');
        const eyeIcon = document.querySelector('#eyeIcon');

        togglePassword.addEventListener('click', function () {
            const isPassword = passwordInput.getAttribute('type') === 'password';
            passwordInput.setAttribute('type', isPassword ? 'text' : 'password');
            eyeIcon.classList.toggle('bi-eye');
            eyeIcon.classList.toggle('bi-eye-slash');
        });
    </script>
</body>
</html>
