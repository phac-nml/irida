import React from "react";
import { LockTwoTone } from "@ant-design/icons";
import { Avatar, List } from "antd";
import { red6 } from "../../../../styles/colors";

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
                size="small"
                style={{ backgroundColor: "transparent" }}
              />
            }
            title={sample.sampleName}
          />
        </List.Item>
      )}
    />
  );
}
