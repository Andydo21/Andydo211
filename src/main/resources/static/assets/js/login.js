document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.getElementById("login-form");
    if (!loginForm) {
        console.error("Không tìm thấy form login-form");
        return;
    }

    const errorDiv = document.getElementById("login-error");
    const loginButton = document.querySelector(".btn-login");

    if (!errorDiv || !loginButton) {
        console.error("Không tìm thấy errorDiv hoặc loginButton");
        return;
    }

    loginForm.addEventListener("submit", function () {
        errorDiv.innerText = "";
        loginButton.innerText = "Đang đăng nhập...";
        loginButton.disabled = true;
    });
});