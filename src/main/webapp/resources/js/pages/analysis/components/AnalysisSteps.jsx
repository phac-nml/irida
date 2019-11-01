/*
 * This file displays the steps of the analysis
 * (Queued, Preparing, Submitting, Running,
 * Completing, Completed)
 */

/*
 * The following import statements makes available all the elements
 * required by the component
 */
import React, { useContext } from "react";
import { Icon, Steps } from "antd";
import { AnalysisContext, stateMap } from "../../../contexts/AnalysisContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { SPACE_MD } from "../../../styles/spacing";
import { Running } from "../../../components/icons/Running";

const Step = Steps.Step;

export function AnalysisSteps() {
  const { analysisContext } = useContext(AnalysisContext);
  return (
    <Steps
      current={stateMap[analysisContext.analysisState]}
      status={analysisContext.isError ? "error" : "finish"}
      style={{ paddingBottom: SPACE_MD, paddingTop: SPACE_MD }}
    >
      <Step
        title={getI18N("AnalysisSteps.new")}
        icon={analysisContext.analysisState === "NEW" ? <Running /> : null}
      />
      <Step
        title={getI18N("AnalysisSteps.preparing")}
        icon={
          analysisContext.analysisState === "PREPARING" ? <Running /> : null
        }
      />
      <Step
        title={getI18N("AnalysisSteps.submitting")}
        icon={
          analysisContext.analysisState === "SUBMITTING" ? <Running /> : null
        }
      />
      <Step
        title={getI18N("AnalysisSteps.running")}
        icon={analysisContext.analysisState === "RUNNING" ? <Running /> : null}
      />
      <Step
        title={getI18N("AnalysisSteps.completing")}
        icon={
          analysisContext.analysisState === "COMPLETING" ? <Running /> : null
        }
      />
      <Step
        title={getI18N("AnalysisSteps.completed")}
        icon={
          analysisContext.analysisState === "COMPLETED" ? <Success /> : null
        }
      />
    </Steps>
  );
}
