import React from "react";
import { LockTwoTone } from "@ant-design/icons";
import { Avatar, Button, List } from "antd";
import { red6 } from "../../../../styles/colors";
import { SampleDetailViewer } from "../../../../components/samples/SampleDetailViewer";

/**
 * React Element to render a list of locked samples.  Use this when they
 * cannot be used in the requested action (e.g. remove).
 * @param {array} locked - list of samples that are locked from modification
 * @returns {JSX.Element}
 * @constructor
 */
export default function LockedSamplesList({ locked }) {
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
                <Button>{sample.sampleName || sample.name}</Button>
              </SampleDetailViewer>
            }
          />
        </List.Item>
      )}
    />
  );
}
