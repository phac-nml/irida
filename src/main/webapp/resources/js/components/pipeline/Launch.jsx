import React, { Suspense, useState } from "react";
import { Button } from "antd";
import { PipelineLaunchModal } from "./PipelineLaunchModal";

const PipelineDetailsModal = React.lazy(() => import("./PipelineDetailsModal"));

export function Launch({ pipelineId, automated }) {
  const [visible, setVisible] = useState(false);

  return (
    <>
      <Button onClick={() => setVisible(true)} loading={visible}>
        LAUNCH
      </Button>
      {visible ? (
        <Suspense fallback={null}>
          <PipelineLaunchModal
            visible={visible}
            pipelineId={pipelineId}
            automated={automated}
            onCancel={() => setVisible(false)}
          />
        </Suspense>
      ) : null}
    </>
  );
}
