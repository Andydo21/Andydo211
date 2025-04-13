<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <html lang="vi">

            <head>
                <meta charset="UTF-8" />
                <title>Admin - Cập nhật phim</title>
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
                                <a class="nav-link submenu active" href="admin-movie-update.jsp">Sửa phim</a>
                            </div>
                            <a class="nav-link d-flex justify-content-between align-items-center"
                                data-bs-toggle="collapse" href="#userMenu" role="button" aria-expanded="true"
                                aria-controls="userMenu">
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
                                <a href="movie-list.jsp" class="btn btn-secondary">&larr; Quay lại</a>
                                <button class="btn btn-primary"
                                    onclick="document.getElementById('updateForm').submit()">Lưu
                                    phim</button>
                            </div>

                            <!-- Tabs -->
                            <ul class="nav nav-tabs" id="movieTab" role="tablist">
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link active" id="info-tab" data-bs-toggle="tab"
                                        data-bs-target="#info" type="button" role="tab">Thông tin
                                        phim</button>
                                </li>
                            </ul>

                            <div class="tab-content mt-3" id="movieTabContent">
                                <div class="tab-pane fade show active" id="info" role="tabpanel">
                                    <form:form id="updateForm" method="POST" action="/Movies/update/${movie.id}"
                                        modelAttribute="movie" enctype="multipart/form-data" class="row g-3">

                                        <div class="col-md-6">
                                            <label class="form-label">Tên phim</label>
                                            <form:input path="productName" cssClass="form-control" />
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Thể loại</label>
                                            <form:input path="categoryID" cssClass="form-control" />
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Tập phim</label>
                                            <form:input path="episode" cssClass="form-control" />
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Năm phát hành</label>
                                            <form:input path="year" type="number" cssClass="form-control" />
                                        </div>
                                        <div class="col-md-12">
                                            <label class="form-label">Mô tả</label>
                                            <form:textarea path="description" cssClass="form-control" />
                                        </div>
                                        <div class="col-md-12">
                                            <label class="form-label">Ảnh poster (upload mới)</label>
                                            <input type="file" name="imageFile" class="form-control" accept="image/*" />
                                        </div>
                                        <div class="col-md-12">
                                            <label class="form-label">Video phim (upload mới)</label>
                                            <input type="file" name="videoFile" class="form-control" accept="video/*" />
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