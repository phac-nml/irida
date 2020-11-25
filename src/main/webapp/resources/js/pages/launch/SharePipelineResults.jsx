import React from "react";
import { ShareResultsWithProjects } from "./share-results/ShareResultsWithProjects";
import { ShareResultsWithSamples } from "./share-results/ShareResultsWithSamples";
import { useLaunch } from "./launch-context";
import { Divider } from "antd";

export function SharePipelineResults() {
  const [{ canUpdateSamples }] = useLaunch();
  return (
    <>
      <ShareResultsWithProjects />
      {canUpdateSamples ? <ShareResultsWithSamples /> : null}
      <Divider />
    </>
  );
}
