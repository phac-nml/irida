<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<body>
<h1>Choose a remote service to read from:</h1>
<table>
<tr>
	<th>ID</th>
	<th>Description</th>
</tr>
<c:forEach items="${apiList}" var="api">
    <tr>
    	<td>${api.id}</td>
        <td><a href="/remote/${api.id}/projects">${api.description}</a></td>
    </tr>
</c:forEach>
</table>
</body>
</html>
