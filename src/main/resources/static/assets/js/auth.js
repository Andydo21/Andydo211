function getJwtFromCookie() {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; jwt=`);
    return parts.length === 2 ? parts.pop().split(';').shift() : null;
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Error parsing JWT:", e);
        return null;
    }
}

function checkLoginStatus() {
    const jwt = getJwtFromCookie();
    const userInfo = document.getElementById("user-info");

    if (jwt) {
        const payload = parseJwt(jwt);
        if (payload) {
            const fullName = payload.fullName || 'User';
            console.log("User logged in with fullName:", fullName);
            userInfo.innerHTML = `
                <div class="dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="userDropdown" data-bs-toggle="dropdown">
                        <i class="fas fa-user"></i> ${fullName}
                    </a>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" id="logout-btn" href="#">Đăng xuất</a></li>
                    </ul>
                </div>
            `;
            document.getElementById("logout-btn").addEventListener("click", logout);
        } else {
            console.log("Invalid JWT payload, showing login button");
            userInfo.innerHTML = `<a href="/user/login">Đăng nhập</a>`;
        }
    } else {
        console.log("No JWT in cookie, showing login button");
        userInfo.innerHTML = `<a href="/user/login">Đăng nhập</a>`;
    }
}

function logout(event) {
    event.preventDefault();
    console.log("Logging out...");
    document.cookie = 'jwt=; Max-Age=0; path=/';
    window.location.href = "/user/login";
}

document.addEventListener("DOMContentLoaded", checkLoginStatus);