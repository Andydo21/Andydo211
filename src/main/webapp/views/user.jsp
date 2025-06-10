<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">

<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Thông tin tài khoản - NEW MOVIE</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/reset.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css" />
  <style>
    body,
    p,
    h1,
    h2,
    h3,
    h4,
    h5,
    h6,
    span,
    a {
        color: white !important;
    }


    header.navbar {
        background-color: #111111 !important;
    }

</style>
</head>

<body>
  <!-- Header/Navbar -->
  <header class="navbar navbar-expand-lg navbar-dark fixed-top">
    <div class="container-fluid px-5">
      <a class="navbar-brand fw-bold text-warning" href="${pageContext.request.contextPath}/home">
        NEW <span class="text-white">MOVIE</span>
      </a>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav main-nav">
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/home">Trang chủ</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/movie">Phim</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">Thể Loại</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/mylist">Danh sách của tôi</a>
          </li>
        </ul>

        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <a class="nav-link" href="#"><i class="fas fa-search"></i></a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#"><i class="fas fa-bell"></i></a>
          </li>
          <li class="nav-item">
            <a class="nav-link active" href="${pageContext.request.contextPath}/user"><i class="fas fa-user"></i></a>
          </li>
        </ul>
      </div>
    </div>
  </header>

  <!-- User Profile Section -->
  <section class="user-profile py-5">
    <div class="container" style="margin-top: 80px;">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
          <div class="card bg-dark text-white">
            <div class="card-header bg-warning text-dark">
              <h4 class="mb-0">Thông tin tài khoản</h4>
            </div>
            <div class="card-body">
              <form:form method="post" modelAttribute="user" action="${pageContext.request.contextPath}/user/update" enctype="multipart/form-data">
                <!-- Avatar -->
                <div class="text-center mb-4">
                  <div class="position-relative d-inline-block">
                    <img src="${user.avatarUrl}" alt="Avatar" class="rounded-circle" width="150" height="150">
                    <input type="file" name="avatar" id="avatar" class="d-none" accept="image/*">
                    <button type="button" class="btn btn-sm btn-warning position-absolute bottom-0 end-0" onclick="document.getElementById('avatar').click()">
                      <i class="fas fa-camera"></i>
                    </button>
                  </div>
                </div>

                <!-- User Info -->
                <div class="mb-3">
                  <form:label path="username" class="form-label">Tên đăng nhập</form:label>
                  <form:input path="username" class="form-control bg-dark text-white" readonly="true"/>
                </div>

                <div class="mb-3">
                  <form:label path="email" class="form-label">Email</form:label>
                  <form:input path="email" type="email" class="form-control bg-dark text-white"/>
                  <form:errors path="email" class="text-danger"/>
                </div>

                <div class="mb-3">
                  <form:label path="fullName" class="form-label">Họ và tên</form:label>
                  <form:input path="fullName" class="form-control bg-dark text-white"/>
                  <form:errors path="fullName" class="text-danger"/>
                </div>

                <div class="mb-4">
                  <form:label path="phone" class="form-label">Số điện thoại</form:label>
                  <form:input path="phone" class="form-control bg-dark text-white"/>
                  <form:errors path="phone" class="text-danger"/>
                </div>

                <!-- Change Password -->
                <div class="mb-3">
                  <h5 class="border-bottom pb-2">Đổi mật khẩu</h5>
                  <div class="mb-3">
                    <form:label path="currentPassword" class="form-label">Mật khẩu hiện tại</form:label>
                    <form:password path="currentPassword" class="form-control bg-dark text-white"/>
                    <form:errors path="currentPassword" class="text-danger"/>
                  </div>
                  <div class="mb-3">
                    <form:label path="newPassword" class="form-label">Mật khẩu mới</form:label>
                    <form:password path="newPassword" class="form-control bg-dark text-white"/>
                    <form:errors path="newPassword" class="text-danger"/>
                  </div>
                  <div class="mb-3">
                    <form:label path="confirmPassword" class="form-label">Xác nhận mật khẩu mới</form:label>
                    <form:password path="confirmPassword" class="form-control bg-dark text-white"/>
                    <form:errors path="confirmPassword" class="text-danger"/>
                  </div>
                </div>

                <!-- Buttons -->
                <div class="d-grid gap-2">
                  <button type="submit" class="btn btn-warning">Lưu thay đổi</button>
                  <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger">Đăng xuất</a>
                </div>
              </form:form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- Footer -->
  <footer class="footer">
    <div class="container">
      <div class="row">
        <div class="col-lg-3 col-md-12 mb-4">
          <a class="footer-brand" href="${pageContext.request.contextPath}/home"> NEW <span>MOVIE</span> </a>
          <p class="location">
            <i class="fas fa-map-marker-alt"></i>
            Movie Web Việt Nam
          </p>
        </div>

        <div class="col-lg-3 col-md-4 col-6 mb-4">
          <h5>KHÁM PHÁ</h5>
          <ul class="footer-links">
            <li><a href="#">Trung tâm hỗ trợ</a></li>
            <li><a href="#">Tài khoản</a></li>
            <li><a href="#">Cách xem</a></li>
            <li><a href="#">Chỉ có tại NEW MOVIE</a></li>
          </ul>
        </div>

        <div class="col-lg-3 col-md-4 col-6 mb-4">
          <h5>PHÁP LÝ</h5>
          <ul class="footer-links">
            <li><a href="#">Điều khoản sử dụng</a></li>
            <li><a href="#">Chính sách bảo mật</a></li>
            <li><a href="#">Cookie</a></li>
            <li><a href="#">Thông tin doanh nghiệp</a></li>
            <li><a href="#">Thông báo pháp lý</a></li>
          </ul>
        </div>

        <div class="col-lg-3 col-md-4 col-6 mb-4">
          <h5>HỖ TRỢ</h5>
          <ul class="footer-links">
            <li><a href="#">FAQ</a></li>
            <li><a href="#">Kiểm tra tốc độ</a></li>
            <li><a href="#">Liên hệ</a></li>
            <li><a href="#">Trung tâm đa phương tiện</a></li>
          </ul>
        </div>
      </div>
    </div>
  </footer>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/js/auth.js"></script>
</body>

</html> 