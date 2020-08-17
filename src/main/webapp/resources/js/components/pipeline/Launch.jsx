import React, { Suspense, useState } from "react";
import { PipelineLaunchModal } from "./PipelineLaunchModal";
import { LaunchProvider } from "./launch-context";

const PipelineDetailsModal = React.lazy(() => import("./PipelineDetailsModal"));

export function Launch({ pipelineId, automated }) {
  const [visible, setVisible] = useState(false);

  return (
    <>
      {visible ? (
        <Suspense fallback={null}>
          <LaunchProvider pipelineId={pipelineId} automated={automated}>
            <PipelineLaunchModal
              visible={visible}
              onCancel={() => setVisible(false)}
            />
          </LaunchProvider>
        </Suspense>
      ) : null}
    </>
  );
}
