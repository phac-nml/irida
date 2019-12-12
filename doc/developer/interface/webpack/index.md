---
layout: default
---

Webpack
=======

[Webpack](https://webpack.js.org) is used to bundle front end assets, including JavaScript, CSS, SCSS, images and fonts into static assets to be consumed.  It is also an integral part of the IRIDA internationalization system. A webpack plugin, `i18nThymeleafWebpackPlugin`, developed in house is used to capture strings that need to be internationalized for each webpack entry, combining them into an html Thymeleaf template that will get processed and translated at runtime.

Due to the nature of webpack code splitting and file chunking to optimize file size, an indeterminate amount of resource files (css and js) can be created for each entry.  Once this process is complete, webpack creates a `manifest.json` file (portion shown below) that contains a json object for teach of the entries that shows all assets that are required for css, js, and html (internationalizations):

```json
{
  "entrypoints": {
      "cart": {
            "js": [
              "js/runtime-b9a6010595ef63e39154.js",
              "js/3-637529209148847ca8e7.chunk.js",
              "js/7-00e10fd8e2b33536721c.chunk.js",
              "js/cart-e1ac16e87376339d571b.chunk.js"
            ],
            "css": [
              "css/3-44fb0174673e9add0d7e.css",
              "css/cart-6edf0537a2f776205516.css"
            ],
            "html": [
              "i18n/cart.html"
            ]
          },
      ...
  }
}
```
  
We created a Thymeleaf dialect called `WebpackerDialect` which will look for the specific entry name based on html elements on the page.  It then parses this `manifest.json` file and dynamically creates all the CSS `link` tags and JavaScript `script` tags.   For each JavaScript entry, it will check to see if there are any translations to be loaded onto the page (based on if there is an `html` attribute on the manifest's entry object), and if so, will inject them onto the page immediate prior to the new `script` tag for that entry.

#### CSS

Use the following html tag in the `head` element of the html page instead of a `link` with a path to the css.  Thymeleaf will process this tag

```html
<webpacker:css entry="entryname" />
```

#### JS

Use the following html tag to add the entry's javascript files and translations.

```html
<webpacker:js entry="entryname" />
```

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
