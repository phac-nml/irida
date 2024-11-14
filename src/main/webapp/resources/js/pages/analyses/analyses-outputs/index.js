import React from "react";
import { createRoot } from 'react-dom/client';
import UserSingleSampleAnalysisOutputs from "./components/UserSingleSampleAnalysisOutputs";
import { setBaseUrl } from "../../../utilities/url-utilities";
import store from "./store";
import { Provider } from "react-redux";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <Provider store={store}>
    <UserSingleSampleAnalysisOutputs />
  </Provider>
);
