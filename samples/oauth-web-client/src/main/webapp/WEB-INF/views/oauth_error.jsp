<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<body>
    <h1>Demo OAuth2 Web Client</h1>
	<h3>An error has occurred while authorizing with OAuth2:</h3>
	<p>${exception.message}<p>
	<p><a href="/">Return to index</a></p>
</body>
</html>
