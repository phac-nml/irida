---
layout: default
---


Webpack
==========================================================

## Code Splitting

### Servlet Context Path
Since IRIDA can be hosted under any context path, this will not be known at compile time.  To get around this set the the webpack global variable `__webpack_public_path__` in the entry point to the bundle. 

See [Webpack Public Path > On the fly](https://webpack.js.org/guides/public-path/#on-the-fly) for more information.

Just add the following snippet to your bundle entry point:

```javascript
/*
WEBPACK PUBLIC PATH
 */
__webpack_public_path__ = `${window.TL.BASE_URL}resources/dist/`;
```
* `window.TL.BASE_URL` is found on on the base template for all pages: `src/main/webapp/pages/template/page.html`, and is passed the servlet context path via the Thymeleaf templating engine.
* `resources/dist` is the path that webpack bundles all js and css files into.
