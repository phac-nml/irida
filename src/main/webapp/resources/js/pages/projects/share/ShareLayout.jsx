import { Space } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { ShareButton } from "./ShareButton";
import { ShareNoSamples } from "./ShareNoSamples";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";

export function ShareLayout() {
  const { originalSamples, currentProject } = useSelector(
    (state) => state.shareReducer
  );

  console.log(originalSamples);

  /*
  1. No Samples - this would be if the user came to this page from anything
  other than the share samples link.
   */
  const NO_SAMPLES =
    typeof originalSamples === "undefined" || originalSamples.length === 0;

  if (NO_SAMPLES) {
    return <ShareNoSamples />;
  }

  return (
    <Space direction="vertical" style={{ display: "block" }} size="large">
      <ShareProject />
      <ShareSamples />
      <ShareButton />
    </Space>
  );
}
