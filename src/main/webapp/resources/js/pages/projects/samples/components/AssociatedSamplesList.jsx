import React from "react";
import { WarningOutlined } from "@ant-design/icons";
import { List, Space, Typography } from "antd";

/**
 * React Element to render a list of associated samples.  Use this when they
 * cannot be used in the requested action (e.g. remove).
 * @param {array} associatedSamples - list of samples belonging to associated projects
 * @returns {JSX.Element}
 * @constructor
 */
export default function AssociatedSamplesList({ associatedSamples }) {
  return (
    <List
      size="small"
      bordered
      header={
        <Space>
          <WarningOutlined />
          <Typography.Text>
            {i18n("AssociatedSamplesList.header")}
          </Typography.Text>
        </Space>
      }
      dataSource={associatedSamples}
      renderItem={(sample) => (
        <List.Item>
          <List.Item.Meta title={sample.sampleName} />
        </List.Item>
      )}
    />
  );
}
