import React from "react";
import { LaunchProvider } from "./launch-context";
import { LaunchContent } from "./LaunchContent";

export function PipelineLaunchPage({ pipelineId }) {
  return (
    <LaunchProvider pipelineId={pipelineId}>
      <LaunchContent />
    </LaunchProvider>
  );
}
