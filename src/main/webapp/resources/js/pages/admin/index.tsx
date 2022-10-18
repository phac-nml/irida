import React from "react";
import { createRoot } from "react-dom/client";
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

const element = document.getElementById("root");
if (element) {
  const root = createRoot(element);
  root.render(
    <BrowserRouter>
      <Admin />
    </BrowserRouter>
  );
} else {
  throw new Error(`Cannot find DOM Element: #root`);
}
