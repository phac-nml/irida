import React from "react";
import { Typography } from "antd";
import { useLaunchState } from "./launch-context";

/**
 * React component to display the details of a pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchPageHeader() {
  const { pipeline } = useLaunchState();
  return (
    <>
      <Typography.Title>{pipeline.name}</Typography.Title>
      <Typography.Paragraph>{pipeline.description}</Typography.Paragraph>
    </>
  );
}
