import React from "react";
import { LaunchProvider, useLaunchState } from "./launch-context";
import { Modal } from "antd";
import { PipelineDetails } from "./PipelineDetails";
import { LaunchSteps } from "./LaunchSteps";
import { STEP_DETAILS } from "./lauch-constants";

const CurrentStep = ({ current }) => {
  switch (current) {
    case STEP_DETAILS:
      return <PipelineDetails />;
    default:
      return <h1>FUCK NOTHING</h1>;
  }
};

export function PipelineLaunchModal({
  visible = false,
  pipelineId,
  automated,
  onCancel,
}) {
  const { step } = useLaunchState();

  return (
    <LaunchProvider pipelineId={pipelineId} automated={automated}>
      <Modal
        title={"Launch the damn pipeline already"}
        visible={visible}
        onCancel={onCancel}
        width={800}
        okText={"LAUNCH THE PIPELINE!"}
      >
        <LaunchSteps />
        <CurrentStep current={step} />
      </Modal>
    </LaunchProvider>
  );
}
