import React, { Suspense, useState } from "react";

const SampleDetailsWrapper = React.lazy(
  () => import("./components/SampleDetailsWrapper")
);

export interface SampleDetailViewerProps {
  sampleId: number;
  projectId: number;
  displayActions?: boolean;
  children: React.ReactElement;
  refetchCart?: () => void;
}

/**
 * React component to provide redux store to sampledetailviewer
 * @param sampleId - identifier for a sample
 * @param projectId - identifier for a project
 * @param displayActions - Whether to display add to/remove from cart buttons. Displayed by default
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleDetailViewer({
  sampleId,
  projectId,
  displayActions = true,
  children,
  refetchCart,
}: SampleDetailViewerProps): JSX.Element {
  const [visible, setVisible] = useState(false);

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      {visible ? (
        <Suspense fallback={<div />}>
          <SampleDetailsWrapper
            sampleId={sampleId}
            projectId={projectId}
            displayActions={displayActions}
            refetchCart={refetchCart}
            setVisible={setVisible}
            visible={visible}
          />
        </Suspense>
      ) : null}
    </>
  );
}
