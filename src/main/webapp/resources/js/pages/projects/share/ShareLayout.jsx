import { Result, Space } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { ShareButton } from "./ShareButton";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";

export function ShareLayout() {
  const { originalSamples, currentProject } = useSelector(
    (state) => state.shareReducer
  );

  /*
  1. No Samples - this would be if the user came to this page from anything
  other than the share samples link.
   */
  const NO_SAMPLES = originalSamples.length === 0;

  if (NO_SAMPLES) {
    return <Result status="warning" title={i18n("ShareSamples.no-samples")} />;
  }

  return (
    <Space direction="vertical" style={{ display: "block" }} size="large">
      <ShareProject />
      <ShareSamples />
      <ShareButton />
    </Space>
  );
}
