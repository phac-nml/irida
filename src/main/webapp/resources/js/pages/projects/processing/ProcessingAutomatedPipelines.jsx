import React from "react";
import { AutomatedPipelineHeader } from "./AutomatedPipelineHeader";
import { AnalysisTemplates } from "./AnalysisTemplates";

export function ProcessingAutomatedPipelines({ projectId }) {
  return (
    <section>
      <AutomatedPipelineHeader projectId={projectId} />
      <AnalysisTemplates projectId={projectId} />
    </section>
  );
}
