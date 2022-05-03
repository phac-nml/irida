import React from "react";
import { render } from "react-dom";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Provider } from "react-redux";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ContentLoading } from "../../components/loader";
import store from "./store";

const SequencingRunListPage = React.lazy(() =>
  import("./components/SequencingRunListPage")
);
const SequencingRunDetailsPage = React.lazy(() =>
  import("./components/SequencingRunDetailsPage")
);

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
*/
__webpack_public_path__ = setBaseUrl(`/dist/`);

/**
 * React component that displays the sequencing runs pages.
 * @returns {*}
 * @constructor
 */
render(
  <Provider store={store}>
    <BrowserRouter basename={setBaseUrl("/sequencing-runs2")}>
      <React.Suspense fallback={<ContentLoading/>}>
        <Routes>
          <Route path="/" element={<SequencingRunListPage/>}/>
          <Route path="/:runId" element={<SequencingRunDetailsPage/>}/>
        </Routes>
      </React.Suspense>
    </BrowserRouter>
  </Provider>,
  document.getElementById("root")
);