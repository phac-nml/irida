import { LockTwoTone } from "@ant-design/icons";
import { Avatar, Button, List } from "antd";
import React from "react";
import type { SelectedSample } from "../../pages/projects/types";
import { red6 } from "../../styles/colors";
import { SampleDetailViewer } from "./SampleDetailViewer";

/**
 * React Element to render a list of locked samples.  Use this when they
 * cannot be used in the requested action (for example remove).
 * @param locked - list of samples that are locked from modification
 */
export default function LockedSamplesList({
  locked,
}: {
  locked: SelectedSample[];
}) {
  return (
    <List
      style={{ maxHeight: 400, overflowY: "auto" }}
      size="small"
      bordered
      dataSource={locked}
      renderItem={(sample) => (
        <List.Item>
          <List.Item.Meta
            avatar={
              <Avatar
                icon={<LockTwoTone twoToneColor={red6} />}
                style={{ backgroundColor: "transparent" }}
              />
            }
            title={
              <SampleDetailViewer
                sampleId={sample.id}
                projectId={sample.projectId}
              >
                <Button className="t-locked-name">{sample.sampleName}</Button>
              </SampleDetailViewer>
            }
          />
        </List.Item>
      )}
    />
  );
}
