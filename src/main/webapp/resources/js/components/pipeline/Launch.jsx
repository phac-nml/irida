import React, { Suspense, useState } from "react";
import { PipelineLaunchModal } from "./PipelineLaunchModal";
import { LaunchProvider } from "../pipeline-launch/launch-context";

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
