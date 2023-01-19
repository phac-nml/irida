import React from "react";
import { LockTwoTone } from "@ant-design/icons";
import { Popover, Space } from "antd";
import { ProjectSample } from "../../../../redux/endpoints/project-samples";

/**
 * React component to render any icons onto the sample listing table that
 * give extra information about the sample.
 * @param {object} sample
 * @returns {JSX.Element}
 * @constructor
 */
export default function SampleIcons({
  sample,
}: {
  sample: ProjectSample;
}): JSX.Element {
  return (
    <Space size="small">
      {!sample.owner && (
        <Popover
          content={i18n("SampleIcon.locked")}
          placement="right"
          trigger="hover"
        >
          <LockTwoTone twoToneColor={"#f5222d"} />
        </Popover>
      )}
    </Space>
  );
}
