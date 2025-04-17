function checkLoginStatus() {
  let user = localStorage.getItem("user");
  let authButtons = document.querySelector(".auth-buttons"); // Nút Đăng nhập/Đăng ký
  let userInfo = document.getElementById("user-info"); // Khu vực hiển thị user

  if (user) {
    let userData = JSON.parse(user);
    authButtons?.classList.add("d-none"); // Ẩn nút Đăng nhập/Đăng ký

    if (userInfo) {
      userInfo.innerHTML = `
          <div class="dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" data-bs-toggle="dropdown">
              <i class="fas fa-user"></i> ${userData.fullName}
            </a>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#">Hồ sơ</a></li>
              <li><a class="dropdown-item" id="logout-btn" href="#">Đăng xuất</a></li>
            </ul>
          </div>
        `;

      // Xử lý logout
      document.getElementById("logout-btn").addEventListener("click", logout);
    }
  } else {
    authButtons?.classList.remove("d-none"); // Hiện lại nút đăng nhập
    if (userInfo) userInfo.innerHTML = `<a href="login.html">Đăng nhập</a>`;
  }
}

// Hàm đăng xuất
function logout() {
  localStorage.removeItem("user");
  window.location.href = "login.html"; // Chuyển hướng về login
}

// Chặn truy cập trang yêu cầu đăng nhập
function requireLogin() {
  if (!localStorage.getItem("user")) {
    document.body.innerHTML = `<div class="alert  text-center mt-5">
        <h4>Bạn cần đăng nhập để truy cập trang này!</h4>
        <a href="login.html" class="btn btn-primary mt-3">Đăng nhập</a>
        <a href="index.html" class="btn btn-primary mt-3">Trang chủ</a>
      </div>`;
  }
}
// Gọi khi trang tải
document.addEventListener("DOMContentLoaded", checkLoginStatus);
