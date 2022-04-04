import React, { lazy, Suspense } from "react";

const MergeModal = lazy(() => import("./MergeModal"));

const VALID_MIN_COUNT = 2; // Bare minimum amount of samples to merge

export default function MergeSamples({ children, samples }) {
  const [visible, setVisible] = React.useState(false);

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      {visible && (
        <Suspense fallback={<span />}>
          <MergeModal
            visible={visible}
            onOk={() => setVisible(false)}
            samples={samples}
          />
        </Suspense>
      )}
    </>
  );
}
