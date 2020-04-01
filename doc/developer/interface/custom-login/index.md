---
layout: default
---


Custom Login Page
==========================================================

The default landing / login page for IRIDA can be replaced by an institution without modifying the existing one.

Create a html file called `login.html` on your files system.  The default path IRIDA will check is `/etc/irida/templates`, but can be configured in the configuration file `src/main/resources/configuration.properties` by updating the `ui.templates` property,

```properties
# Overwritten UI templates
ui.templates=/etc/irida/templates
```

The most minimal required code all login pages is:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet" th:href="@{/dist/css/login.bundle.css}" />
    <script th:inline="javascript">
        window.PAGE = {
          BASE_URL: /*[[@{"/"}]]*/ "/",
        };
    </script>
</head>
<body style="background-color: #1c2833">
    <div>
        <div id="login-root"></div>
        <script th:src="@{/dist/js/login.bundle.js}"></script>
    </div>
</body>
</html>


```