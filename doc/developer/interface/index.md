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
- Compilation of SCSS/CSS using `PostCSS` and `autoprefixr`
- CSS extracted from JS/JSX files and put in seperate css files to be loaded onto page.
- Extraction of internationalization strings (more on this later)
- Asset manifest created stating which files have been generated for which entry point.

Production Mode: `yarn build`
- Minimal source map
- File minification
- Bundle Chunking: Splits code into various bundles which can then me loaded on demand or in parallel. This makes for smaller bundle sizes and controls resource load prioritization. Bundle chunking also for code that is loaded on different pages be bundled together and cached by the browser.
- Hash code added to file name to allow for browser cache breaking.

Development Mode: `yarn start`
- **watch** mode set; any changes to JavaScript of CSS files will be automatically compiled
- Extensive source map
- No file minification

#### JavaScript

IRIDA uses the [babel-loader](https://webpack.js.org/loaders/babel-loader/#root) to transpile the latest versions of JavaScript to a version supported by browsers.
 
Current babel plugins for UI development:
- [Class Properties](https://babeljs.io/docs/en/babel-plugin-proposal-class-properties)
- [Export default from](https://babeljs.io/docs/en/babel-plugin-proposal-export-default-from) 
- [Optional Chaining](https://babeljs.io/docs/en/babel-plugin-proposal-optional-chaining)

