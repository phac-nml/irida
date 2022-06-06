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

 * CSS (`login.css`) and JS (`login.js`) files.
 * A `head` > `script` tag with a global `PAGE` variable containing: `BASE_URL: /*[[@{"/"}]]*/ "/"` as an attribute.
 * The div with id `login-root` should be placed on the dom element that you want React to render the login form.

#### Adding custom resources

Images, CSS and JS files can be added into `/etc/irida/static` and then loaded onto the custom login page.  Ensure that the Thymeleaf attributes are added, for example:

Examples:

    * Image: `<img th:src="@{/static/myImage.png}" alt="A great picture or logo" >`
    * JS: `<script th:src="@{/static/myFile.js}"></script>`
    * CSS: `<link th:href="@{/static/myFile.css}" />`

#### Example

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <webpacker:css entry="login" />
    <script th:inline="javascript">
      window.TL = {
        _BASE_URL: /*[[@{/}]]*/ "/",
        emailConfigured: /*[[${emailConfigured}]]*/ false
      };
    </script>
  </head>
  <body>
    <div id="login-root"></div>
    <webpacker:js entry="login" />
  </body>
</html>
```

#### Example of Custom login page

[Code for a basic custom login page.](./basic-login.html.md)

![Custom Login Page](images/irida-basic-login.png)

[Code for a more involved login page with added CSS.](./custom-login.html.md)

![Custom Login Page](images/irida-custom-login.png)

