import React from "react";
import { useLaunchState } from "./launch-context";
import { Modal, Space } from "antd";
import { PipelineDetails } from "./PipelineDetails";
import { LaunchSteps } from "./LaunchSteps";
import { STEP_DETAILS, STEP_REFERENCE } from "./lauch-constants";

const CurrentStep = ({ current }) => {
  switch (current) {
    case STEP_DETAILS:
      return <PipelineDetails />;
    case STEP_REFERENCE:
      return <strong>REFERENCE FILES</strong>;
    default:
      return <h1>FUCK NOTHING</h1>;
  }
};

export function PipelineLaunchModal({ visible = false, onCancel }) {
  const { step } = useLaunchState();

  return (
    <Modal
      title={"Launch the damn pipeline already"}
      visible={visible}
      onCancel={onCancel}
      width={800}
      okText={"LAUNCH THE PIPELINE!"}
    >
      <Space direction="vertical" size="large" style={{ width: "100%" }}>
        <LaunchSteps />
        <CurrentStep current={step} />
      </Space>
    </Modal>
  );
}
