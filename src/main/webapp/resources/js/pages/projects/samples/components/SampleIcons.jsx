import React from "react";
import { LockTwoTone } from "@ant-design/icons";
import { Popover, Space } from "antd";
import { red6 } from "../../../../styles/colors";

export default function SampleIcons({ sample }) {
  return (
    <Space size="small">
      {!Boolean(sample.owner) && (
        <Popover
          content={i18n("SampleIcon.locked")}
          placement="right"
          trigger="hover"
        >
          <LockTwoTone twoToneColor={red6} />
        </Popover>
      )}
    </Space>
  );
}
