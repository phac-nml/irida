import React from "react";
import { useLaunchState } from "./launch-context";
import { Modal, Tabs } from "antd";
import { PipelineDetails } from "./PipelineDetails";
import { ReferenceFiles } from "../reference/ReferenceFiles";

const { TabPane } = Tabs;

export function PipelineLaunchModal({ visible = false, onCancel }) {
  const { step, requiresReference, files } = useLaunchState();

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
          FUN PARAMETERS HERE
        </TabPane>
      </Tabs>
    </Modal>
  );
}
