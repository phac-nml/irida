/**
 * @file Base file for the project samples page.
 */
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import ProjectSamples from "./components/ProjectSamples";
import { setBaseUrl } from "../../../utilities/url-utilities";
import store from "./store";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

render(
  <Provider store={store}>
    <ProjectSamples />
  </Provider>,
  document.getElementById("root")
);
