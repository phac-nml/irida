import React from "react";
import { LockOutlined } from "@ant-design/icons";
import { List, Space, Typography } from "antd";

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
      size="small"
      bordered
      header={
        <Space>
          <LockOutlined />
          <Typography.Text>{i18n("LockedSamplesList.header")}</Typography.Text>
        </Space>
      }
      dataSource={locked}
      renderItem={(sample) => (
        <List.Item>
          <List.Item.Meta title={sample.sampleName} />
        </List.Item>
      )}
    />
  );
}
