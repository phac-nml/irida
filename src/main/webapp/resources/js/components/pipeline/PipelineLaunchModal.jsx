import React from "react";
import { LaunchProvider } from "./launch-context";
import { Modal } from "antd";
import { PipelineDetails } from "./PipelineDetails";
import { setBaseUrl } from "../../utilities/url-utilities";

export function PipelineLaunchModal({
  visible = false,
  pipelineId,
  automated,
  onCancel,
}) {
  return (
    <LaunchProvider pipelineId={pipelineId} automated={automated}>
      <Modal
        title={"Launch the damn pipeline already"}
        visible={visible}
        onCancel={onCancel}
      >
        <PipelineDetails path={setBaseUrl(`/cart/pipelines/launch/:id`)} />
      </Modal>
    </LaunchProvider>
  );
}
