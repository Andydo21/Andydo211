document
  .getElementById("login-form")
  .addEventListener("submit", function (event) {
    event.preventDefault();

    let username = document.getElementById("login-username").value;
    let password = document.getElementById("login-password").value;
    let errorDiv = document.getElementById("login-error");
    let loginButton = document.querySelector(".btn-login");

    errorDiv.innerText = "";
    loginButton.innerText = "Đang đăng nhập...";
    loginButton.disabled = true;

    fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        userName: username,
        password: password,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.error) {
          errorDiv.innerText = "Sai tài khoản hoặc mật khẩu!";
        } else {
          alert("Đăng nhập thành công!");
          localStorage.setItem("user", JSON.stringify(data.user));
          window.location.href = "index.html";
        }
      })
      .catch((error) => {
        console.error("Lỗi đăng nhập:", error);
        errorDiv.innerText = "Không thể kết nối đến server!";
      })
      .finally(() => {
        loginButton.innerText = "Đăng nhập";
        loginButton.disabled = false;
      });
  });
