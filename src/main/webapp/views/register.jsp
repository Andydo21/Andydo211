<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Register</title>
</head>
<body>
<h2>Register</h2>
<form th:action="@{/Auth/register}" method="post">
    <label>Username:</label>
    <input type="text" name="username" required>
    <label>Password:</label>
    <input type="password" name="password" required>
    <label>Email:</label>
    <input type="email" name="email" required>
    <label>Phone:</label>
    <input type="text" name="phone" required>
    <label>Address:</label>
    <input type="text" name="address" required>
    <label>Role:</label>
    <select name="role">
        <option value="user">User</option>
        <option value="admin">Admin</option>
    </select>
    <button type="submit">Register</button>
</form>
<p th:if="${error}" th:text="${error}" style="color: red;"></p>
</body>
</html>
