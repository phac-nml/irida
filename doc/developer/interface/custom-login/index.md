---
layout: default
---


Custom Login Page
==========================================================

The default landing / login page for IRIDA can be replaced by an institution without modifying the existing one.

Create a html file called `login.html` on your files system, and save it to the path `/etc/irida/templates/`;

```properties
# Overwritten UI templates
ui.templates=/etc/irida/templates/
```

### Use IRIDA base login form.

IRIDA uses a React component as its login form and can be added to any template by adding:

 * CSS (`/dist/css/login.bundle.css`) and JS (`/dist/js/login.bundle.js`) files.
 * A `head` > `script` tag with a global `PAGE` variable containing: `BASE_URL: /*[[@{"/"}]]*/ "/"` as an attribute.
 * The div with id `login-root` should be placed on the dom element that you want React to render the login form.

#### Example

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <link rel="stylesheet" th:href="@{/dist/css/login.bundle.css}" />
    <script th:inline="javascript">
      window.PAGE = {
        BASE_URL: /*[[@{"/"}]]*/ "/"
      };
    </script>
  </head>
  <body>
    <div id="login-root"></div>
    <script th:src="@{/dist/js/login.bundle.js}"></script>
  </body>
</html>
```

#### Example of Custom login page

[Source code can be found here.](./custom-login.html.md)

![Custom Login Page](images/irida-custom-login.png)

### Create a custom login form

Login form basic skeleton:

```html
<form method="post" th:action="@{/login}">
    <div>
      <label for="name" th:text="#{LoginPage.username}">Username</label>
      <input id="name" name="username" type="text">
    </div>
    <div>
      <label for="password" th:text="#{LoginPage.password}">Password</label>
      <input id="password" name="password" type="password" >
    </div>
    <button type="submit" th:text="#{LoginPage.submit}">Login</button>
    <div>
      <a th:href="@{/password_reset}" th:text="#{LoginPage.forgot}">Forgot Password</a>
      <a th:href="@{/password_reset/activate}" th:text="#{LoginPage.activate}">Activate Account</a>
    </div>
  </form>
```

This will successfully log an authorized user into the IRIDA instance.  If the credentials are incorrect, the url will redirect to `/login?error=true` which can be captured with JavaScript and disply an error message to the user.