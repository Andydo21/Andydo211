<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
        <html lang="vi">

        <head>
            <meta charset="UTF-8" />
            <title>Admin - Tạo User</title>
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
                        <a href="admin-home.jsp"><i class=" bi bi-clipboard-data-fill"></i>
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
                            <a class="nav-link submenu active" href="admin-user-update.jsp">Tạo user</a>
                        </div>
                        <a href="#"><i class="bi bi-globe"></i> Thể loại & Quốc gia</a>
                    </nav>

                    <main class="col-md-10 ms-sm-auto px-md-4 mt-3">
                        <div class="mb-3 d-flex justify-content-between align-items-center">
                            <a href="user-list.jsp" class="btn btn-secondary">&larr; Quay lại</a>
                            <button class="btn btn-primary" onclick="document.getElementById('userForm').submit()">Tạo
                                user</button>
                        </div>

                        <div class="card">
                            <div class="card-header">Thông tin user</div>
                            <div class="card-body">
                                <form:form method="POST" action="/Users/save" modelAttribute="user" id="userForm"
                                    class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Tên đăng nhập</label>
                                        <form:input path="fullName" cssClass="form-control" />
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Email</label>
                                        <form:input path="userName" type="email" cssClass="form-control" />
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Mật khẩu</label>
                                        <form:password path="password" cssClass="form-control" />
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Số điện thoại</label>
                                        <form:input path="phone" cssClass="form-control" />
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Quyền</label>
                                        <form:select path="role" cssClass="form-select">
                                            <form:option value="USER">USER</form:option>
                                            <form:option value="ADMIN">ADMIN</form:option>
                                        </form:select>
                                    </div>
                                </form:form>
                            </div>
                        </div>
                    </main>
                </div>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>