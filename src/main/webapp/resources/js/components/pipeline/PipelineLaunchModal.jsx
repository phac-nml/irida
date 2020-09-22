import React from "react";
import { useLaunchState } from "../pipeline-launch-old/launch-context";
import { Modal, Tabs } from "antd";
import { PipelineDetails } from "../../pages/pipeline-launch/PipelineDetails";
import { ReferenceFiles } from "../reference/ReferenceFiles";
import { PipelineParameters } from "../pipeline-launch-old/PipelineParameters";

const { TabPane } = Tabs;

export function PipelineLaunchModal({ visible = false, onCancel }) {
  const { step, requiresReference, files, parameters } = useLaunchState();

  return (
    <Modal
      title={"Launch the damn pipeline already"}
      visible={visible}
      onCancel={onCancel}
      width={800}
      okText={"LAUNCH THE PIPELINE!"}
    >
      <Tabs defaultActiveKey={1}>
        <TabPane tab={"Pipeline Description"} key={1}>
          <PipelineDetails />
        </TabPane>
        {requiresReference ? (
          <TabPane tab={"Reference Files"}>
            <ReferenceFiles files={files} />
            {/*<UploadReferenceFile afterReferenceUpload={}*/}
          </TabPane>
        ) : null}
        <TabPane tab={"PARAMETERS"} key={3}>
          <PipelineParameters parameters={parameters} />
        </TabPane>
      </Tabs>
    </Modal>
  );
}
