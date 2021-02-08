import React from "react";
import { AutomatedPipelineHeader } from "./AutomatedPipelineHeader";
import { AnalysisTemplates } from "./AnalysisTemplates";
import { ProcessingPriorities } from "./ProcessingPriorities";

export function ProcessingAutomatedPipelines({ projectId, canManage }) {
  return (
    <section>
      <AutomatedPipelineHeader canMange={canManage} projectId={projectId} />
      {canManage && <ProcessingPriorities projectId={projectId} />}
      <AnalysisTemplates canManage={canManage} projectId={projectId} />
    </section>
  );
}
