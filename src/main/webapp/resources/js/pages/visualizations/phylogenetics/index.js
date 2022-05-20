import React from "react";
import { render } from "react-dom";
import store from './store';
import { Provider } from "react-redux";
import { setBaseUrl } from "../../../utilities/url-utilities";
import AdvancedPhylo from "./AdvancedPhylo";
import { AnalysisProvider } from "../../../contexts/AnalysisContext";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

render(
  <AnalysisProvider>
    <Provider store={store}>
      <AdvancedPhylo />
    </Provider>
  </AnalysisProvider>,
  document.getElementById("root")
);

