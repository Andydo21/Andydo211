<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Xem Phim</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>

<div class="container mt-5">
    <h2 class="text-center">Xem Phim</h2>
    <div class="d-flex justify-content-center">
        <video id="videoPlayer" class="w-75" controls>
            <source th:src="@{/video?filename=sample.mp4}" type="video/mp4">
            Trình duyệt không hỗ trợ video.
        </video>
    </div>
</div>

</body>
</html>

