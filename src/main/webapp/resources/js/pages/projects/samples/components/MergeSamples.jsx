import React, { lazy, Suspense } from "react";

const MergeModal = lazy(() => import("./MergeModal"));

const VALID_MIN_COUNT = 2; // Bare minimum amount of samples to merge

export default function MergeSamples({ children, samples, updateTable }) {
  const [visible, setVisible] = React.useState(false);

  const onComplete = () => {
    setVisible(false);
    updateTable();
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      {visible && (
        <Suspense fallback={<span />}>
          <MergeModal
            visible={visible}
            onComplete={onComplete}
            onCancel={() => setVisible(false)}
            samples={samples}
          />
        </Suspense>
      )}
    </>
  );
}
