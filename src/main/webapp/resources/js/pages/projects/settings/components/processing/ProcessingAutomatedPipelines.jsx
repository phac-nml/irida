import React from "react";
import { AutomatedPipelineHeader } from "./AutomatedPipelineHeader";
import { AnalysisTemplates } from "./AnalysisTemplates";
import { ProcessingPriorities } from "./ProcessingPriorities";

/**
 * Display automated pipeline options for the current project
 *
 * @param {number} projectId - project identifier
 * @param {boolean} canManage - if the current user can manage the project
 * @returns {JSX.Element}
 * @constructor
 */
export function ProcessingAutomatedPipelines({ projectId, canManage }) {
  return (
    <section>
      <AutomatedPipelineHeader canMange={canManage} projectId={projectId} />
      {canManage && <ProcessingPriorities projectId={projectId} />}
      <AnalysisTemplates canManage={canManage} projectId={projectId} />
    </section>
  );
}
