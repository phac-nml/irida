import React from "react";
import { render } from "react-dom";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Provider } from "react-redux";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ContentLoading } from "../../components/loader";
import store from "./store";

const SequencingRunLayout = React.lazy(() =>
  import("./components/SequencingRunLayout")
);
const SequencingRunListPage = React.lazy(() =>
  import("./components/SequencingRunListPage")
);
const SequencingRunDetailsPage = React.lazy(() =>
  import("./components/SequencingRunDetailsPage")
);
const SequencingRunFilesPage = React.lazy(() =>
  import("./components/SequencingRunFilesPage")
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
      <React.Suspense fallback={<ContentLoading />}>
        <Routes>
          <Route path="/" element={<SequencingRunListPage />} />
          <Route path="/:runId" element={<SequencingRunLayout />}>
            <Route index element={<SequencingRunDetailsPage />} />
            <Route path="details" element={<SequencingRunDetailsPage />} />
            <Route path="files" element={<SequencingRunFilesPage />} />
          </Route>
        </Routes>
      </React.Suspense>
    </BrowserRouter>
  </Provider>,
  document.getElementById("root")
);