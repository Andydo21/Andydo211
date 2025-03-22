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
    <th>Avatar</th>
    <th>ID</th>
    <th>Name</th>
    <th>Category</th>
    <th>price</th>
    <th>Desctiption</th>
    <th>Action</th>
  </tr>
  <c:forEach var="product" items="${products}">
    <tr>
      <td>
        <img src="${product.getImageUrl()}" alt="Product Image" width="100">
      </td>

      <td>${product.getId()}</td>
      <td>${product.getProductName()}</td>
      <td>${product.getCategoryID()}</td>
      <td>${product.getFormattedPrice()}</td>
      <td>${product.getDescription()}</td>
      <td><a href="../../Products/movie/${product.getId()}">Update Product</a></td>
    </tr>
  </c:forEach>
</table>
<a href="/categories">
  Go back
</a>
</body>

