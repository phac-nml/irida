import React from "react";
import { render } from "react-dom";
import { BrowserRouter } from "react-router-dom";
import { AnalysisProvider } from "../../../contexts/AnalysisContext";
import { setBaseUrl } from "../../../utilities/url-utilities";
import AdvancedPhylo from "./AdvancedPhylo";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

render(
  <BrowserRouter>
    <AnalysisProvider>
      <AdvancedPhylo />
    </AnalysisProvider>
  </BrowserRouter>,
  document.getElementById("root")
);

