import { Avatar, Button, List } from "antd";
import React, { useCallback } from "react";

import { LockTwoTone } from "@ant-design/icons";
import { SelectedSample } from "../../../../types/irida";
import { SampleDetailViewer } from "../../../samples/SampleDetailViewer";

type LockedSamplesListParams = {
  locked: Array<SelectedSample> | undefined;
};

/**
 * React Element to render a list of locked samples.  Use this when they
 * cannot be used in the requested action (e.g. remove).
 * @param locked - list of samples that are locked from modification
 */
export default function LockedSamplesList({
  locked,
}: LockedSamplesListParams): JSX.Element | null {
  const renderItem = useCallback(
    (item: SelectedSample) => (
      <List.Item>
        <List.Item.Meta
          avatar={
            <Avatar
              icon={<LockTwoTone twoToneColor={"#f5222d"} />}
              style={{ backgroundColor: "transparent" }}
            />
          }
          title={
            <SampleDetailViewer sampleId={item.id} projectId={item.projectId}>
              <Button className="t-locked-name">{item.sampleName}</Button>
            </SampleDetailViewer>
          }
        />
      </List.Item>
    ),
    []
  );

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
