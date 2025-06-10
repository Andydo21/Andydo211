document
  .getElementById("register-form")
  .addEventListener("submit", function (event) {
    event.preventDefault();

    let name = document.getElementById("register-name").value.trim();
    let email = document.getElementById("register-email").value.trim();
    let phone = document.getElementById("register-phone").value.trim();
    let password = document.getElementById("register-password").value;
    let confirmPassword = document.getElementById(
      "register-confirm-password"
    ).value;
    let errorDiv = document.getElementById("register-error");
    let registerButton = document.querySelector(".btn-register");

    errorDiv.innerText = ""; // Xóa lỗi cũ
    registerButton.innerText = "Đang đăng ký...";
    registerButton.disabled = true;

    if (password !== confirmPassword) {
      errorDiv.innerText = "Mật khẩu nhập lại không khớp!";
      registerButton.innerText = "Đăng ký";
      registerButton.disabled = false;
      return;
    }

    // Gửi request đăng ký
    fetch("http://localhost:8080/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        userName: email,
        password: password,
        fullName: name,
        phone: phone,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.error) {
          errorDiv.innerText = data.error; // Hiển thị lỗi nếu username đã tồn tại
        } else {
          alert("Đăng ký thành công! Vui lòng đăng nhập.");
          window.location.href = "login.html";
        }
      })
      .catch((error) => {
        console.error("Lỗi đăng ký:", error);
        errorDiv.innerText = "Không thể kết nối đến server!";
      })
      .finally(() => {
        registerButton.innerText = "Đăng ký";
        registerButton.disabled = false;
      });
  });
