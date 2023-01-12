import { Avatar, Button, List } from "antd";
import React, { useCallback } from "react";

import { LockTwoTone } from "@ant-design/icons";
import { ProjectSample } from "../../../../redux/endpoints/project-samples";
import { SelectedSample } from "../../../../types/irida";
import { SampleDetailViewer } from "../../../samples/SampleDetailViewer";

type LockedSamplesListParams = {
  locked: Array<ProjectSample> | Array<SelectedSample> | undefined;
};

/**
 * Type guard to see if item is a project sample or selected sample,
 * making this component more reusable.
 * @param item
 */
function isItemProjectSample(
  item: ProjectSample | SelectedSample
): item is ProjectSample {
  return "project" in item;
}

/**
 * React Element to render a list of locked samples.  Use this when they
 * cannot be used in the requested action (e.g. remove).
 * @param locked - list of samples that are locked from modification
 */
export default function LockedSamplesList({
  locked,
}: LockedSamplesListParams): JSX.Element | null {
  const renderItem = useCallback((item: SelectedSample | ProjectSample) => {
    const isProjectSample = isItemProjectSample(item);

    return (
      <List.Item>
        <List.Item.Meta
          avatar={
            <Avatar
              icon={<LockTwoTone twoToneColor={"#f5222d"} />}
              style={{ backgroundColor: "transparent" }}
            />
          }
          title={
            <SampleDetailViewer
              sampleId={isProjectSample ? item.sample.id : item.id}
              projectId={isProjectSample ? item.project.id : item.projectId}
            >
              <Button className="t-locked-name">
                {isProjectSample ? item.sample.name : item.sampleName}
              </Button>
            </SampleDetailViewer>
          }
        />
      </List.Item>
    );
  }, []);

  return (
    <List
      style={{ maxHeight: 400, overflowY: "auto" }}
      size="small"
      bordered
      dataSource={locked}
      renderItem={renderItem}
    />
  );
}
