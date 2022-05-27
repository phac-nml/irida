import * as React from "react";
import { Popover, Space, Typography } from "antd";
import { QuestionCircleOutlined } from "@ant-design/icons";
import { grey } from "@ant-design/colors";

export default function TextWithHelpPopover({ text, help }) {
  return (
    <Space size="small">
      <Typography.Text>{text}</Typography.Text>
      <Popover
        content={
          <div
            dangerouslySetInnerHTML={{
              __html: help,
            }}
          />
        }
      >
        <QuestionCircleOutlined style={{ color: grey[2] }} />
      </Popover>
    </Space>
  );
}
