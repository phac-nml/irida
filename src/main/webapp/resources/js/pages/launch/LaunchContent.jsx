import React from "react";
import { LaunchPageHeader } from "./LaunchPageHeader";
import { useLaunch } from "./launch-context";
import { LaunchForm } from "./LaunchForm";
import { Space } from "antd";
import { SPACE_LG } from "../../styles/spacing";

/**
 * React component to layout the content of the pipeline launch.
 * It will act as the top level logic controller.
 */
export function LaunchContent() {
  const [{ pipeline }] = useLaunch();

  return (
    <Space direction="vertical" style={{ width: `100%`, padding: SPACE_LG }}>
      <LaunchPageHeader pipeline={pipeline} />
      <LaunchForm />
    </Space>
  );
}
