import React, { useContext } from "react";
import PropTypes from "prop-types";
import { Steps } from "antd";
import { AnalysisContext } from '../../../state/AnalysisState'


const Step = Steps.Step;

export function AnalysisSteps() {
    const { state } = useContext(AnalysisContext);
    return (
      <>
        <Steps current={state.stateMap[state.analysisState]} status="finish" style={{paddingBottom: "15px"}}>
          <Step title="Queued" />
          <Step title="Preparing" />
          <Step title="Submitting" />
          <Step title="Running" />
          <Step title="Completing" />
          <Step title="Completed" />
        </Steps>
      </>
    );
}
