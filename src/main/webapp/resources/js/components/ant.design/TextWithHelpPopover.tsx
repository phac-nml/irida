import * as React from "react";
import { Popover, Space, Typography } from "antd";
import { QuestionCircleOutlined } from "@ant-design/icons";

type Props = {
  text: JSX.Element | string;
  help: string;
};

export default function TextWithHelpPopover({
  text,
  help,
}: Props): JSX.Element {
  return (
    <Space size="small">
      <Typography.Text>{text}</Typography.Text>
      <Popover content={<div
          dangerouslySetInnerHTML={{
              __html: help,
          }}
      />}>
        <QuestionCircleOutlined style={{ color: `var(--grey-7)` }} />
      </Popover>
    </Space>
  );
}
