document.addEventListener("DOMContentLoaded", function() {
    const registerForm = document.getElementById("register-form");
    if (!registerForm) {
        console.error("Không tìm thấy form register-form");
        return;
    }

    const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
    const phoneRegex = /^[0-9]{10}$/;

    registerForm.addEventListener("submit", function(event) {
        let hasError = false;

        // Reset error messages
        document.querySelectorAll("span[id$='-error']").forEach(span => span.textContent = "");

        // Validate username
        const username = document.getElementById("username").value.trim();
        if (!username) {
            document.getElementById("username-error").textContent = "Tên đăng nhập không được để trống!";
            hasError = true;
        }

        // Validate password
        const password = document.getElementById("password").value.trim();
        if (!password) {
            document.getElementById("password-error").textContent = "Mật khẩu không được để trống!";
            hasError = true;
        }

        // Validate fullName
        const fullName = document.getElementById("fullname").value.trim();
        if (!fullName) {
            document.getElementById("fullname-error").textContent = "Họ tên không được để trống!";
            hasError = true;
        }

        // Validate gender
        const gender = document.getElementById("gender").value;
        if (!gender) {
            document.getElementById("gender-error").textContent = "Vui lòng chọn giới tính!";
            hasError = true;
        }

        // Validate birthday
        const birthday = document.getElementById("birthday").value;
        if (!birthday) {
            document.getElementById("birthday-error").textContent = "Ngày sinh không được để trống!";
            hasError = true;
        } else {
            const birthdayDate = new Date(birthday);
            const today = new Date();
            if (birthdayDate > today) {
                document.getElementById("birthday-error").textContent = "Ngày sinh không được là ngày trong tương lai!";
                hasError = true;
            }
        }

        // Validate address
        const address = document.getElementById("address").value.trim();
        if (!address) {
            document.getElementById("address-error").textContent = "Địa chỉ không được để trống!";
            hasError = true;
        }

        // Validate email
        const email = document.getElementById("email").value.trim();
        if (!email) {
            document.getElementById("email-error").textContent = "Email không được để trống!";
            hasError = true;
        } else if (!emailRegex.test(email)) {
            document.getElementById("email-error").textContent = "Email không đúng định dạng!";
            hasError = true;
        }

        // Validate telephone
        const telephone = document.getElementById("telephone").value.trim();
        if (!telephone) {
            document.getElementById("telephone-error").textContent = "Số điện thoại không được để trống!";
            hasError = true;
        } else if (!phoneRegex.test(telephone)) {
            document.getElementById("telephone-error").textContent = "Số điện thoại phải có 10 chữ số!";
            hasError = true;
        }

        if (hasError) {
            event.preventDefault();
        }
    });
});