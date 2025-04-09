// Hàm hiển thị modal đăng nhập
function showLoginModal() {
    const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
    loginModal.show();
}

// Hàm hiển thị modal đăng ký
function showRegisterModal() {
    const registerModal = new bootstrap.Modal(document.getElementById('registerModal'));
    registerModal.show();
}

// Hàm hiển thị modal list
function showListModal() {
    const listModal = new bootstrap.Modal(document.getElementById('listModal'));
    listModal.show();
}

// Khởi tạo Swiper
const swiper = new Swiper(".mySwiper", {
    slidesPerView: 6,
    spaceBetween: 20,
    loop: true,
    speed: 500,
    grabCursor: true,
    allowTouchMove: true,
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },
    pagination: {
        el: '.swiper-pagination',
        clickable: true,
    },
    autoplay: {
        delay: 3000,
        disableOnInteraction: false,
        pauseOnMouseEnter: true
    },
    effect: "slide",
    watchSlidesProgress: true,
    loopAdditionalSlides: 5,
    breakpoints: {
        0: { slidesPerView: 2 },
        576: { slidesPerView: 3 },
        768: { slidesPerView: 4 },
        992: { slidesPerView: 5 },
        1200: { slidesPerView: 6 },
    },
});
    function confirmClick(element) {
        if (!element.dataset.clicked) {
            element.dataset.clicked = "true";
            alert("Nhấn lần nữa để tiếp tục!");
            return false; 
        }
        return true;
    }


function setupMovieSlider() {
    document.querySelectorAll('.movie-thumbnail').forEach(thumb => {
        thumb.addEventListener('click', function() {
            // Lấy URL ảnh từ thuộc tính data
            const posterUrl = this.getAttribute('data-poster-url');
            
            // Thay đổi ảnh lớn
            const mainPoster = document.getElementById('main-movie-poster');
            if (mainPoster) {
                mainPoster.src = posterUrl;
                
                // Thêm hiệu ứng mờ trong 0.3s
                mainPoster.style.opacity = 0;
                setTimeout(() => {
                    mainPoster.style.opacity = 1;
                }, 300);
            }
        });
    });
}

// Chờ DOM tải xong
document.addEventListener('DOMContentLoaded', setupMovieSlider);