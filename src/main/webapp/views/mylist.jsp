<!DOCTYPE html>
<html lang="vi">

<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Danh sách phim yêu thích - Movie Web</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" />

  <!-- Custom CSS -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/reset.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css" />
</head>

<body>
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
            <a class="nav-link active" href="${pageContext.request.contextPath}/mylist">Danh sách của tôi</a>
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
            <a class="nav-link" href="${pageContext.request.contextPath}/login"><i class="fas fa-user"></i></a>
          </li>
        </ul>
      </div>
    </div>
  </header>

  <!-- Main Content -->
  <div class="container mt-5 pt-5">
    <h2 class="mb-4 text-center">Danh sách phim yêu thích</h2>
    <div class="row">
      <!-- Empty State -->
      <div class="col-12 text-center py-5">
        <i class="fas fa-film fa-3x mb-3"></i>
        <h3>Chưa có phim nào trong danh sách</h3>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Quay lại trang chủ</a>
      </div>
    </div>
  </div>
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

  <!-- Scripts -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
  <script src="${pageContext.request.contextPath}/assets/js/auth.js"></script>
  <script>
    requireLogin();
  </script>
</body>

</html>