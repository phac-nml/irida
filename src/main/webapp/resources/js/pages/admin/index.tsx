import React from "react";
import { render } from "react-dom";
import { BrowserRouter } from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import Admin from "./components/Admin";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

render(
  <BrowserRouter>
    <Admin />
  </BrowserRouter>,
  document.getElementById("root")
);
