<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <html lang="vi">

        <head>
            <meta charset="UTF-8" />
            <title>Admin - Dashboard</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
            <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet" />
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
            <link rel="stylesheet" href="styles.css" />
        </head>

        <body>
            <div class="container-fluid">
                <div class="row">
                    <!-- Sidebar -->
                    <nav class="col-md-2 d-none d-md-block sidebar">
                        <div class="text-center mb-4">
                            <h4><span style="font-size: 0.7em">ADMIN</span></h4>
                        </div>
                        <a href="admin-home.jsp" class="active"><i class=" bi bi-clipboard-data-fill"></i>
                            Dashboard</a>
                        <a class="nav-link d-flex justify-content-between align-items-center" data-bs-toggle="collapse"
                            href="#movieMenu" role="button" aria-expanded="true" aria-controls="movieMenu">
                            <span><i class="fas fa-film"></i> Phim</span>
                            <i class="fas fa-chevron-down nav-toggle-icon"></i>
                        </a>
                        <div class="collapse show" id="movieMenu">
                            <a class="nav-link submenu" href="admin-movie.jsp">Danh sách phim</a>
                            <a class="nav-link submenu" href="admin-movie-add.jsp">Thêm phim</a>
                            <a class="nav-link submenu" href="admin-movie-update.jsp">Sửa phim</a>
                        </div>
                        <a class="nav-link d-flex justify-content-between align-items-center" data-bs-toggle="collapse"
                            href="#userMenu" role="button" aria-expanded="true" aria-controls="userMenu">
                            <span><i class="bi bi-person-fill"></i> User</span>
                            <i class="fas fa-chevron-down nav-toggle-icon"></i>
                        </a>
                        <div class="collapse show" id="userMenu">
                            <a class="nav-link submenu" href="admin-user.jsp">Danh sách user</a>
                            <a class="nav-link submenu" href="admin-user-update.jsp">Tạo user</a>
                        </div>
                        <a href="#"><i class="bi bi-globe"></i> Thể loại & Quốc gia</a>
                    </nav>

                    <!-- Main content -->
                    <main class="col-md-10 ms-sm-auto px-md-4 mt-3">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h2>Trang quản trị</h2>
                        </div>

                        <div class="row">
                            <div class="col-md-4">
                                <div class="card text-white bg-primary mb-3">
                                    <div class="card-body">
                                        <h5 class="card-title">Tổng số phim</h5>
                                        <p class="card-text fs-3">${movieCount}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-success mb-3">
                                    <div class="card-body">
                                        <h5 class="card-title">Tổng số user</h5>
                                        <p class="card-text fs-3">${userCount}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-warning mb-3">
                                    <div class="card-body">
                                        <h5 class="card-title">Thể loại</h5>
                                        <p class="card-text fs-3">${categoryCount}</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="card mt-4">
                            <div class="card-header">Thống kê gần đây</div>
                            <div class="card-body">
                                <p>Đây là khu vực bạn có thể mở rộng hiển thị thống kê chi tiết hơn.</p>
                            </div>
                        </div>
                    </main>
                </div>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>