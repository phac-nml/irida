import React from "react";
import { AutomatedPipelineHeader } from "./AutomatedPipelineHeader";
import { AnalysisTemplates } from "./AnalysisTemplates";
import { ProcessingPriorities } from "./ProcessingPriorities";

export function ProcessingAutomatedPipelines({ projectId }) {
  return (
    <section>
      <AutomatedPipelineHeader projectId={projectId} />
      <ProcessingPriorities projectId={projectId} />
      <AnalysisTemplates projectId={projectId} />
    </section>
  );
}
