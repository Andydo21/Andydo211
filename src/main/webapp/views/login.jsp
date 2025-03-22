<!-- login.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <title>Login</title>
</head>
<body>
<h2>Login</h2>
<form th:action="@{/Auth/login}" method="post">
  <label>Username:</label>
  <input type="text" name="username" required>
  <label>Password:</label>
  <input type="password" name="password" required>
  <button type="submit">Login</button>
</form>
<p th:if="${error}" th:text="${error}" style="color: red;"></p>
</body>
</html>
