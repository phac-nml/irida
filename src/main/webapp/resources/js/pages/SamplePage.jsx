import React, { lazy, Suspense } from "react";
import { render } from "react-dom";

const SampleMenuBar = lazy(() => import("../components/sample/SampleMenuBar"));

function SamplePage() {
  // window.PAGE.__cm variable is for if the user can manage samples.
  return (
    <div>
      {window.PAGE.__cm ? (
        <Suspense fallback={<div style={{ height: 40 }} />}>
          <SampleMenuBar />
        </Suspense>
      ) : null}
    </div>
  );
}

render(<SamplePage />, document.querySelector("#sample-root"));
