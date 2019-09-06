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
import { Steps } from "antd";
import { AnalysisContext, stateMap } from "../../../contexts/AnalysisContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { SPACE_MD } from "../../../styles/spacing";

const Step = Steps.Step;

export function AnalysisSteps() {
  const { analysisContext } = useContext(AnalysisContext);
  return (
    <>
      <Steps
        current={stateMap[analysisContext.analysisState]}
        status={analysisContext.isError ? "error" : "finish"}
        style={{ paddingBottom: SPACE_MD }}
      >
        <Step title={getI18N("analysis.state.NEW")} />
        <Step title={getI18N("analysis.state.PREPARING")} />
        <Step title={getI18N("analysis.state.SUBMITTING")} />
        <Step title={getI18N("analysis.state.RUNNING")} />
        <Step title={getI18N("analysis.state.COMPLETING")} />
        <Step title={getI18N("analysis.state.COMPLETED")} />
      </Steps>
    </>
  );
}
