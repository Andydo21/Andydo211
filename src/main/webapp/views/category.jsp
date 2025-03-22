<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Category List</title>
</head>
<body>
  <h1>hello andy</h1>
  <table>
    <tr>
      <th>ID</th>
      <th>Name</th>
      <th>Description</th>
      <th>Actions</th>
    </tr>
    <c:forEach var="category" items="${categories}">
      <tr>
        <td>${category.getCategoryID()}</td>
        <td>${category.getCategoryName()}</td>
        <td>${category.getDescription()}</td>
        <td><a href="Products/getProductsByCategoryID/${category.getCategoryID()}">show more</a></td>
      </tr>
    </c:forEach>
  </table>
  <a href="Products/insertProduct">Insert product</a>
</body>
</html>