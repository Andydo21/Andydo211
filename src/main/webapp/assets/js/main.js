
// Xử lý navbar khi cuộn
window.addEventListener("scroll", function () {
  const navbar = document.querySelector(".navbar");
  if (window.scrollY > 50) {
    navbar.classList.add("scrolled");
  } else {
    navbar.classList.remove("scrolled");
  }
});

// Movie Banner Slider
const movieContents = document.querySelectorAll(".movie-content");
const dots = document.querySelectorAll(".movie-dots .dot");
let currentSlide = 0;
let slideInterval;

function showSlide(index) {
  movieContents.forEach((content) => content.classList.remove("active"));
  dots.forEach((dot) => dot.classList.remove("active"));

  movieContents[index].classList.add("active");
  dots[index].classList.add("active");
}

// Click on dots
dots.forEach((dot, index) => {
  dot.addEventListener("click", () => {
    currentSlide = index;
    showSlide(currentSlide);
  });
});

// Auto slide functions
function startSlideShow() {
  slideInterval = setInterval(() => {
    currentSlide = (currentSlide + 1) % movieContents.length;
    showSlide(currentSlide);
  }, 5000);
}

function stopSlideShow() {
  clearInterval(slideInterval);
}

// Pause on hover
const slider = document.querySelector('.movie-slider');
slider.addEventListener('mouseenter', stopSlideShow);
slider.addEventListener('mouseleave', startSlideShow);

// Start auto slide
startSlideShow();

// Swiper initialization
document.querySelectorAll('.movie-poster-slider').forEach((section, index) => {
  const swiper = new Swiper(section.querySelector('.mySwiper'), {
    slidesPerView: "auto",
    spaceBetween: 20,
    navigation: {
      nextEl: section.querySelector('.swiper-button-next'),
      prevEl: section.querySelector('.swiper-button-prev'),
    },
  });
});