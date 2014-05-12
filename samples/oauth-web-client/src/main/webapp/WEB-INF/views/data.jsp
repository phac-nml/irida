<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<body>
<h1>Demo page displaying project list for ${service}</h1>
<table>
<c:forEach items="${data}" var="data">
    <tr>      
        <td>${data.name}</td>
        <td><a href="/remote/${apiId}/projects/${data.id}">${data.id}</a></td>
    </tr>
</c:forEach>
</table>
</body>
</html>
