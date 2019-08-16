import React, { useContext } from "react";
import { Steps } from "antd";
import { AnalysisContext } from "../../../state/AnalysisState";
import { getI18N } from "../../../utilities/i18n-utilties";

const Step = Steps.Step;

export function AnalysisSteps() {
  const { context } = useContext(AnalysisContext);
  return (
    <>
      <Steps
        current={context.stateMap[context.analysisState]}
        status={context.isError ? "error" : "finish"}
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
