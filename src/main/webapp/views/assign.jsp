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
<h1>Change category of product: <strong>${product.productName}</strong> to another category</h1>

<form:form method="POST" action="/Products/updateProduct/${product.getId()}" modelAttribute="product" enctype="multipart/form-data">
    <form:input type="text" value="${product.getProductName()}" placeholder="enter product name" path="productName"/><br>
    <form:errors path="productName" cssClass="error"/>
    <form:input type="number" value="${product.getPrice()}" placeholder="enter product price" path="price"/><br><br>
    <form:errors path="price" cssClass="error"/>
    <form:input type="text" value="${product.getDescription()}" placeholder="enter product description" path="description"/><br><br>
    <form:errors path="description" cssClass="error"/>

    <form:select path="categoryID">
        <c:forEach var="category" items="${categories}">
            <form:option value="${category.categoryID}">
                ${category.categoryName}
            </form:option>
        </c:forEach>
    </form:select>
    <label>Upload Image:</label>
    <input type="file" name="imageFile" accept="image/*"/><br><br>

    <input type="submit" value="Update"/>
</form:form>
<form:form method="POST" action="/Products/deleteProduct/${product.getId()}" onsubmit="return confirm('are you sure')? true: false">
    <input type="submit" value="Delete">
</form:form>

</body>
</html>
