<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@9/swiper-bundle.min.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
    <link href="https://vjs.zencdn.net/8.10.0/video-js.css" rel="stylesheet" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css" />
    <style>
        body,
        p,
        h1,
        h2,
        h3,
        h4,
        h5,
        h6,
        span,
        a {
            color: white !important;
        }


        header.navbar {
            background-color: #111111 !important;
        }

    </style>

<body>
    <!-- Header/Navbar -->
    <header class="navbar navbar-expand-lg navbar-dark fixed-top">
        <div class="container-fluid px-5">
            <a class="navbar-brand fw-bold text-warning" href="${pageContext.request.contextPath}/home">
                NEW <span class="text-white">MOVIE</span>
            </a>

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
                        <a class="nav-link" href="${pageContext.request.contextPath}/mylist">Danh sách của tôi</a>
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
                        <div id="user-info">
                            <a class="nav-link" href="${pageContext.request.contextPath}/login"><i class="fas fa-user"></i></a>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </header>

    <!-- Movie Player Section -->
    <main class="pt-5">
        <div class="container-fluid p-0">
            <!-- Video Player -->
            <div class="video-player-container">
                <video id="my-video" class="video-js vjs-default-skin vjs-big-play-centered" 
                    controls preload="auto" width="100%" height="100%"
                    poster="${movie.thumbnail}"
                    data-setup='{
                        "fluid": true,
                        "playbackRates": [0.5, 1, 1.5, 2],
                        "controlBar": {
                            "children": [
                                "playToggle",
                                "volumePanel",
                                "currentTimeDisplay",
                                "timeDivider",
                                "durationDisplay",
                                "progressControl",
                                "playbackRateMenuButton",
                                "fullscreenToggle"
                            ]
                        }
                    }'>
                    <source src="${movie.videoUrl}" type="video/mp4" />
                    <source src="${movie.videoUrl}" type="video/webm" />
                </video>
            </div>
        </div>
    </main>

    <!-- Movie Intro -->
    <div class="container-fluid px-5 text-white mt-5">
        <div class="row">
            <!-- Cột trái -->
            <div class="col-md-8">
                <h4 class="fw-bold">${movie.title}</h4>
                <div class="text-muted">${movie.englishTitle}</div>

                <div class="d-flex align-items-center mb-2">
                    <span class="me-3">${movie.views} lượt xem</span>
                    <span class="text-warning fw-bold">${movie.rating} </span>
                </div>

                <div class="mb-2">
                    ${movie.year} |
                    ${movie.ageLimit} |
                    ${movie.country} |
                    ${movie.season} |
                    ${movie.quality}
                </div>

                <h6 class="fw-bold">${movie.episodeTitle}</h6>
                <p class="text-muted">${movie.description}</p>
            </div>

            <!-- Cột phải -->
            <div class="col-md-4">
                <div class="d-flex mb-3">
                    <span class="me-3">${movie.likes}</span>
                    <span class="me-3"><a href="#chatSection" class="text-white text-decoration-none">Bình luận 💬</a></span>
                    <button class="btn btn-outline-light btn-sm" onclick="copyShareLink()">Chia sẻ</button>
                </div>

                <div><strong>Diễn viên:</strong> ${movie.actors}</div>
                <div><strong>Đạo diễn:</strong> ${movie.directors}</div>
                <div><strong>Thể loại:</strong> ${movie.genre}</div>
            </div>
        </div>
    </div>


    <!-- Episode -->
    <section class="movie-poster-slider">
        <h2>DANH SÁCH TẬP PHIM</h2>
        <div class="container-fluid">
            <div class="swiper mySwiper">
                <div class="swiper-wrapper">
                    <c:forEach var="episode" items="${episodes}">
                        <div class="swiper-slide">
                            <div class="movie-poster">
                                <img src="${episode.thumbnail}" alt="${episode.title}" />
                                <div class="episode-badge">${episode.title}</div>
                                <div class="update-badge">${episode.duration}</div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <div class="swiper-button-next"></div>
                <div class="swiper-button-prev"></div>
            </div>
        </div>
    </section>

    <!-- Đề xuất -->
    <section class="movie-poster-slider">
        <h2>ĐỀ XUẤT CHO BẠN</h2>
        <div class="container-fluid">
            <div class="swiper mySwiper">
                <div class="swiper-wrapper">
                    <c:forEach var="movie" items="${suggestedMovies}">
                        <div class="swiper-slide">
                            <div class="movie-poster">
                                <img src="${movie.thumbnail}" alt="${movie.title}" />
                                <div class="episode-badge">${movie.title}</div>
                                <div class="update-badge">${movie.duration}</div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <div class="swiper-button-next"></div>
                <div class="swiper-button-prev"></div>
            </div>
        </div>
    </section>

    <!-- Chat Section -->
    <section id="chatSection" class="chat-section py-5" style="background-color: #1a1a1a;">
        <div class="container-fluid px-5">
            <h2 class="text-white mb-4">Bình luận</h2>
            
            <!-- Danh sách bình luận -->
            <div class="chat-messages mb-5" style="max-height: 600px; overflow-y: auto;">
                <c:forEach var="comment" items="${comments}">
                    <div class="d-flex gap-3 mb-4">
                        <img src="${comment.userAvatar}" class="rounded-circle" width="50" height="50">
                        <div>
                            <div class="d-flex gap-2 align-items-center">
                                <h5 class="text-white mb-0">${comment.userName}</h5>
                                <small class="text-muted">${comment.timestamp}</small>
                            </div>
                            <p class="text-white mt-2 mb-0">${comment.content}</p>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Form bình luận -->
            <div class="comment-form-container">
                <form:form action="${pageContext.request.contextPath}/movie/comment" method="post" modelAttribute="commentForm">
                    <div class="d-flex gap-3">
                        <img src="${currentUser.avatar}" class="rounded-circle" width="50" height="50">
                        <div class="flex-grow-1">
                            <form:textarea path="content" class="form-control bg-dark text-white border-0" 
                                style="resize: none;" rows="3" placeholder="Viết bình luận..." />
                            <div class="text-end mt-3">
                                <form:hidden path="movieId" value="${movie.id}" />
                                <button type="submit" class="btn btn-primary px-5">
                                    Gửi
                                </button>
                            </div>
                        </div>
                    </div>
                </form:form>
            </div>
        </div>
    </section>

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
</body>
<script>
    

    // Nút Share
    function copyShareLink() {
        const url = window.location.href;
        navigator.clipboard.writeText(url).then(() => {
            alert("Đã sao chép liên kết!");
        }).catch(err => {
            console.error("Lỗi khi sao chép liên kết:", err);
        });
    }
</script>
<script src="https://vjs.zencdn.net/8.10.0/video.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/swiper/swiper-bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

</html>