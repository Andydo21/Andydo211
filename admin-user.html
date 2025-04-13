<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
            <html lang="vi">

            <head>
                <meta charset="UTF-8" />
                <title>Admin - Danh sách user</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
                <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet" />
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
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
                            <a class="nav-link d-flex justify-content-between align-items-center"
                                data-bs-toggle="collapse" href="#movieMenu" role="button" aria-expanded="true"
                                aria-controls="movieMenu">
                                <span><i class="fas fa-film"></i> Phim</span>
                                <i class="fas fa-chevron-down nav-toggle-icon"></i>
                            </a>
                            <div class="collapse show" id="movieMenu">
                                <a class="nav-link submenu" href="admin-movie.jsp">Danh sách phim</a>
                                <a class="nav-link submenu" href="admin-movie-add.jsp">Thêm phim</a>
                                <a class="nav-link submenu" href="admin-movie-update.jsp">Sửa phim</a>
                            </div>
                            <a class="nav-link d-flex justify-content-between align-items-center"
                                data-bs-toggle="collapse" href="#userMenu" role="button" aria-expanded="true"
                                aria-controls="userMenu">
                                <span><i class="bi bi-person-fill"></i> User</span>
                                <i class="fas fa-chevron-down nav-toggle-icon"></i>
                            </a>
                            <div class="collapse show" id="userMenu">
                                <a class="nav-link submenu active" href="admin-user.jsp">Danh sách user</a>
                                <a class="nav-link submenu" href="admin-user-update.jsp">Tạo user</a>
                            </div>
                            <a href="#"><i class="bi bi-globe"></i> Thể loại & Quốc gia</a>
                        </nav>

                        <!-- Main content -->
                        <main class="col-md-10 ms-sm-auto px-md-4 mt-3">
                            <div class="d-flex justify-content-between mb-3">
                                <h3>Danh sách user</h3>
                                <a href="user-add.jsp" class="btn btn-primary">+ Tạo user</a>
                            </div>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Avatar</th>
                                        <th>Họ tên</th>
                                        <th>Email</th>
                                        <th>Số điện thoại</th>
                                        <th>Role</th>
                                        <th>Trạng thái</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td>
                                                <div class="rounded-circle bg-secondary text-white d-flex justify-content-center align-items-center"
                                                    style="width: 40px; height: 40px;">
                                                    ${fn:substring(user.fullName, 0, 1)}
                                                </div>
                                            </td>
                                            <td>${user.fullName}</td>
                                            <td>${user.userName}</td>
                                            <td>${user.phone}</td>
                                            <td>
                                                <span class="badge bg-secondary">${user.role}</span>
                                            </td>
                                            <td>
                                                <span class="badge ${user.active ? 'bg-success' : 'bg-secondary'}">
                                                    ${user.active ? 'Đã kích hoạt' : 'Chưa kích hoạt'}
                                                </span>
                                            </td>
                                            <td>
                                                <a href="user-update.jsp?id=${user.id}" class="btn btn-sm btn-warning">
                                                    <i class="fas fa-edit"></i>
                                                </a>

                                                <form method="post" action="/Users/delete/${user.id}"
                                                    style="display:inline;"
                                                    onsubmit="return confirm('Bạn có chắc chắn muốn xoá user này?');">
                                                    <button type="submit" class="btn btn-sm btn-danger">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </main>
                    </div>
                </div>

                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            </body>

            </html>