---
layout: default
title: "User Interface Development Documentation"
---

# User Interface Development Documentation

## HTML

**User interface components in IRIDA should be developed using ReactJS and the Ant Design component library** (for more information see below).

IRIDA depends upon the [Thymeleaf Template Engine](https://www.thymeleaf.org/) for internationalization and server root URLs.

## React JS

The IRIDA user interface is developed using the [React JS Framework](https://reactjs.org/), although there are legacy pages that still contain jQuery and AngularJS. **All new development for IRIDA is expected to be done in React**.

## Ant Design

IRIDA uses the design system [Ant Design](https://ant.design/), a [React JS](https://reactjs.org/) based component library.

## Webpack

- Compilation of JavaScript using `babel-loader`.
- Compilation of SCSS/CSS using `PostCSS` and `autoprefixr`.
- CSS extracted from JS/JSX files and put in seperate css files to be loaded onto page.
- Extraction of internationalization strings (more on this later).
- Asset manifest created stating which files have been generated for which entry point.

### Build Process:

#### Production Mode: `yarn build`
- Minimal source map
- File minification
- Bundle Chunking: Splits code into various bundles which can then me loaded on demand or in parallel. This makes for smaller bundle sizes and controls resource load prioritization. Bundle chunking also for code that is loaded on different pages be bundled together and cached by the browser.
- Hash code added to file name to allow for browser cache breaking.

#### Development Mode: `yarn start`
- **watch** mode set; any changes to JavaScript of CSS files will be automatically compiled
- Extensive source map
- No file minification

### JavaScript

IRIDA uses the [babel-loader](https://webpack.js.org/loaders/babel-loader/#root) to transpile the latest versions of JavaScript to a version supported by browsers.
 
Current babel plugins for UI development:
- [Class Properties](https://babeljs.io/docs/en/babel-plugin-proposal-class-properties)
- [Export default from](https://babeljs.io/docs/en/babel-plugin-proposal-export-default-from) 
- [Optional Chaining](https://babeljs.io/docs/en/babel-plugin-proposal-optional-chaining)

### CSS

IRIDA uses [PostCSS](https://postcss.org/) to transform CSS:
- Add vendor prefixes with [Autoprefixer](https://github.com/postcss/autoprefixer)
- Transform [LESS CSS](https://lesscss.org/) to CSS

IRIDA uses webpacks' [MiniCSSExtractPlugin](https://webpack.js.org/plugins/mini-css-extract-plugin/) to extract found in JS Files ito its own css file.

## Full Front End Build with Webpack and Thymeleaf

An "entry" is used by webpack to indicate the root JavaScript file for a page in IRIDA.  All entires for IRIDA are listed in `src/main/webapp/entries.js`.  This is a simple JavaScript exported object that contains key va pairs (key is the entry name, value is the path to the root file).

Example:
```javascript
module.exports = {
  dashboard: "./resources/js/pages/dashboard.js",
}
```

When webpack compiles all the assets found for that entry (CSS and JavaScript) it will output them in optimized chunks for faster loading and shared code between different entry points (e.g. code for displaying a modal window). This allows for faster loading between entries since this file would not need to be re-downloaded.

These compiled files change during development so it would become a futile effort to keep maintaining all the new links statically on their respective HTML pages.  To get around this, we have created a system connecting Webpack and Thymeleaf to allow the dynamic addition of these links to their HTML page at runtime.

### Custom HTML Tags

**Do not add CSS `link` and JavaScript `script` tags for any Webpack entry**