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

import { SPACE_MD } from "../../../styles/spacing";
import { Running } from "../../../components/icons/Running";

const Step = Steps.Step;

export function AnalysisSteps() {
  const { analysisContext } = useContext(AnalysisContext);
  return (
    <Steps
      current={
        analysisContext.isError
          ? stateMap[analysisContext.previousState]
          : stateMap[analysisContext.analysisState]
      }
      status={analysisContext.isError ? "error" : "finish"}
      style={{ paddingBottom: SPACE_MD, paddingTop: SPACE_MD }}
    >
      <Step
        title={i18n("AnalysisSteps.new")}
        icon={analysisContext.analysisState === "NEW" ? <Running /> : null}
      />
      <Step
        title={i18n("AnalysisSteps.preparing")}
        icon={
          analysisContext.analysisState === "PREPARING" ? <Running /> : null
        }
      />
      <Step
        title={i18n("AnalysisSteps.submitting")}
        icon={
          analysisContext.analysisState === "SUBMITTING" ? <Running /> : null
        }
      />
      <Step
        title={i18n("AnalysisSteps.running")}
        icon={analysisContext.analysisState === "RUNNING" ? <Running /> : null}
      />
      <Step
        title={i18n("AnalysisSteps.completing")}
        icon={
          analysisContext.analysisState === "COMPLETING" ? <Running /> : null
        }
      />
      <Step
        title={i18n("AnalysisSteps.completed")}
        icon={
          analysisContext.analysisState === "COMPLETED" ? <Success /> : null
        }
      />
    </Steps>
  );
}
