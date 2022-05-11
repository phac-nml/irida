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
    <div style="max-width: 800px; margin: auto">This is an example of a basic custom IRIDA login page.  You can add images, custom CSS and JavaScript, or anything else in these spaces to customize the login page for your institution.  You just need to be sure to keep the content from the <code>head</code> section above, the <code>login-root</code> div, and the <code>script</code> import for <code>login.bundle.js</code> below.</div>
    <div id="login-root"></div>
    <webpacker:js entry="login" />
  </body>
</html>
