<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 — Server Error | SmartResolve</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8fafc;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
        }
        .error-card {
            max-width: 520px;
            border: none;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.06);
            background: #ffffff;
            padding: 3rem 2rem;
            text-align: center;
        }
    </style>
</head>
<body>

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="error-card">
                    <div class="text-danger mb-3">
                        <i class="bi bi-exclamation-octagon fs-1" style="font-size: 4rem !important;"></i>
                    </div>
                    <h1 class="display-5 fw-bold text-dark mb-2">500</h1>
                    <h4 class="fw-semibold text-secondary mb-3">Unexpected System Error</h4>
                    <p class="text-muted mb-4">
                        We encountered an unexpected condition while processing your request. Our system administrators have been alerted to resolve this issue.
                    </p>
                    <div class="d-flex justify-content-center gap-2">
                        <a href="javascript:history.back()" class="btn btn-outline-secondary px-4">
                            <i class="bi bi-arrow-left me-1"></i>Go Back
                        </a>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary px-4">
                            <i class="bi bi-house me-1"></i>Return Home
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

</body>
</html>
