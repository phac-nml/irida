import React from "react";
import PropTypes from "prop-types";
import { Steps } from "antd";

const Step = Steps.Step;

export function AnalysisSteps() {
  return (
      <>
        <Steps current={5} status="finish" style={{paddingBottom: "15px"}}>
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
