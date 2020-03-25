import React, { lazy, Suspense } from "react";
import { render } from "react-dom";

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
