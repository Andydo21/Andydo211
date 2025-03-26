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