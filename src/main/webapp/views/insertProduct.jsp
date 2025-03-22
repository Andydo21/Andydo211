<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Category List</title>
    <style>
        .error{
            color: red;
        }
    </style>
</head>
<body>
<h1>Insert product <strong>${product.productName}</strong> to another category</h1>

<form:form method="POST" action="/Products/insertProduct" modelAttribute="product" enctype="multipart/form-data">
    <form:input type="text" placeholder="enter product name" path="productName"/><br><br>
    <form:errors path="productName" cssClass="error"/>
    <form:input type="number" placeholder="enter product price" path="price"/><br><br>
    <form:errors path="price" cssClass="error"/>
    <form:input type="text" placeholder="enter product description" path="description"/><br><br>
    <form:errors path="description" cssClass="error"/>
    <label>Upload Image:</label>
    <input type="file" name="imageFile" accept="image/*"/>
    <form:select path="categoryID">
        <c:forEach var="category" items="${category}">
            <form:option value="${category.categoryID}">
                ${category.categoryName}
            </form:option>
        </c:forEach>
    </form:select>
    <br>
    <br>
    <br>
    <p>${error}</p>
    <input type="submit" value="Update"/>
    <p>Categories: ${category}</p>
</form:form>

</body>
</html>
