import React, { lazy, Suspense } from "react";

const SampleDetails = lazy(() => import("./SampleDetails"));

export function SampleDetailsLoader() {
  return (
    <Suspense fallback={<span />}>
      <SampleDetails />
    </Suspense>
  );
}
