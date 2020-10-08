import React, { lazy, Suspense } from "react";
import { render } from "react-dom";
import { setBaseUrl } from "../utilities/url-utilities";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

const SampleMenuBar = lazy(() => import("../components/sample/SampleMenuBar"));

/**
 * React component to render the sample details page.
 * @returns {*}
 * @constructor
 */
function SamplePage() {
  return (
    <div>
      {window.PAGE.canManage ? (
        <Suspense fallback={<div style={{ height: 40 }} />}>
          <SampleMenuBar />
        </Suspense>
      ) : null}
    </div>
  );
}

render(<SamplePage />, document.querySelector("#sample-root"));
