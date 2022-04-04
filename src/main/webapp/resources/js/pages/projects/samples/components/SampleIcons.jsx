import React from "react";
import { LockTwoTone } from "@ant-design/icons";
import { Popover, Space } from "antd";
import { red6 } from "../../../../styles/colors";

export default function SampleIcons({ sample }) {
  return (
    <Space size="small">
      {!Boolean(sample.owner) && (
        <Popover
          content={
            "You do not have ownership of this sample and cannot modified it."
          }
          placement="right"
          trigger="hover"
        >
          <LockTwoTone twoToneColor={red6} />
        </Popover>
      )}
    </Space>
  );
}
