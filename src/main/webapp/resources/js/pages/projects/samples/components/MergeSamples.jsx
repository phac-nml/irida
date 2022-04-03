import React, { lazy, Suspense } from "react";

const MergeModal = lazy(() => import("./MergeModal"));

export default function MergeSamples({ children, samples }) {
  const [visible, setVisible] = React.useState(false);
  const samplesLength = Object.keys(samples).length;
  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
        disabled: samplesLength !== 2
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
