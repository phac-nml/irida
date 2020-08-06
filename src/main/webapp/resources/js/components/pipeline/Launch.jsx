import React, { Suspense, useState } from "react";
import { Button } from "antd";

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
          <PipelineDetailsModal
            visible={visible}
            id={pipelineId}
            onCancel={() => setVisible(false)}
            automated={automated}
          />
        </Suspense>
      ) : null}
    </>
  );
}
