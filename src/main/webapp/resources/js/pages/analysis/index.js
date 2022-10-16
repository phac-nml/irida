import React from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { AnalysisProvider } from "../../contexts/AnalysisContext";
import { setBaseUrl } from "../../utilities/url-utilities";
import Analysis from "./Analysis";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

const root = createRoot(document.getElementById("root"));
root.render(
  <BrowserRouter>
    <AnalysisProvider>
      <Analysis />
    </AnalysisProvider>
  </BrowserRouter>
);
