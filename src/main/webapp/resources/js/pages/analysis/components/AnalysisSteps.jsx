import React, { useContext } from "react";
import { Steps } from "antd";
import { AnalysisContext } from "../../../state/AnalysisState";
import { getI18N } from "../../../utilities/i18n-utilties";

const Step = Steps.Step;

export function AnalysisSteps() {
  const { state } = useContext(AnalysisContext);
  return (
    <>
      <Steps
        current={state.stateMap[state.analysisState]}
        status="finish"
        style={{ paddingBottom: "15px" }}
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
